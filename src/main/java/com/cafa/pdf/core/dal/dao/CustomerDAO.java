package com.cafa.pdf.core.dal.dao;

import com.cafa.pdf.core.dal.entity.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerDAO extends IBaseDAO<Customer,Long> {

    Customer findByTelephone(String telephone);

    @Query("SELECT c FROM Customer AS c ")
    List<Customer> listAllPaged(Pageable pageable);



}
