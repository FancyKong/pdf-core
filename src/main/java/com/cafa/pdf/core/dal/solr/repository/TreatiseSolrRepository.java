/**
 * JDKCC.com.
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.dal.solr.repository;

import com.cafa.pdf.core.dal.solr.document.ChapterSolrDoc;
import com.cafa.pdf.core.dal.solr.document.TreatiseSolrDoc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.Highlight;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * @author FancyKong
 * @file TreatiseSolrRepository.java
 * @date 2017/4/20 9:34
 * @since 0.0.1
 */
public interface TreatiseSolrRepository extends SolrCrudRepository<TreatiseSolrDoc,String>{

    @Highlight(prefix = "<b>", postfix = "</b>" , fields = {"content"})
    HighlightPage<TreatiseSolrDoc> findByContentOrderById(String content, Pageable pageable);
}
