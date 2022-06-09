package org.appmockito.examples.repositories;

import org.appmockito.examples.Data;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuestionRepositoryImpl implements QuestionRepository{

    @Override
    public List<String> findQuestionsByExamId(Long id) {
        System.out.println("QuestionRepositoryImpl.findQuestionsByExamId");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Data.QUESTIONS;
    }

    @Override
    public void saveQuestions(List<String> questions) {
        System.out.println("QuestionRepositoryImpl.saveQuestions");
    }
}
