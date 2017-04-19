package com.cafa.pdf.core.web.request.chapter;

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
public class ChapterReq implements java.io.Serializable {

    private static final long serialVersionUID = -6399342009699033293L;
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
    /**
     * 操作，
     * 0：为新增
     * 1：更新
     * 2：删除
     * 其它：不操作
     */
    private Integer operate;


}
