package com.cafa.pdf.core.web.request.chapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/4/20 20:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterListReq implements java.io.Serializable {

    private static final long serialVersionUID = -2613034715756367221L;
    private List<ChapterReq> chapters;
    private Long treatiseId;
}

