/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.TreatiseDTO;
import com.cafa.pdf.core.commom.enums.Language;
import com.cafa.pdf.core.commom.enums.PublicationMode;
import com.cafa.pdf.core.dal.dao.AuthorDAO;
import com.cafa.pdf.core.dal.dao.IBaseDAO;
import com.cafa.pdf.core.dal.dao.TreatiseCategoryDAO;
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

    private final AuthorDAO authorDAO;

    private final TreatiseCategoryDAO categoryDAO;

    @Autowired
    public TreatiseService(TreatiseDAO treatiseDAO, AuthorDAO authorDAO, TreatiseCategoryDAO categoryDAO) {
        this.treatiseDAO = treatiseDAO;
        this.authorDAO = authorDAO;
        this.categoryDAO = categoryDAO;
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
    public TreatiseDTO update(TreatiseUpdateReq treatiseUpdateReq) {
        Treatise treatise = findById(treatiseUpdateReq.getId());
        ObjectConvertUtil.objectCopy(treatise, treatiseUpdateReq);
        Treatise update = update(treatise);
        return getTreatiseDTO(update);
    }

    @Transactional
    public TreatiseDTO save(TreatiseSaveReq treatiseSaveReq) {
        Treatise treatise = new Treatise();
        ObjectConvertUtil.objectCopy(treatise, treatiseSaveReq);
        Treatise save = save(treatise);
        return getTreatiseDTO(save);
    }

    private TreatiseDTO getTreatiseDTO(Treatise source) {
        TreatiseDTO treatiseDTO = new TreatiseDTO();
        ObjectConvertUtil.objectCopy(treatiseDTO, source);
        treatiseDTO.setAuthor(authorDAO.findOne(source.getAuthorId()).getNickname());
        treatiseDTO.setCategory(categoryDAO.findById(source.getId()).getName());
        treatiseDTO.setLanguage(Language.valueOf(source.getLanguage()).getDesc());
        treatiseDTO.setPublicationMode(PublicationMode.valueOf(source.getPublicationMode()).getDesc());
        treatiseDTO.setISBN(source.getISBN());
        log.info("isbn = {}",source.getISBN());
        return treatiseDTO;
    }

}
