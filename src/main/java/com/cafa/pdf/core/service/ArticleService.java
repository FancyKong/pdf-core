package com.cafa.pdf.core.service;

import com.cafa.pdf.core.dal.dto.ArticleDTO;
import com.cafa.pdf.core.dal.entity.Article;
import com.cafa.pdf.core.dal.request.ArticleReq;
import com.cafa.pdf.core.dal.request.BasicSearchReq;
import com.cafa.pdf.core.repository.ArticleDAO;
import com.cafa.pdf.core.repository.IBaseDAO;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ArticleService extends ABaseService<Article, Long> {

    @Autowired
    private ArticleDAO newsDAO;

    @Override
    protected IBaseDAO<Article, Long> getEntityDAO() {
        return newsDAO;
    }

    public ArticleDTO findOne(Long articleId) {
        Article article = newsDAO.findOne(articleId);
        return ObjectConvertUtil.objectCopy(new ArticleDTO(), article);
    }

    @Transactional
    public void delete(Long articleId) {
        super.delete(articleId);
    }

    public Page<ArticleDTO> findAll(BasicSearchReq basicSearchReq) {

        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        Page<Article> newsPage = this.findAll(pageNumber, basicSearchReq.getPageSize());

        return newsPage.map(source -> {
            ArticleDTO newsDTO = new ArticleDTO();
            ObjectConvertUtil.objectCopy(newsDTO, source);
            return newsDTO;
        });
    }

    @Transactional
    public void updateByReq(ArticleReq newsReq) {
        Article news = this.findById(newsReq.getId());
        ObjectConvertUtil.objectCopy(news, newsReq);
        news.setModifiedTime(new Date());
        if (news.getReadSum() == null){
            news.setReadSum(0);
        }
        this.update(news);
    }

    @Transactional
    public void saveByReq(ArticleReq newsReq) {
        Article news = new Article();
        ObjectConvertUtil.objectCopy(news, newsReq);
        news.setCreatedTime(new Date());
        news.setModifiedTime(new Date());
        if (news.getReadSum() == null){
            news.setReadSum(0);
        }
        this.save(news);
    }


}
