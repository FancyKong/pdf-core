package com.cafa.pdf.core.dal.dao;

import com.cafa.pdf.core.dal.entity.Chapter;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChapterDAO extends IBaseDAO<Chapter, Long> {

    @Query("select new Chapter(c.id,c.pages,c.privacy,c.seq,c.title,c.treatiseId) from Chapter c where c.treatiseId = :treatiseId order by c.seq asc ")
    List<Chapter> findByTreatiseIdOrderBySeqAsc(@Param("treatiseId") Long treatiseId);

    @Query("delete from Chapter as c where c.treatiseId=:treatiseId")
    @Modifying
    void deleteByTreatiseId(@Param("treatiseId") Long treatiseId);

    @Query("select c.content from Chapter c where c.id = :treatiseId")
    String findContentById(@Param("treatiseId")Long treatiseId);

}
