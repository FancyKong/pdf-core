/**
 * JDKCC.com.
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.dal.solr.repository;

import com.cafa.pdf.core.dal.solr.document.ChapterSolrDoc;
import com.cafa.pdf.core.dal.solr.document.TreatiseSolrDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.Highlight;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.Date;

/**
 * @author FancyKong
 * @file TreatiseSolrRepository.java
 * @date 2017/4/20 9:34
 * @since 0.0.1
 */
public interface TreatiseSolrRepository extends SolrCrudRepository<TreatiseSolrDoc,String>{

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content","title"},snipplets = 3,fragsize = 150)
    @Query(fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    HighlightPage<TreatiseSolrDoc> findByContent(String content, Pageable pageable);

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content","title"},snipplets = 3,fragsize = 150)
    @Query(fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    HighlightPage<TreatiseSolrDoc> findByContentOrderByPublishDateAsc(String content, Pageable pageable);

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content","title"},snipplets = 3,fragsize = 150)
    @Query(fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    HighlightPage<TreatiseSolrDoc> findByDescription(String content, Pageable pageable);

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content","title"},snipplets = 3,fragsize = 150)
    @Query(fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    HighlightPage<TreatiseSolrDoc> findByDescriptionOrderByPublishDateAsc(String content, Pageable pageable);

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content","title"},snipplets = 3,fragsize = 150)
    @Query(fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    HighlightPage<TreatiseSolrDoc> findByAuthor(String content, Pageable pageable);

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content","title"},snipplets = 3,fragsize = 150)
    @Query(fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    HighlightPage<TreatiseSolrDoc> findByAuthorOrderByPublishDateAsc(String content, Pageable pageable);

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content","title"},snipplets = 3,fragsize = 150)
    @Query(fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    HighlightPage<TreatiseSolrDoc> findByTitleOrderByPublishDateAsc(String content, Pageable pageable);

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content","title"},snipplets = 3,fragsize = 150)
    @Query(fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    HighlightPage<TreatiseSolrDoc> findByTitle(String content, Pageable pageable);

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content","title"},snipplets = 3,fragsize = 150)
    @Query(fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    HighlightPage<TreatiseSolrDoc> findByCategoryId(Long query, Pageable solrPageRequest);

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content","title"},snipplets = 3,fragsize = 150)
    @Query(fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    HighlightPage<TreatiseSolrDoc> findByCategoryIdOrderByPublishDateAsc(Long query, Pageable solrPageRequest);

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content"},snipplets = 3,fragsize = 150)
    @Query(fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    HighlightPage<TreatiseSolrDoc> findByPCategoryId(Long query, Pageable solrPageRequest);

    @Highlight(prefix = "<span style='color:red'>", postfix = "</span>" , fields = {"content"},snipplets = 3,fragsize = 150)
    @Query(fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    HighlightPage<TreatiseSolrDoc> findByPCategoryIdOrderByPublishDateAsc(Long query, Pageable solrPageRequest);

    @Query(value = "title:?0 AND author:?1 AND publishHouse:?2 AND publishDate>?3 AND publishDate<?4 AND language:?5 AND categoryId:?6",
            fields = {"id","title","description","keywords","author","publishDate","categoryName"})
    Page<TreatiseSolrDoc> advance(String title,String author,String house,String start,String end,String language,String category, Pageable page);
}
