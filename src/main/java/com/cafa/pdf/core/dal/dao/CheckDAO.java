package com.cafa.pdf.core.dal.dao;

import com.cafa.pdf.core.dal.entity.Check;

/**
 * 信息核实
 * @author Cherish
 * @version 1.0
 * @date 2017/4/26 8:22
 */
public interface CheckDAO extends IBaseDAO<Check, Long> {

    Check findByRandomKey(String randomKey);

}
