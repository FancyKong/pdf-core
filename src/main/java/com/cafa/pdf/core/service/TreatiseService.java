/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.TreatiseDTO;
import com.cafa.pdf.core.dal.dao.IBaseDAO;
import com.cafa.pdf.core.dal.dao.TreatiseDAO;
import com.cafa.pdf.core.dal.entity.Treatise;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSaveReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseUpdateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author FancyKong
 * @file TreatiseService.java
 * @date 2017/4/14 11:03
 * @since 0.0.1
 */
@Service
@Transactional(readOnly = true)
public class TreatiseService extends ABaseService<Treatise, Long> {

    private final TreatiseDAO treatiseDAO;

    @Autowired
    public TreatiseService(TreatiseDAO treatiseDAO) {
        this.treatiseDAO = treatiseDAO;
    }

    @Override
    protected IBaseDAO<Treatise, Long> getEntityDAO() {
        return treatiseDAO;
    }

    public Page<TreatiseDTO> findAll(TreatiseSearchReq treatiseSearchReq, BasicSearchReq basicSearchReq) {
        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        PageRequest pageRequest = super.buildPageRequest(pageNumber, basicSearchReq.getPageSize());

        //有了其它搜索条件
        Page<Treatise> treatisePage = super.findAllBySearchParams(
                buildSearchParams(treatiseSearchReq), pageNumber, basicSearchReq.getPageSize());

        return treatisePage.map(this::getTreatiseDTO);
    }

    public Long getCount() {
        return treatiseDAO.count();
    }

    @Transactional
    public Treatise update(TreatiseUpdateReq treatiseUpdateReq) {
        Treatise treatise = findById(treatiseUpdateReq.getId());
        ObjectConvertUtil.objectCopy(treatise, treatiseUpdateReq);
        return update(treatise);
    }

    @Transactional
    public Treatise save(TreatiseSaveReq treatiseSaveReq) {
        Treatise treatise = new Treatise();
        ObjectConvertUtil.objectCopy(treatise, treatiseSaveReq);
        return save(treatise);
    }

    private TreatiseDTO getTreatiseDTO(Treatise source) {
        TreatiseDTO treatiseDTO = new TreatiseDTO();
        ObjectConvertUtil.objectCopy(treatiseDTO, source);
        return treatiseDTO;
    }

}
