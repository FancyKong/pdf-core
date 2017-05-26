package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.CustomerDTO;
import com.cafa.pdf.core.commom.enums.ActiveEnum;
import com.cafa.pdf.core.commom.enums.CheckEnum;
import com.cafa.pdf.core.commom.shiro.CryptographyUtil;
import com.cafa.pdf.core.dal.dao.CheckDAO;
import com.cafa.pdf.core.dal.dao.CustomerDAO;
import com.cafa.pdf.core.dal.dao.IBaseDAO;
import com.cafa.pdf.core.dal.entity.Check;
import com.cafa.pdf.core.dal.entity.Customer;
import com.cafa.pdf.core.util.IPv4Util;
import com.cafa.pdf.core.util.MStringUtils;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import com.cafa.pdf.core.util.RequestHolder;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.customer.CustomerRegisterReq;
import com.cafa.pdf.core.web.request.customer.CustomerSaveReq;
import com.cafa.pdf.core.web.request.customer.CustomerSearchReq;
import com.cafa.pdf.core.web.request.customer.CustomerUpdateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(readOnly = true)
public class CustomerService extends ABaseService<Customer, Long> {

    private final CustomerDAO customerDAO;
    private final CheckDAO checkDAO;
    private final MailComponent mailComponent;

    @Autowired
    public CustomerService(CustomerDAO customerDAO, CheckDAO checkDAO, MailComponent mailComponent) {
        this.customerDAO = customerDAO;
        this.checkDAO = checkDAO;
        this.mailComponent = mailComponent;
    }

    @Override
    protected IBaseDAO<Customer, Long> getEntityDAO() {
        return customerDAO;
    }

    public Customer findByTelephone(String telephone) {
        return customerDAO.findByTelephone(telephone);
    }

    public Customer findByUsername(String username) {
        return customerDAO.findByUsername(username);
    }

    public Long getCount() {
        return customerDAO.count();
    }

    @Transactional
    public void freezeOrActive(Long customerId) {
        Customer customer = findById(customerId);
        Integer active = customer.getActive();
        customer.setActive(1 - active);
        customerDAO.save(customer);
    }

    @Transactional
    public Customer save(CustomerSaveReq customerSaveReq) {
        log.info("【新增会员】 {}", customerSaveReq);
        Customer customer = new Customer();
        ObjectConvertUtil.objectCopy(customer, customerSaveReq);
        customer.setCreatedTime(new Date());
        customer.setModifiedTime(new Date());
        return customerDAO.save(customer);
    }

    @Transactional
    public Customer update(CustomerUpdateReq customerUpdateReq) {
        log.info("【更新会员】 {}", customerUpdateReq);
        Customer customer = findById(customerUpdateReq.getId());
        ObjectConvertUtil.objectCopy(customer, customerUpdateReq);
        customer.setModifiedTime(new Date());
        return customerDAO.save(customer);
    }

    @Transactional
    public void register(CustomerRegisterReq customerRegisterReq) {
        log.info("【注册会员】 {}", customerRegisterReq);
        Customer customer = new Customer();
        ObjectConvertUtil.objectCopy(customer, customerRegisterReq);
        customer.setCreatedTime(new Date());
        customer.setModifiedTime(new Date());
        // 设置密码 此时的ip
        customer.setPassword(CryptographyUtil.cherishSha1(customer.getPassword()));
        customer.setIp(IPv4Util.ipToInt(MStringUtils.getIpAddress(RequestHolder.getRequest())));
        // 待邮箱激活
        customer.setActive(ActiveEnum.UN_CHECK_EMAIL.getNum());

        customer = this.save(customer);
        // 发送邮件
        this.sendCustomerEmail(customer);
    }

    public Page<CustomerDTO> findAll(BasicSearchReq basicSearchReq, CustomerSearchReq customerSearchReq) {
        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        Page<Customer> customerPage = super.findAllBySearchParams(
                buildSearchParams(customerSearchReq), pageNumber, basicSearchReq.getPageSize());
        return customerPage.map(this::getCustomerDTO);
    }

    /**
     * 转成DTO
     * @param source Customer
     * @return CustomerDTO
     */
    private CustomerDTO getCustomerDTO(Customer source) {
        CustomerDTO customerDTO = new CustomerDTO();
        ObjectConvertUtil.objectCopy(customerDTO, source);
        customerDTO.setActiveStr(ActiveEnum.getDesc(source.getActive()));
        return customerDTO;
    }

    /**
     * 检测该邮箱是否已存在，
     * @param email 邮箱
     * @return 存在:true 不存在或邮箱未验证：false
     */
    @Transactional
    public boolean existEmail(String email) {
        Customer customer = customerDAO.findByEmail(email);
        if (customer == null) {
            return false;
        }
        if (customer.getActive().equals(ActiveEnum.UN_CHECK_EMAIL.getNum())){
            //邮箱未验证的，删除他，返回false
            customerDAO.delete(customer);
            return false;
        }
        return true;
    }
    @Transactional
    public boolean existUsername(String username) {
        Customer customer = customerDAO.findByUsername(username);
        if (customer == null) {
            return false;
        }
        if (customer.getActive().equals(ActiveEnum.UN_CHECK_EMAIL.getNum())){
            //邮箱未验证的，删除他，返回false
            customerDAO.delete(customer);
            return false;
        }
        return true;
    }

    /**
     * 邮箱验证激活会员
     * @param checkId
     * @param key
     * @return
     */
    @Transactional
    public boolean active(Long checkId, String key) {
        log.info("【激活会员】 checkId:{} key:{}", checkId, key);
        Check check = checkDAO.findOne(checkId);
        log.info("【激活会员】 check:{}", check);
        if (check == null || !check.getRandomKey().equals(key)) {
            return false;
        }
        // 激活会员账号
        Customer customer = customerDAO.findOne(check.getActiveId());
        customer.setActive(ActiveEnum.ACTIVE.getNum());
        customerDAO.save(customer);
        // 删除邮箱核实信息
        checkDAO.delete(check.getId());
        return true;
    }

    /**
     * 发邮件给会员，验证所填写邮箱
     * @param customer 会员信息
     * @return 是否成功发送
     */
    @Transactional
    private boolean sendCustomerEmail(Customer customer){
        String randomStr = MStringUtils.randomStr();
        Check check = new Check(0L, randomStr, CheckEnum.AUTHOR.getNum(), customer.getId());
        check = checkDAO.save(check);

        String basePath = MStringUtils.getBasePath(RequestHolder.getRequest());
        // http://www.domain.com/customer/666/active?key=uuid
        String link = basePath + "customer/" + check.getId() + "/" + "active?key=" + randomStr;
        log.info("【发送邮件】 link:{} check:{} customer:{}", link, check, customer);

        String subject = "邮箱激活 - 广东科技著作出版服务平台";
        StringBuilder sb = new StringBuilder();
        sb.append("欢迎您加入成为会员！<br><br>");
        sb.append("您的账号已经成功创建，请点击以下链接激活账号：<br><br>");
        sb.append("<a href='" + link + "'>" + link + "</a><br><br>");
        sb.append("请妥善保管此电子邮件。<br><br>");

        sb.append("<br><br><b>广东科技著作出版服务平台</b><br><br>");

        mailComponent.sendHtmlMail(customer.getEmail(), subject, sb.toString());
        return true;
    }


}
