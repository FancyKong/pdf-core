package com.cafa.pdf.core.service;

import com.cafa.pdf.core.dal.dao.IBaseDAO;
import com.cafa.pdf.core.dal.dao.SysConfigDAO;
import com.cafa.pdf.core.dal.entity.SysConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/5/17 14:55
 */
@Service
@Transactional(readOnly = true)
public class SysConfigService extends ABaseService<SysConfig, Long> {

    private final SysConfigDAO sysConfigDAO;

    private static final String TREATISE_AMOUNT = "treatise_amount";
    private static final String CUSTOMER_AMOUNT = "customer_amount";
    private static final String VISIT = "visit";

    @Autowired
    public SysConfigService(SysConfigDAO sysConfigDAO) {
        this.sysConfigDAO = sysConfigDAO;
    }

    @Override
    protected IBaseDAO<SysConfig, Long> getEntityDAO() {
        return sysConfigDAO;
    }

    public SysConfig findByKeyword(String keyword) {
        return sysConfigDAO.findByKeyword(keyword);
    }

    /**
     * 获取访问量
     * @return Long 访问量
     */
    public Long findVisit() {
        return getKeywordLong(VISIT);
    }

    @Transactional
    public Long addVisit() {
        return incrementAndGetKeywordLong(VISIT, 1L);
    }

    /**
     * 会员注册量
     */
    public Long findCustomerAmount() {
        return getKeywordLong(CUSTOMER_AMOUNT);
    }

    @Transactional
    public Long addCustomerAmount() {
        return incrementAndGetKeywordLong(CUSTOMER_AMOUNT, 1L);
    }

    /**
     * 专著收录量
     */
    public Long findTreatiseAmount() {
        return getKeywordLong(TREATISE_AMOUNT);
    }


    @Transactional
    public Long addTreatiseAmount() {
        return incrementAndGetKeywordLong(TREATISE_AMOUNT, 1L);
    }

    @Transactional
    public Long decreaseTreatiseAmount() {
        return incrementAndGetKeywordLong(TREATISE_AMOUNT, -1L);
    }

    /**
     * 获取Long值
     * @param keyword 关键字
     * @return Long
     */
    private Long getKeywordLong(String keyword) {
        SysConfig treatiseAmount = sysConfigDAO.findByKeyword(keyword);
        if (treatiseAmount == null) {
            return 0L;
        }
        return treatiseAmount.getLongValue();
    }
    /**
     * 加一并获取
     * @param keyword 关键字
     * @param delta 增量，正负值
     * @return Long
     */
    private Long incrementAndGetKeywordLong(String keyword, Long delta) {
        SysConfig treatiseAmount = sysConfigDAO.findByKeyword(keyword);
        if (treatiseAmount == null) {
            treatiseAmount = new SysConfig(0L, keyword, "", 0, 0L, "");
        }
        treatiseAmount.setLongValue(treatiseAmount.getLongValue() + delta);
        treatiseAmount = sysConfigDAO.save(treatiseAmount);
        return treatiseAmount.getLongValue();
    }


}
