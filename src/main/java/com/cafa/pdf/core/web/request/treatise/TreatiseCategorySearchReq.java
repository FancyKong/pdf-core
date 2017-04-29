package com.cafa.pdf.core.web.request.treatise;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 著作类别搜索
 * Created by Cherish on 2017/2/6.
 */
@Data
public class TreatiseCategorySearchReq implements java.io.Serializable {

    private static final long serialVersionUID = -6549926745989510926L;

    @Length(min = 1, max = 16, message="必须是1~16个字符")
    private String classifiedNum;

    @Length(min = 1, max = 32, message="必须是1~32个字符")
    private String name;

}
