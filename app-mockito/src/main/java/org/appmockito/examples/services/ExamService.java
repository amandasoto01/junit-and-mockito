package org.appmockito.examples.services;

import org.appmockito.examples.models.Exam;

public interface ExamService {

    Exam findExamByName(String name);

}
