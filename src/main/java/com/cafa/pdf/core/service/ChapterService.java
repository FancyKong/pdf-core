/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.ChapterDTO;
import com.cafa.pdf.core.dal.dao.ChapterDAO;
import com.cafa.pdf.core.dal.dao.IBaseDAO;
import com.cafa.pdf.core.dal.entity.Chapter;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import com.cafa.pdf.core.web.request.chapter.ChapterReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 章节逻辑
 * @author Cherish
 * @version 1.0
 * @date 2017/4/18 23:25
 */
@Service
@Transactional(readOnly = true)
public class ChapterService extends ABaseService<Chapter, Long> {

    private final ChapterDAO chapterDAO;

    @Autowired
    public ChapterService(ChapterDAO chapterDAO) {
        this.chapterDAO = chapterDAO;
    }

    @Override
    protected IBaseDAO<Chapter, Long> getEntityDAO() {
        return chapterDAO;
    }

    public List<ChapterDTO> findByTreatiseId(Long treatiseId) {
        List<Chapter> chapters = chapterDAO.findByTreatiseIdOrderBySeqAsc(treatiseId);
        if (chapters == null || chapters.isEmpty()) {
            return null;
        }
        return chapters.stream().map(this::getChapterDTO).collect(Collectors.toList());
    }

    public Long getCount() {
        return chapterDAO.count();
    }

    @Transactional
    public void update(ChapterReq chapterReq) {
        Chapter chapter = findById(chapterReq.getId());
        ObjectConvertUtil.objectCopy(chapter, chapterReq);
        this.update(chapter);
    }

    @Transactional
    public void save(ChapterReq chapterReq) {
        Chapter chapter = new Chapter();
        ObjectConvertUtil.objectCopy(chapter, chapterReq);
        this.save(chapter);
    }

    @Transactional
    public void update(List<ChapterReq> chapterReqList) {
        List<Chapter> chapters = chapterReqList.stream().map(chapterReq -> {
            Chapter chapter = new Chapter();
            ObjectConvertUtil.objectCopy(chapter, chapterReq);
            return chapter;
        }).collect(Collectors.toList());

        chapterDAO.save(chapters);
    }

    @Transactional
    public void deleteAllByTreatiseId(Long treatiseId) {
        chapterDAO.deleteByTreatiseId(treatiseId);
    }

    /**
     * 保存章节信息
     * @param chapterReqList List<ChapterReq>
     */
    @Transactional
    public void saveChapters(List<ChapterReq> chapterReqList) {
        List<Chapter> chapters = chapterReqList.stream().map(chapterReq -> {
            Chapter chapter = new Chapter();
            ObjectConvertUtil.objectCopy(chapter, chapterReq);
            return chapter;
        }).collect(Collectors.toList());

        chapterDAO.save(chapters);
    }

    /**
     * 转换
     * @param source Chapter
     * @return ChapterDTO
     */
    private ChapterDTO getChapterDTO(Chapter source) {
        ChapterDTO chapterDTO = new ChapterDTO();
        ObjectConvertUtil.objectCopy(chapterDTO, source);
        return chapterDTO;
    }


}
