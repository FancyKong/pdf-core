package com.cafa.pdf.core.commom.dto;

import lombok.Data;

/**
 * 著作类别
 * Created by Cherish on 2017/2/6.
 */
@Data
public class TreatiseCategoryDTO implements java.io.Serializable{

    private static final long serialVersionUID = -6048886061013170445L;
    private Long id;

    private String classifiedNum;

    private String name;

    private String description;

}
