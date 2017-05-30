package com.cafa.pdf.core.dal.dao;

import com.cafa.pdf.core.dal.entity.TreatiseReading;

import java.util.List;

public interface ReadingDAO extends IBaseDAO<TreatiseReading, Long> {
    List<TreatiseReading> findTop10ByOrderByCountDesc();
}
