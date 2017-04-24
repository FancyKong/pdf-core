package com.cafa.pdf.core.web;

import com.cafa.pdf.core.commom.exception.ServiceException;
import com.cafa.pdf.core.dal.solr.document.ChapterSolrDoc;
import com.cafa.pdf.core.dal.solr.document.TreatiseSolrDoc;
import com.cafa.pdf.core.dal.solr.repository.ChapterSolrRepository;
import com.cafa.pdf.core.dal.solr.repository.TreatiseSolrRepository;
import com.cafa.pdf.core.service.ChapterService;
import com.cafa.pdf.core.web.request.chapter.ChapterListReq;
import com.cafa.pdf.core.web.request.chapter.ChapterReq;
import com.cafa.pdf.core.web.response.Response;
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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        try {
            chapterService.delete(chapterId);
            return buildResponse(Boolean.TRUE, "删除 成功", null);
        } catch (Exception e) {
            log.error("删除失败:{}", e.getMessage());
            return buildResponse(Boolean.FALSE, "删除失败", null);
        }
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
            chapterService.update(chapterReq);
            mv.addObject("chapter", chapterService.findById(chapterReq.getId()));
            errorMap.put("msg", "修改成功");
        } catch (Exception e) {
            errorMap.put("msg", "系统繁忙");
            log.error("修改错误:{}", e.getMessage());
        }
        return mv;
    }

    /**
     * 保存
     * @param chapterListReq 保存的信息
     * @return ModelAndView
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(@Validated @RequestBody ChapterListReq chapterListReq, BindingResult bindingResult) {
        log.info("【保存】 {}", chapterListReq);

        List<ChapterReq> chapterReqList = chapterListReq.getChapters();
        if (chapterReqList == null || chapterReqList.isEmpty()) {
            return buildResponse(Boolean.FALSE, "无章节信息", null);
        }
        try {
            // TODO solr 等 其它。。。
            chapterService.saveChapters(chapterReqList);

            return buildResponse(Boolean.TRUE, "保存成功", null);
        } catch (Exception e) {
            log.error("添加失败:{}", e.getMessage());
            return buildResponse(Boolean.FALSE, BUSY_MSG, null);
        }
    }


    //TODO 文件存放路径
    private static final String FILE_PATH = "/pdf-core/file/";

    private static int test = 1;
    /**
     * 上传章节的pdf
     * @param multipartFile 文件
     * @param request HttpServletRequest
     * @return url 文件存放路径
     */
    @PostMapping("/uploadPdf")
    @ResponseBody
    public String uploadPdf(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request){
        if (multipartFile.isEmpty()) {
            throw new ServiceException("403", "没有文件数据");
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String extendName = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!".pdf".equals(extendName)) {
            throw new ServiceException("403", "非pdf文件");
        }
        long treatiseId = Long.parseLong(request.getParameter("treatiseId"));
        int chapterSeq = Integer.parseInt(request.getParameter("seq"));
        File directory = new File(FILE_PATH+treatiseId+"/"+chapterSeq+"/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String url = "上传出错";
        try {
            String newFIleName = System.currentTimeMillis() + extendName;
            //multipartFile.transferTo(new File(directory, newFIleName));
            String solrContent = splitPDFAndGetContent(multipartFile.getInputStream(),
                    directory);
            saveChapterInSolr(solrContent,treatiseId,chapterSeq);
            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                    + request.getContextPath() + "/";
            url = basePath + "fileDownload?filename=" + newFIleName;
        } catch (Exception e) {
            log.error("上传错误 {}", e.getMessage());
        }
        return url;
    }

    private void saveChapterInSolr(String solrContent, long treatiseId, int chapterSeq) {
        //TODO　to find treatiseSolrId by treatiseId in mysql
        String treatiseSolrId = "abc";
        ChapterSolrDoc chapter = new ChapterSolrDoc();
        chapter.setId(UUID.randomUUID().toString().replace("-",""));
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
