/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.HitsDTO;
import com.cafa.pdf.core.commom.dto.ReadingDTO;
import com.cafa.pdf.core.commom.dto.TreatiseDTO;
import com.cafa.pdf.core.commom.enums.Language;
import com.cafa.pdf.core.commom.enums.PublicationMode;
import com.cafa.pdf.core.dal.dao.*;
import com.cafa.pdf.core.dal.entity.ChapterFileInfo;
import com.cafa.pdf.core.dal.entity.Treatise;
import com.cafa.pdf.core.dal.entity.TreatiseHits;
import com.cafa.pdf.core.dal.entity.TreatiseReading;
import com.cafa.pdf.core.dal.solr.document.TreatiseSolrDoc;
import com.cafa.pdf.core.dal.solr.repository.TreatiseSolrRepository;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import com.cafa.pdf.core.web.TreatiseController;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.statistics.HitsSearchReq;
import com.cafa.pdf.core.web.request.statistics.ReadingSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSaveCoreReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseSearchReq;
import com.cafa.pdf.core.web.request.treatise.TreatiseUpdateReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author FancyKong
 * @file TreatiseService.java
 * @date 2017/4/14 11:03
 * @since 0.0.1
 */
@Service
@Transactional(readOnly = true)
public class TreatiseService extends ABaseService<Treatise, Long> {

    private final TreatiseDAO treatiseDAO;
    private final AuthorDAO authorDAO;
    private final TreatiseCategoryDAO categoryDAO;
    private final ReadingDAO readingDAO;
    private final HitsDAO hitsDAO;
    private final ChapterService chapterService;
    private final TreatiseSolrRepository treatiseSolrRepository;

    @Autowired
    public TreatiseService(TreatiseDAO treatiseDAO, AuthorDAO authorDAO, TreatiseCategoryDAO categoryDAO, ReadingDAO readingDAO, HitsDAO hitsDAO, ChapterService chapterService, TreatiseSolrRepository treatiseSolrRepository, TreatiseSolrRepository solrRepository, ChapterFileInfoDAO fileInfoDAO) {
        this.treatiseDAO = treatiseDAO;
        this.authorDAO = authorDAO;
        this.categoryDAO = categoryDAO;
        this.readingDAO = readingDAO;
        this.hitsDAO = hitsDAO;
        this.chapterService = chapterService;
        this.treatiseSolrRepository = treatiseSolrRepository;
        this.solrRepository = solrRepository;
        this.fileInfoDAO = fileInfoDAO;
    }

    @Override
    protected IBaseDAO<Treatise, Long> getEntityDAO() {
        return treatiseDAO;
    }

    public Long getHitsOfTreatise(Long treatiseId){
        return hitsDAO.findOne(treatiseId).getCount();
    }

    public Page<TreatiseDTO> findAll(TreatiseSearchReq treatiseSearchReq, BasicSearchReq basicSearchReq) {
        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;

        Page<Treatise> treatisePage = super.findAllBySearchParams(
                buildSearchParams(treatiseSearchReq), pageNumber, basicSearchReq.getPageSize());

        return treatisePage.map(this::getTreatiseDTO);
    }

    public Long getCount() {
        return treatiseDAO.count();
    }

    public List<TreatiseReading> treatisesHot(){
        return readingDAO.findTop10ByOrderByCountDesc();
    }
    @Transactional
    public TreatiseDTO update(TreatiseUpdateReq treatiseUpdateReq) {
        Treatise treatise = findById(treatiseUpdateReq.getId());
        ObjectConvertUtil.objectCopy(treatise, treatiseUpdateReq);
        Treatise update = update(treatise);
        return getTreatiseDTO(update);
    }

    private final TreatiseSolrRepository solrRepository;

    @Override
    @Transactional
    public void delete(Long id) {
        chapterService.deleteAllByTreatiseId(id);
        solrRepository.delete(String.valueOf(id));
        fileInfoDAO.deleteAllByTreatiseId(id);
        readingDAO.delete(id);
        hitsDAO.delete(id);
        super.delete(id);
    }

    @Transactional
    public TreatiseDTO saveCore(TreatiseSaveCoreReq treatiseSaveReq) {
        Treatise treatise = new Treatise();
        ObjectConvertUtil.objectCopy(treatise, treatiseSaveReq);
        Treatise save = save(treatise);
        TreatiseReading reading = new TreatiseReading(save.getId(),save.getBookName(),0L);
        TreatiseHits hits = new TreatiseHits(save.getId(), save.getBookName(),0L);
        hitsDAO.save(hits);
        readingDAO.save(reading);
        return getTreatiseDTO(save);
    }

    private TreatiseDTO getTreatiseDTO(Treatise source) {
        TreatiseDTO treatiseDTO = new TreatiseDTO();
        ObjectConvertUtil.objectCopy(treatiseDTO, source);
        treatiseDTO.setAuthor(authorDAO.findOne(source.getAuthorId()).getNickname());
        treatiseDTO.setCategory(categoryDAO.findById(source.getCategoryId()).getName());
        treatiseDTO.setLanguage(Language.valueOf(source.getLanguage()).getDesc());
        treatiseDTO.setPublicationMode(PublicationMode.valueOf(source.getPublicationMode()).getDesc());
        treatiseDTO.setISBN(source.getISBN());
        return treatiseDTO;
    }

    public List<TreatiseDTO> findByAuthorId(Long authorId) {
        List<Treatise> treatises = treatiseDAO.findByAuthorId(authorId);
        if (treatises == null || treatises.isEmpty()) {
            return new ArrayList<>();
        }
        return treatises.stream().map(this::getTreatiseDTO).collect(Collectors.toList());
    }

    public TreatiseDTO findOne(Long treatiseId) {
        Treatise treatise = treatiseDAO.findOne(treatiseId);
        if (treatise == null) {
            return null;
        }
        return getTreatiseDTO(treatise);
    }

    public Treatise saveChapter(Long treatiseId) {
        Treatise treatise = findById(treatiseId);
        saveTreatiseInSolr(treatise);
        return treatise;
    }
    private final ChapterFileInfoDAO fileInfoDAO;
    private void saveTreatiseInSolr(Treatise treatise){
        TreatiseSolrDoc treatiseSolrDoc = new TreatiseSolrDoc();
        StringBuilder sb = new StringBuilder();
        List<ChapterFileInfo> list = fileInfoDAO.findByTreatiseIdOrderBySeqAsc(treatise.getId());
        for(ChapterFileInfo d : list){
            sb.append(d.getContent());
        }
        treatiseSolrDoc.setId(String.valueOf(treatise.getId()));
        treatiseSolrDoc.setAuthor(authorDAO.findOne(treatise.getAuthorId()).getNickname());
        treatiseSolrDoc.setPublishDate(treatise.getPublishDate());
        treatiseSolrDoc.setCategoryId(treatise.getCategoryId());
        treatiseSolrDoc.setPCategoryId(categoryDAO.findById(treatise.getCategoryId()).getPid());
        treatiseSolrDoc.setDescription(treatise.getDescription());
        treatiseSolrDoc.setCategoryName(categoryDAO.findById(treatise.getCategoryId()).getName());
        treatiseSolrDoc.setTitle(treatise.getBookName());
        treatiseSolrDoc.setContent(sb.toString());
        treatiseSolrDoc.setPublishHouse(treatise.getPublishHouse());
        treatiseSolrDoc.setLanguage(treatise.getLanguage());
        log.info("sent to solr treatiseSolrDoc = {}",treatiseSolrDoc);
        treatiseSolrRepository.save(treatiseSolrDoc);
    }

    public TreatiseDTO updateCore(TreatiseUpdateReq treatiseUpdateReq) {
        Treatise treatise = findById(treatiseUpdateReq.getId());
        treatise.setISBN(treatiseUpdateReq.getISBN());
        treatise.setNo(treatiseUpdateReq.getNo());
        treatise.setBookName(treatiseUpdateReq.getBookName());
        treatise.setAuthorId(treatiseUpdateReq.getAuthorId());
        treatise.setLanguage(treatiseUpdateReq.getLanguage());
        return null;
    }

    /**
     * 搜索著作点击量
     * @param hitsSearchReq 其他条件
     * @param basicSearchReq 基本条件
     * @return Page<HitsDTO>
     */
    public Page<HitsDTO> findHitsPage(HitsSearchReq hitsSearchReq, BasicSearchReq basicSearchReq) {
        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        PageRequest pageRequest = new PageRequest(pageNumber - 1, basicSearchReq.getPageSize(), new Sort(Sort.Direction.DESC, "count"));

        //除了分页条件没有特定搜索条件的，为了缓存count
        if (ObjectConvertUtil.objectFieldIsBlank(hitsSearchReq)){
            log.debug("没有特定搜索条件的");
            Page<TreatiseHits> hitsPage = hitsDAO.findAll(pageRequest);
            return hitsPage.map(this::getHitsDTO);
        }

        //有了其它搜索条件，先搜索出著作，再查询对应著作的点击量
        Map<String, Object> searchParams = new HashMap<>();
        if (StringUtils.isNotBlank(hitsSearchReq.getISBN())) {
            searchParams.put("EQ_ISBN", hitsSearchReq.getISBN());
        }
        if (StringUtils.isNotBlank(hitsSearchReq.getKeyword())) {
            searchParams.put("LIKE_keywords", hitsSearchReq.getKeyword());
        }

        log.debug("【搜索著作点击量】 SearchParams: {}", searchParams.toString());
        Page<Treatise> treatisePage = super.findAllBySearchParams(searchParams, pageNumber, basicSearchReq.getPageSize());

        return treatisePage.map(treatise -> {
            TreatiseHits hits = hitsDAO.findOne(treatise.getId());
            return getHitsDTO(hits);
        });
    }
    private HitsDTO getHitsDTO(TreatiseHits source) {
        HitsDTO hitsDTO = new HitsDTO();
        ObjectConvertUtil.objectCopy(hitsDTO, source);
        Treatise treatise = treatiseDAO.findOne(source.getId());
        if (treatise != null) {
            hitsDTO.setISBN(treatise.getISBN());
            hitsDTO.setKeywords(treatise.getKeywords());
        }
        return hitsDTO;
    }
    /**
     * 搜索著作阅读量
     * @param readingSearchReq 其他条件
     * @param basicSearchReq 基本条件
     * @return Page<HitsDTO>
     */
    public Page<ReadingDTO> findReadingPage(ReadingSearchReq readingSearchReq, BasicSearchReq basicSearchReq) {
        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        PageRequest pageRequest = new PageRequest(pageNumber - 1, basicSearchReq.getPageSize(), new Sort(Sort.Direction.DESC, "count"));

        //除了分页条件没有特定搜索条件的，为了缓存count
        if (ObjectConvertUtil.objectFieldIsBlank(readingSearchReq)){
            log.debug("没有特定搜索条件的");
            Page<TreatiseReading> readingPage = readingDAO.findAll(pageRequest);
            return readingPage.map(this::getReadingDTO);
        }

        //有了其它搜索条件，先搜索出著作，再查询对应著作的点击量
        Map<String, Object> searchParams = new HashMap<>();
        if (StringUtils.isNotBlank(readingSearchReq.getISBN())) {
            searchParams.put("EQ_ISBN", readingSearchReq.getISBN());
        }
        if (StringUtils.isNotBlank(readingSearchReq.getKeyword())) {
            searchParams.put("LIKE_keywords", readingSearchReq.getKeyword());
        }

        log.debug("【搜索著作阅读量】 SearchParams: {}", searchParams.toString());
        Page<Treatise> treatisePage = super.findAllBySearchParams(searchParams, pageNumber, basicSearchReq.getPageSize());

        return treatisePage.map(treatise -> {
            TreatiseReading reading = readingDAO.findOne(treatise.getId());
            return getReadingDTO(reading);
        });
    }
    private ReadingDTO getReadingDTO(TreatiseReading source) {
        ReadingDTO readingDTO = new ReadingDTO();
        ObjectConvertUtil.objectCopy(readingDTO, source);
        Treatise treatise = treatiseDAO.findOne(source.getId());
        if (treatise != null) {
            readingDTO.setISBN(treatise.getISBN());
            readingDTO.setKeywords(treatise.getKeywords());
        }
        return readingDTO;
    }

    @Transactional
    public void addHitsForTreatise(Long id, int x) {
        TreatiseHits hits = hitsDAO.findOne(id);
        hits.setCount(hits.getCount() + x);
        hitsDAO.save(hits);
    }

    @Transactional
    public void addReadingForTreatise(Long treatiseId, int x) {
        TreatiseReading reading = readingDAO.findOne(treatiseId);
        reading.setCount(reading.getCount() + x);
        readingDAO.save(reading);
    }
}
