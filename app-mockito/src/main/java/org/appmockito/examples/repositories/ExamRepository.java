package org.appmockito.examples.repositories;

import org.appmockito.examples.models.Exam;

import java.util.List;

public interface ExamRepository {

    Exam save(Exam exam);
    List<Exam> findAll();
}
