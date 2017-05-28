/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.ChapterDTO;
import com.cafa.pdf.core.commom.exception.ServiceException;
import com.cafa.pdf.core.dal.dao.ChapterDAO;
import com.cafa.pdf.core.dal.dao.ChapterFileInfoDAO;
import com.cafa.pdf.core.dal.dao.IBaseDAO;
import com.cafa.pdf.core.dal.entity.Chapter;
import com.cafa.pdf.core.dal.entity.ChapterFileInfo;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import com.cafa.pdf.core.web.request.chapter.ChapterReq;
import com.google.common.base.Throwables;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    public Chapter saveChapter(ChapterReq chapterReq,MultipartFile multipartFile) {
        Chapter chapter = new Chapter();
        ObjectConvertUtil.objectCopy(chapter, chapterReq);
        File directory = new File(FILE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        chapter = this.save(chapter);
        try {
            uploadPdf(multipartFile, chapter);
        } catch (Exception e) {
            log.error("【章节上传】 {}", Throwables.getStackTraceAsString(e));
            throw new ServiceException("500", "文件解析出错");
        }
        return chapter;
    }

    @Value("${file.path}")
    private String FILE_PATH;

    /**
     * 上传章节的pdf
     * @param multipartFile 文件
     * @param chapter ChapterReq
     */
    private void uploadPdf(MultipartFile multipartFile, Chapter chapter) throws Exception {
        if (multipartFile.isEmpty()) {
            throw new ServiceException("403", "没有文件数据");
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String extendName = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!".pdf".equals(extendName)) {
            throw new ServiceException("403", "非pdf文件");
        }
        long treatiseId = chapter.getTreatiseId();
        int chapterSeq = chapter.getSeq();
        File directory = new File(FILE_PATH+treatiseId+"/"+chapterSeq+"/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        PdfReader reader = new PdfReader(multipartFile.getInputStream());
        ChapterFileInfo chapterFileInfo = new ChapterFileInfo(chapter.getId(),
                chapter.getTreatiseId(),
                chapter.getSeq(),
                chapter.getTitle(),
                multipartFile.getOriginalFilename(),
                chapter.getPrivacy(),
                reader.getNumberOfPages(),
                splitPDFAndGetContent(reader, directory));
        fileInfoDAO.save(chapterFileInfo);
    }
    @Autowired
    private ChapterFileInfoDAO fileInfoDAO;
    private String splitPDFAndGetContent(PdfReader pdfReader, File filePath) throws IOException, DocumentException {
        int pages = pdfReader.getNumberOfPages();
        StringBuilder solrContent = new StringBuilder();
        for (int i = 1; i < pages + 1; i++) {
            //获取任意一页的规格
            Document document = new Document(pdfReader.getPageSize(1));
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(filePath+"/"+i+".pdf"));
            document.open();
            document.newPage();
            PdfImportedPage page = copy.getImportedPage(pdfReader, i);
            solrContent.append(PdfTextExtractor.getTextFromPage(pdfReader,i));
            copy.addPage(page);
            copy.close();
            document.close();
        }
        pdfReader.close();
        return solrContent.toString();
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

    @Transactional(readOnly = false)
    public void delete(Long id) {
        Chapter chapter = findById(id);
        if(chapter != null){
            getEntityDAO().delete(id);
        }
    }

}
