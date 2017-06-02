package com.cafa.pdf.core.dal.dao;

import com.cafa.pdf.core.dal.entity.ChapterFileInfo;

import java.util.List;

public interface ChapterFileInfoDAO extends IBaseDAO<ChapterFileInfo, Long> {
    List<ChapterFileInfo> findByTreatiseIdOrderBySeqAsc(Long treatiseId);
    void deleteAllByTreatiseId(Long treatiseId);

}
