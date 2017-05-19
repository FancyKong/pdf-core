package com.cafa.pdf.core.commom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/5/19 15:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatiseAndChaptersDTO implements java.io.Serializable {

    private static final long serialVersionUID = -337814872209744633L;
    private TreatiseDTO treatise;

    private List<ChapterDTO> chapters;

}
