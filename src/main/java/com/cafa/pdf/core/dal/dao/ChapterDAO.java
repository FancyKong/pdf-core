package com.cafa.pdf.core.dal.dao;

import com.cafa.pdf.core.dal.entity.Chapter;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChapterDAO extends IBaseDAO<Chapter, Long> {

    List<Chapter> findByTreatiseIdOrderBySeqAsc(Long treatiseId);

    @Query("delete from Chapter as c where c.treatiseId=:treatiseId")
    @Modifying
    void deleteByTreatiseId(@Param("treatiseId") Long treatiseId);

    Chapter findByTreatiseIdAndSeq(Long treatise, Integer seq);

    Integer countChaptersByTreatiseId(Long treatiseId);
}
