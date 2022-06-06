package org.appmockito.examples.services;

import org.appmockito.examples.models.Exam;

import java.util.Optional;

public interface ExamService {

    Optional<Exam> findExamByName(String name);
    Exam findExamByNameWithQuestions(String name);

}
