package com.cafa.pdf.core.commom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 章节转换类
 * @author Cherish
 * @version 1.0
 * @date 2017/4/18 23:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDTO implements java.io.Serializable {

    private static final long serialVersionUID = -7470016484451785268L;
    private Long id;
    /**
     * 著作ID
     */
    private Long treatiseId;
    /**
     * 自定义排序，第几章
     */
    private Integer seq;
    /**
     * 章标题
     */
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
