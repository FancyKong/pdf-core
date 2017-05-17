package com.cafa.pdf.core.dal.dao;

import com.cafa.pdf.core.dal.entity.SysConfig;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/5/17 14:53
 */
public interface SysConfigDAO extends IBaseDAO<SysConfig, Long> {

    SysConfig findByKeyword(String keyword);

}
