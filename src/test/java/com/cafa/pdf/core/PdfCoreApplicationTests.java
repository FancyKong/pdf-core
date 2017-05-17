package com.cafa.pdf.core;

import com.cafa.pdf.core.dal.solr.document.Article;
import com.cafa.pdf.core.dal.solr.document.ChapterSolrDoc;
import com.cafa.pdf.core.dal.solr.document.TreatiseSolrDoc;
import com.cafa.pdf.core.dal.solr.repository.ArticleRepository;
import com.cafa.pdf.core.dal.solr.repository.ChapterSolrRepository;
import com.cafa.pdf.core.dal.solr.repository.TreatiseSolrRepository;
import com.sun.codemodel.internal.JForEach;
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
import java.util.UUID;

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

    @Autowired
    private ChapterSolrRepository chapterSolrRepository;

    @Test
    public void saveChapter(){
        //e6dc25ce81c34b10bef5f189c0bed292
        ChapterSolrDoc chapter = new ChapterSolrDoc();
        chapter.setId(UUID.randomUUID().toString().replace("-",""));
        chapter.setId("0aee431390074d8292221115ed2c6018");
        chapter.setSeq(2);
        chapter.setContent("4.\t阅读次数和点击量有联系吗? \n" +
                "我理解是阅读次数是在用户点击之后,进入阅读才会产生阅读量. 在检索出来条目之后, 点击条目进入详情页面, 详情页面有阅读按钮. 点击按钮进入阅读.\n");
        chapter.setTreatiseId("abc");
        log.info("param={}",chapter);
        chapterSolrRepository.save(chapter);
    }

    @Autowired
    private TreatiseSolrRepository treatiseSolrRepository;

    @Test
    public void saveTreatise(){
        TreatiseSolrDoc treatiseSolrDoc = new TreatiseSolrDoc();
        List<ChapterSolrDoc> list = chapterSolrRepository.findByTreatiseIdOrderBySeqAsc("abc");
        treatiseSolrDoc.setId("abc");
        StringBuilder content = new StringBuilder();
        for(ChapterSolrDoc d : list){
            content.append(d.getContent());
        }
        treatiseSolrDoc.setContent(content.toString());
        treatiseSolrRepository.save(treatiseSolrDoc);
    }

    @Test
    public void queryChapter(){
        HighlightPage<ChapterSolrDoc> pages = chapterSolrRepository.findByContentOrderById("apache",new SolrPageRequest(0,10));
        List<ChapterSolrDoc> chapterSolrDocs = pages.getContent();
        for(ChapterSolrDoc d : chapterSolrDocs) {
            StringBuilder sb = new StringBuilder();
            List<HighlightEntry.Highlight> hs = pages.getHighlights(d);
            for(HighlightEntry.Highlight h  : hs) {
                for(String s  : h.getSnipplets()) {
                    sb.append(s);
                    System.out.println(s);
                }
            }
            System.out.println();
            d.setContent(sb.toString());
        }
    }
}
