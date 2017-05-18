package com.cafa.pdf.core.web.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Cherish on 2017/2/24.
 */
@Data
@AllArgsConstructor
public class Response<T> implements java.io.Serializable {
    private static final long serialVersionUID = -222983483999088181L;
    private String code;
    private Boolean success;
    private String message;
    private T data;
}
