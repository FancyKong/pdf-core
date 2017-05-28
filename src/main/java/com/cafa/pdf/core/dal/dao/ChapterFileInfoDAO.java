package com.cafa.pdf.core.dal.dao;

import com.cafa.pdf.core.dal.entity.Chapter;
import com.cafa.pdf.core.dal.entity.ChapterFileInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChapterFileInfoDAO extends IBaseDAO<ChapterFileInfo, Long> {
    List<ChapterFileInfo> findByTreatiseIdOrderBySeqAsc(Long treatiseId);
}
