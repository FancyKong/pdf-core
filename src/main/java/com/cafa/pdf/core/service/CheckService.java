package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.enums.CheckEnum;
import com.cafa.pdf.core.commom.shiro.CryptographyUtil;
import com.cafa.pdf.core.dal.dao.*;
import com.cafa.pdf.core.dal.entity.Author;
import com.cafa.pdf.core.dal.entity.Check;
import com.cafa.pdf.core.dal.entity.Customer;
import com.cafa.pdf.core.dal.entity.User;
import com.cafa.pdf.core.util.MStringUtils;
import com.cafa.pdf.core.util.RequestHolder;
import com.cafa.pdf.core.web.request.OverridePwd;
import org.apache.commons.lang3.StringUtils;
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
public class CheckService extends ABaseService<Check, Long> {

    private final CheckDAO checkDAO;
    private final UserDAO userDAO;
    private final AuthorDAO authorDAO;
    private final CustomerDAO customerDAO;
    private final MailComponent mailComponent;

    @Autowired
    public CheckService(CheckDAO checkDAO, UserDAO userDAO, AuthorDAO authorDAO, CustomerDAO customerDAO, MailComponent mailComponent) {
        this.checkDAO = checkDAO;
        this.userDAO = userDAO;
        this.authorDAO = authorDAO;
        this.customerDAO = customerDAO;
        this.mailComponent = mailComponent;
    }

    @Override
    protected IBaseDAO<Check, Long> getEntityDAO() {
        return checkDAO;
    }

    @Transactional
    public String sendOverridePwdEmail(String email, String type) {
        CheckEnum checkEnum; // 检测类别
        Long activeId; // 需要激活的id
        if ("AUTHOR".equals(type)) {
            checkEnum = CheckEnum.AUTHOR;
            Author author = authorDAO.findByEmail(email);
            if (author == null) {
                log.warn("【找回密码】 不存在该著作者邮箱！");
                return "该邮箱未注册";
            }
            activeId = author.getId();
        }else if ("ADMIN".equals(type)){
                checkEnum = CheckEnum.ADMIN;
                User user = userDAO.findByEmail(email);
                if (user == null) {
                    log.warn("【找回密码】 不存在该管理员邮箱！");
                    return "该邮箱未注册";
                }
                activeId = user.getId();
        }else if ("CUSTOMER".equals(type)){
            checkEnum = CheckEnum.CUSTOMER;
            Customer customer = customerDAO.findByEmail(email);
            if (customer == null) {
                log.warn("【找回密码】 不存在该会员邮箱！");
                return "该邮箱未注册";
            }
            activeId = customer.getId();
        }else {
            log.error("【找回密码】发送邮件——>未知用户类型 ");
            return "该邮箱未注册";
        }

        boolean send = send(email, checkEnum, activeId);
        if (send) {
            return "修改密码邮件已发送，请您登录邮箱查看";
        }
        return "系统繁忙，请稍后再试";
    }

    private boolean send(String email, CheckEnum checkEnum, Long activeId) {
        String randomStr = MStringUtils.randomStr();
        Check check = new Check(0L, randomStr, checkEnum.getNum(), activeId);
        check = checkDAO.save(check);

        String basePath = MStringUtils.getBasePath(RequestHolder.getRequest());
        // http://www.domain.com/overridePwd/666/override?key=uuid
        String link = basePath + "overridePwd/" + check.getId() + "/" + "override?key=" + randomStr;
        log.info("【发送邮件】找回密码 link:{} check:{} activeId:{}", link, check, activeId);

        String subject = "找回密码 - 广东科技著作出版服务平台";
        StringBuilder sb = new StringBuilder();
        sb.append("请点击以下链接找回密码：<br><br>");
        sb.append("<a href='" + link + "'>" + link + "</a><br><br>");
        sb.append("请妥善保管此电子邮件。<br><br>");

        sb.append("<br><br><b>广东科技著作出版服务平台</b><br><br>");

        mailComponent.sendHtmlMail(email, subject, sb.toString());

        return true;
    }

    private boolean overridePwdSuccess(String email){
        log.info("【发送邮件】成功修改密码");

        String subject = "成功修改密码 - 广东科技著作出版服务平台";
        StringBuilder sb = new StringBuilder();
        sb.append("您的密码修改成功，可以去登陆啦！<br><br>");
        sb.append("<br><br><b>广东科技著作出版服务平台</b><br><br>");

        mailComponent.sendHtmlMail(email, subject, sb.toString());
        return true;
    }

    /**
     * 执行密码修改
     * @param overridePwd OverridePwd
     * @return bool
     */
    @Transactional
    public boolean overridePwd(OverridePwd overridePwd) {
        Check check = checkDAO.findOne(overridePwd.getCheckId());
        if (check != null && StringUtils.equals(overridePwd.getKey(), check.getRandomKey())) {
            String newPwd = CryptographyUtil.cherishSha1(overridePwd.getNewPwd());

            if (CheckEnum.AUTHOR.getNum().equals(check.getActiveType())){// 著作者
                Author author = authorDAO.findOne(check.getActiveId());
                author.setPassword(newPwd);
                authorDAO.save(author);

                log.info("【执行密码修改】 著作者 {}", author);
                overridePwdSuccess(author.getEmail());
            }else if (CheckEnum.ADMIN.getNum().equals(check.getActiveType())){// 管理员
                User user = userDAO.findOne(check.getActiveId());
                user.setPassword(newPwd);
                userDAO.save(user);
                log.info("【执行密码修改】 管理员 {}", user);

                overridePwdSuccess(user.getEmail());
            }else if (CheckEnum.CUSTOMER.getNum().equals(check.getActiveType())){// 会员
                Customer customer = customerDAO.findOne(check.getActiveId());
                customer.setPassword(newPwd);
                customerDAO.save(customer);
                log.info("【执行密码修改】 会员 {}", customer);

                overridePwdSuccess(customer.getEmail());
            }else {
                return false;
            }
            checkDAO.delete(check.getId());
            log.info("【执行密码修改】 删除该checkId: {}", check.getId());

            return true;
        }
        return false;
    }


}
