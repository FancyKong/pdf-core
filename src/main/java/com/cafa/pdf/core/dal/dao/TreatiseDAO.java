package com.cafa.pdf.core.dal.dao;

import com.cafa.pdf.core.dal.entity.Treatise;

import java.util.List;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/4/14 14:31
 */
public interface TreatiseDAO extends IBaseDAO<Treatise, Long> {

    List<Treatise> findByAuthorId(Long authorId);


}
