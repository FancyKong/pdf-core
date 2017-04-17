package com.cafa.pdf.core.web.request.treatise;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 著作类别保存的参数
 * Created by Cherish on 2017/2/6.
 */
@Data
public class TreatiseCategorySaveReq implements java.io.Serializable {

    private static final long serialVersionUID = 9170373893193352781L;
    @Length(min = 1, max = 16, message="必须是1~16个字符")
    private String name;

    @Length(min = 0, max = 1024, message = "{user.description}")
    private String description;

}
