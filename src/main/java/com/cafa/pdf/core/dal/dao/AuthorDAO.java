package com.cafa.pdf.core.dal.dao;

import com.cafa.pdf.core.dal.entity.Author;
import com.cafa.pdf.core.dal.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 著作者
 * Created by Cherish on 2017/1/4.
 */
public interface AuthorDAO extends IBaseDAO<Author, Long> {

    Author findByTelephone(String telephone);

    @Query("SELECT au FROM Author AS au ")
    List<Author> listAllPaged(Pageable pageable);

    Author findByEmail(String email);

}
