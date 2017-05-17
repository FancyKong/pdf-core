package com.cafa.pdf.core.web.request;

import lombok.Data;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/5/17 20:29
 */
@Data
public class OverridePwd implements java.io.Serializable {

    private static final long serialVersionUID = -1054735630007884579L;

    private Long checkId;

    private String key;

    private String newPwd;

    private String repeatPwd;


}
