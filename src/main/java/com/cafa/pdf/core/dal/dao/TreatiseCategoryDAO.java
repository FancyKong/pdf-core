package com.cafa.pdf.core.dal.dao;

import com.cafa.pdf.core.dal.entity.TreatiseCategory;

import java.util.List;

/**
 * 著作类别
 * Created by Cherish on 2017/1/4.
 */
public interface TreatiseCategoryDAO extends IBaseDAO<TreatiseCategory,Long> {

    List<TreatiseCategory> findAll();

    TreatiseCategory findByName(String name);

    List<TreatiseCategory> findByPid(Long pid);

}
