package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.TreatiseCategoryDTO;
import com.cafa.pdf.core.dal.dao.IBaseDAO;
import com.cafa.pdf.core.dal.dao.TreatiseCategoryDAO;
import com.cafa.pdf.core.dal.entity.TreatiseCategory;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseCategorySaveReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseCategorySearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseCategoryUpdateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TreatiseCategoryService extends ABaseService<TreatiseCategory, Long> {

    private final TreatiseCategoryDAO treatiseCategoryDAO;

    @Autowired
    public TreatiseCategoryService(TreatiseCategoryDAO treatiseCategoryDAO) {
        this.treatiseCategoryDAO = treatiseCategoryDAO;
    }

    @Override
    protected IBaseDAO<TreatiseCategory, Long> getEntityDAO() {
        return treatiseCategoryDAO;
    }

    public Page<TreatiseCategoryDTO> findAll(BasicSearchReq basicSearchReq, TreatiseCategorySearchReq treatiseCategorySearchReq) {

        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        PageRequest pageRequest = new PageRequest(pageNumber - 1, basicSearchReq.getPageSize(), null);

        Page<TreatiseCategory> treatiseCategoryPage = super.findAllBySearchParams(
                buildSearchParams(treatiseCategorySearchReq), pageRequest);

        return treatiseCategoryPage.map(this::getTreatiseCategoryDTO);
    }

    public List<TreatiseCategory> findParent(){
        return treatiseCategoryDAO.findByPid(0L);
    }


    @Transactional
    public void update(TreatiseCategoryUpdateReq treatiseCategoryUpdateReq) {
        TreatiseCategory treatiseCategory = this.findById(treatiseCategoryUpdateReq.getId());
        ObjectConvertUtil.objectCopy(treatiseCategory, treatiseCategoryUpdateReq);
        this.update(treatiseCategory);
    }

    public boolean exist(String name) {
        return treatiseCategoryDAO.findByName(name) != null;
    }

    @Transactional
    public void save(TreatiseCategorySaveReq treatiseCategorySaveReq) {
        TreatiseCategory treatiseCategory = new TreatiseCategory();
        ObjectConvertUtil.objectCopy(treatiseCategory, treatiseCategorySaveReq);
        this.save(treatiseCategory);
    }

    /**
     * 转为DTO
     * @param source TreatiseCategory
     * @return TreatiseCategoryDTO
     */
    private TreatiseCategoryDTO getTreatiseCategoryDTO(TreatiseCategory source) {
        TreatiseCategoryDTO treatiseCategoryDTO = new TreatiseCategoryDTO();
        ObjectConvertUtil.objectCopy(treatiseCategoryDTO, source);
        return treatiseCategoryDTO;
    }

    public List<TreatiseCategory> findChildren(Long pid) {
        return treatiseCategoryDAO.findByPid(pid);
    }
}
