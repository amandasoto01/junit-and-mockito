package org.appmockito.examples.services;

import org.appmockito.examples.models.Exam;
import org.appmockito.examples.repositories.ExamRepository;

import java.util.Optional;

public class ExamServiceImpl implements ExamService{

    private ExamRepository examRepository;

    public ExamServiceImpl(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public Exam findExamByName(String name) {
        Optional<Exam> examOptional = examRepository.findAll().stream()
                .filter( e -> e.getName().contains(name))
                .findFirst();
        Exam exam = null;
        if(examOptional.isPresent()) {
            exam = examOptional.orElseThrow();
        }
        return exam;
    }
}
