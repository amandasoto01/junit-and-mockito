package org.appmockito.examples.services;

import org.appmockito.examples.models.Exam;
import org.appmockito.examples.repositories.ExamRepository;
import org.appmockito.examples.repositories.QuestionRepository;

import java.util.List;
import java.util.Optional;

public class ExamServiceImpl implements ExamService{

    private ExamRepository examRepository;
    private QuestionRepository questionRepository;

    public ExamServiceImpl(ExamRepository examRepository, QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Optional<Exam> findExamByName(String name) {
       return examRepository.findAll().stream()
                .filter( e -> e.getName().contains(name))
                .findFirst();
    }

    @Override
    public Exam findExamByNameWithQuestions(String name) {
        Optional<Exam> optionalExam = findExamByName(name);
        Exam exam = null;

        if(optionalExam.isPresent()){
            exam = optionalExam.orElseThrow();
            List<String> questions = questionRepository.findQuestionsByExamId(exam.getId());
            exam.setQuestions(questions);
        }

        return exam;
    }
}
