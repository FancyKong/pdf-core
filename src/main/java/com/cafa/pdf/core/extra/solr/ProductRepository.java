package com.cafa.pdf.core.extra.solr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.*;

import java.util.Collection;
import java.util.List;

/**
 * Created by Cherish on 2017/1/6.
 */
public interface ProductRepository extends SolrCrudRepository<Article, String> {

    List<Article> findByNameStartingWith(String name);

    Page<Article> findByPrice(Double price, Pageable page);

    Page<Article> findByNameOrDescription(@Boost(2) String name, String description, Pageable page);

    @Highlight(prefix = "<b>", postfix = "</b>")
    HighlightPage<Article> findByNameIn(Collection<String> name, Pageable page);

    @Query(value = "name:?0")
    @Facet(fields = { "cat" }, limit=20)
    FacetPage<Article> findByNameAndFacetOnCategory(String name, Pageable page);


}