/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.dal.solr.repository;

import com.cafa.pdf.core.dal.solr.document.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.Highlight;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * @author FancyKong
 * @file ArticleRepository.java
 * @date 2017/3/26 12:16
 * @since 0.0.1
 */
public interface ArticleRepository extends SolrCrudRepository<Article, String> {

    @Highlight(prefix = "<b>", postfix = "</b>" , fields = {"content"})
    HighlightPage<Article> findByContentOrderById(String content, Pageable pageable);
}
