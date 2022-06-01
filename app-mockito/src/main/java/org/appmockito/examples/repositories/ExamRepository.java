package org.appmockito.examples.repositories;

import org.appmockito.examples.models.Exam;

import java.util.List;

public interface ExamRepository {
    List<Exam> findAll();
}
