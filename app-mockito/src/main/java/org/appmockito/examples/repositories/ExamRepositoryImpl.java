package org.appmockito.examples.repositories;

import org.appmockito.examples.models.Exam;

import java.util.Arrays;
import java.util.List;

public class ExamRepositoryImpl implements  ExamRepository{
    @Override
    public List<Exam> findAll() {
        return Arrays.asList(new Exam(5L, "Math"), new Exam(6L, "Languages"), new Exam(7L, "History"));
    }
}
