package com.cafa.pdf.core.dal.dao;

import com.cafa.pdf.core.dal.entity.Chapter;
import com.cafa.pdf.core.dal.entity.TreatiseReading;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReadingDAO extends IBaseDAO<TreatiseReading, Long> {
    List<TreatiseReading> findTop10ByOrderByCount();
}
