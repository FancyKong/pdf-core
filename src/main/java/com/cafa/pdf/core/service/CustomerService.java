package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.CustomerDTO;
import com.cafa.pdf.core.commom.enums.ActiveEnum;
import com.cafa.pdf.core.commom.shiro.CryptographyUtil;
import com.cafa.pdf.core.dal.dao.CustomerDAO;
import com.cafa.pdf.core.dal.dao.IBaseDAO;
import com.cafa.pdf.core.dal.entity.Customer;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.customer.CustomerReq;
import com.cafa.pdf.core.web.request.customer.CustomerSearchReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CustomerService extends ABaseService<Customer, Long> {

    private final CustomerDAO customerDAO;

    @Autowired
    public CustomerService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    protected IBaseDAO<Customer, Long> getEntityDAO() {
        return customerDAO;
    }

    public CustomerDTO findOne(Long CustomerId) {
        Customer customer = customerDAO.findOne(CustomerId);
        return customer != null ? getCustomerDTO(customer) : null;
    }

    public boolean exist(String telephone) {
        return customerDAO.findByTelephone(telephone) != null;
    }

    @Transactional
    public void delete(Long customerId) {
        Customer customer = customerDAO.findOne(customerId);
        if (customer == null) return;

        super.delete(customerId);
    }

    public Page<CustomerDTO> findAll(BasicSearchReq basicSearchReq, CustomerSearchReq customerSearchReq) {

        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        PageRequest pageRequest = super.buildPageRequest(pageNumber, basicSearchReq.getPageSize());

        //除了分页条件没有特定搜索条件的，为了缓存count
        if (ObjectConvertUtil.objectFieldIsBlank(customerSearchReq)){
            log.debug("没有特定搜索条件的");
            List<Customer> customerList = customerDAO.listAllPaged(pageRequest);
            List<CustomerDTO> CustomerDTOList = customerList.stream().map(this::getCustomerDTO).collect(Collectors.toList());

            //为了计算总数使用缓存，加快搜索速度
            Long count = getCount();
            return new PageImpl<>(CustomerDTOList, pageRequest, count);
        }

        //有了其它搜索条件
        Page<Customer> userPage = super.findAllBySearchParams(
                buildSearchParams(customerSearchReq), pageNumber, basicSearchReq.getPageSize());

        return userPage.map(this::getCustomerDTO);
    }

    @Transactional
    public void update(CustomerReq customerReq) {
        Customer customer = this.findById(customerReq.getId());
        if (customer == null) return;

        ObjectConvertUtil.objectCopy(customer, customerReq);
        customer.setModifiedTime(new Date());
        this.update(customer);

    }

    @Transactional
    public Customer save(CustomerReq customerReq) {
        Customer customer = new Customer();
        ObjectConvertUtil.objectCopy(customer, customerReq);
        customer.setPassword(CryptographyUtil.cherishSha1(customer.getPassword()));
        customer.setCreatedTime(new Date());
        customer.setModifiedTime(new Date());

        return this.save(customer);
    }

    /**
     * 把实体类转换成DTO
     * @param source Customer
     * @return CustomerDTO
     */
    private CustomerDTO getCustomerDTO(Customer source) {
        CustomerDTO customerDTO = new CustomerDTO();
        ObjectConvertUtil.objectCopy(customerDTO, source);
        customerDTO.setActiveStr(ActiveEnum.getDesc(source.getActive()));
        return customerDTO;
    }


}
