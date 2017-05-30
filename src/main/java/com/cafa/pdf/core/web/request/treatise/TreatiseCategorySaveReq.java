package com.cafa.pdf.core.web.request.treatise;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;

/**
 * 著作类别保存的参数
 * Created by Cherish on 2017/2/6.
 */
@Data
public class TreatiseCategorySaveReq implements java.io.Serializable {

    private static final long serialVersionUID = 9170373893193352781L;

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
