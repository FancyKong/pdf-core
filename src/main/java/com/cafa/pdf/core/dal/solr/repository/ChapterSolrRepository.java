/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.dal.solr.repository;

import com.cafa.pdf.core.dal.solr.document.Article;
import com.cafa.pdf.core.dal.solr.document.ChapterSolrDoc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.Highlight;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import javax.persistence.FieldResult;
import java.util.List;

/**
 * @author FancyKong
 * @file ChapterSolrRepository.java
 * @date 2017/4/20 9:31
 * @since 0.0.1
 */
public interface ChapterSolrRepository extends SolrCrudRepository<ChapterSolrDoc,String>{

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content"},snipplets = 1,fragsize = 150)
    @Query(fields = {"id","treatiseId","seq"})
    HighlightPage<ChapterSolrDoc> findByContentOrderById(String content, Pageable pageable);

    List<ChapterSolrDoc> findByTreatiseIdOrderBySeqAsc(String treatiseId);
}
