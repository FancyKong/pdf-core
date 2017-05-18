package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.exception.ServiceException;
import com.cafa.pdf.core.dal.entity.Chapter;
import com.cafa.pdf.core.dal.solr.document.ChapterSolrDoc;
import com.cafa.pdf.core.dal.solr.document.TreatiseSolrDoc;
import com.cafa.pdf.core.dal.solr.repository.ChapterSolrRepository;
import com.cafa.pdf.core.dal.solr.repository.TreatiseSolrRepository;
import com.cafa.pdf.core.service.ChapterService;
import com.cafa.pdf.core.web.request.chapter.ChapterReq;
import com.cafa.pdf.core.web.response.Response;
import com.google.common.base.Throwables;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/4/20 0:20
 */
@Controller
@RequestMapping("chapter")
@RequiresAuthentication
public class ChapterController extends ABaseController {
    
    private final ChapterService chapterService;
    private final ChapterSolrRepository chapterSolrRepository;
    private final TreatiseSolrRepository treatiseSolrRepository;

    @Autowired
    public ChapterController(ChapterService chapterService, ChapterSolrRepository chapterSolrRepository, TreatiseSolrRepository treatiseSolrRepository) {
        this.chapterService = chapterService;
        this.chapterSolrRepository = chapterSolrRepository;
        this.treatiseSolrRepository = treatiseSolrRepository;
    }

    /**
     * 删除
     * @param chapterId ID
     * @return JSON
     */
    @DeleteMapping("/{chapterId}/delete")
    @ResponseBody
    public Response delete(@PathVariable("chapterId") Long chapterId) {
        //TODO 同时要删除solr中的数据
        chapterService.delete(chapterId);
        return buildResponse(Boolean.TRUE, "删除 成功", null);
    }

    /**
     * 更改信息
     * @param chapterReq 更新信息
     * @return ModelAndView
     */
    @PostMapping("/update")
    public ModelAndView update(@Validated ChapterReq chapterReq, BindingResult bindingResult) {
        log.info("【更改信息】 {}", chapterReq);

        ModelAndView mv = new ModelAndView("admin/chapter/edit");
        Map<String, Object> errorMap = new HashMap<>();
        mv.addObject("errorMap", errorMap);

        if (chapterReq == null || chapterReq.getId() == null) {
            errorMap.put("msg", "数据错误");
            return mv;
        }

        if (bindingResult.hasErrors()) {
            errorMap.putAll(getErrors(bindingResult));
            mv.addObject("chapter", chapterReq);
            return mv;
        }

        try {
            //TODO 同时要更新solr中的数据
            chapterService.update(chapterReq);
            mv.addObject("chapter", chapterService.findById(chapterReq.getId()));
            errorMap.put("msg", "修改成功");
        } catch (Exception e) {
            errorMap.put("msg", "系统繁忙");
            log.error("修改错误:{}", Throwables.getStackTraceAsString(e));
        }
        return mv;
    }

    /**
     * 保存
     * @param chapterReq 保存的信息
     * @return Response
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ChapterReq chapterReq, @RequestParam("pdf") MultipartFile multipartFile) {
        log.info("【保存】 {}", chapterReq);
        if (multipartFile.isEmpty()) {
            return buildResponse(Boolean.FALSE, "请上传文件", null);
        }

        File directory = new File(FILE_PATH);

//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String newFIleName;
        try {
            newFIleName = uploadPdf(multipartFile, chapterReq);
        } catch (Exception e) {
            log.error("【章节上传】 {}", Throwables.getStackTraceAsString(e));
            throw new ServiceException("500", "文件解析出错");
        }
        chapterReq.setUrl(newFIleName);
        Chapter chapter = chapterService.save(chapterReq);

        return buildResponse(Boolean.TRUE, "保存成功", chapter);
    }


    //TODO 文件存放路径
    private static final String FILE_PATH = "F:/pdf_core/file/";
    /**
     * 上传章节的pdf
     * @param multipartFile 文件
     * @param chapterReq ChapterReq
     * @return url 文件存放路径
     */
    private String uploadPdf(MultipartFile multipartFile, ChapterReq chapterReq) throws Exception {
        if (multipartFile.isEmpty()) {
            throw new ServiceException("403", "没有文件数据");
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String extendName = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!".pdf".equals(extendName)) {
            throw new ServiceException("403", "非pdf文件");
        }
        long treatiseId = chapterReq.getTreatiseId();
        int chapterSeq = chapterReq.getSeq();
        File directory = new File(FILE_PATH+treatiseId+"/"+chapterSeq+"/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String newFIleName = System.currentTimeMillis() + extendName;
        String solrContent = splitPDFAndGetContent(multipartFile.getInputStream(), directory);
        saveChapterInSolr(solrContent,treatiseId,chapterSeq);
        return newFIleName;
    }

    private void saveChapterInSolr(String solrContent, long treatiseId, int chapterSeq) {
        //TODO　to find treatiseSolrId by treatiseId in mysql
        String treatiseSolrId = treatiseId + "";

        ChapterSolrDoc chapter = new ChapterSolrDoc();
        chapter.setId(UUID.randomUUID().toString().replaceAll("-",""));
        chapter.setTreatiseId(treatiseSolrId);
        chapter.setContent(solrContent);
        chapter.setSeq(chapterSeq);
        chapterSolrRepository.save(chapter);
    }

    private void saveTreatiseInSolr(String treatiseSolrId){
        List<ChapterSolrDoc> list = chapterSolrRepository.findByTreatiseIdOrderBySeqAsc(treatiseSolrId);
        TreatiseSolrDoc treatiseSolrDoc = new TreatiseSolrDoc();
        StringBuilder sb = new StringBuilder();
        for(ChapterSolrDoc d : list){
            sb.append(d.getContent());
        }
        treatiseSolrDoc.setId(treatiseSolrId);
        treatiseSolrDoc.setContent(sb.toString());
        treatiseSolrRepository.save(treatiseSolrDoc);
    }


    private String splitPDFAndGetContent(InputStream in, File filePath) throws IOException, DocumentException {
        PdfReader pdfReader = new PdfReader(in);
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
            document.close();
        }
        return solrContent.toString();
    }

}
