package com.cafa.pdf.core.web.request.treatise;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;

/**
 * 著作类别修改参数
 * Created by Cherish on 2017/2/6.
 */
@Data
public class TreatiseCategoryUpdateReq implements java.io.Serializable {

    private static final long serialVersionUID = 3239377781964051307L;
    @Min(value = 1, message = "{user.id}")
    private Long id;
    /**
     * 父类别ID，0代表顶级类别
     */
    @Min(value = 0, message = "{user.id}")
    private Long pid;
    /**
     * 分类号
     */
    @Length(min = 1, max = 16, message="必须是1~16个字符")
    private String classifiedNum;
    /**
     * 分类名
     */
    @Length(min = 1, max = 32, message="必须是1~32个字符")
    private String name;


}
