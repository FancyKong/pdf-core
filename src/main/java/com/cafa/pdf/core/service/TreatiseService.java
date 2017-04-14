/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.TreatiseDTO;
import com.cafa.pdf.core.dal.entity.Treatise;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSearchReq;
import com.cafa.pdf.core.dal.dao.IBaseDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author FancyKong
 * @file TreatiseService.java
 * @date 2017/4/14 11:03
 * @since 0.0.1
 */
@Service
public class TreatiseService extends ABaseService<Treatise,Long>{

    public Page<TreatiseDTO> findAll(TreatiseSearchReq userSearchReq, BasicSearchReq basicSearchReq) {
        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        PageRequest pageRequest = super.buildPageRequest(pageNumber, basicSearchReq.getPageSize());

        return null;
    }

    @Override
    protected IBaseDAO<Treatise, Long> getEntityDAO() {
        return null;
    }
}
