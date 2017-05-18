package com.cafa.pdf.core.web.request.chapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * 章节转换类
 * @author Cherish
 * @version 1.0
 * @date 2017/4/18 23:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterReq implements java.io.Serializable {

    private static final long serialVersionUID = -6399342009699033293L;
    private Long id;
    /**
     * 著作ID
     */
    @Min(1)
    private Long treatiseId;
    /**
     * 自定义排序，第几章
     */
    @Range(min = 1, max = 99, message = "章节序号为1-99")
    private Integer seq;
    /**
     * 章标题
     */
    @Length(min = 1, message = "请输入章标题")
    private String title;
    /**
     * 文件路径
     */
    private String url;
    /**
     * 是否被加密
     */
    private Integer privacy;

}
