package com.cafa.pdf.core.web.request.treatise;

import com.cafa.pdf.core.web.request.chapter.ChapterReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/4/19 9:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatiseAndChapterReq implements java.io.Serializable {

    private static final long serialVersionUID = -9091028453948077071L;
    @Min(value = 1,message = "著作出错")
    private Long treatiseId;

    private List<ChapterReq> chapterReqList;

}
