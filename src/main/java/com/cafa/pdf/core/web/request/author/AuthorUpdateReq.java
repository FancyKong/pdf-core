package com.cafa.pdf.core.web.request.author;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

@Data
public class AuthorUpdateReq implements java.io.Serializable {

    private static final long serialVersionUID = -4468922410807772451L;
    @Min(value = 1, message = "{user.id}")
    private Long id;

    @Length(min = 1 ,max = 16 ,message = "{user.nickname}")
    private String nickname;

    @Range(min = 0, max = 1, message = "{user.active}")
    private Integer active;


}
