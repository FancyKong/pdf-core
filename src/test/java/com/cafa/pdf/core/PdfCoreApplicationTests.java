package com.cafa.pdf.core;

import com.cafa.pdf.core.dal.solr.document.Article;
import com.cafa.pdf.core.dal.solr.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PdfCoreApplicationTests {

    @Autowired
    private ArticleRepository repository;

    @Test
    public void solrSave() {
        for (int i = 0; i < 50; i++) {
            repository.save(new com.cafa.pdf.core.dal.solr.document.Article("" + i, null, "分别把交换机命名为" + i));
        }
    }

    @Test
    public void query(){
        HighlightPage<Article> articles = repository.findByContentOrderById("命名",new SolrPageRequest(0,3));
        List<HighlightEntry<Article>> hs = articles.getHighlighted();
        for (HighlightEntry<Article> p : hs) {
            System.out.println(p.getEntity().getId());
            for (HighlightEntry.Highlight h : p.getHighlights()) {
                h.getSnipplets().parallelStream().forEach(System.out::println);
            }
        }
    }

}
