package org.appmockito.examples.services;

import org.appmockito.examples.models.Exam;
import org.appmockito.examples.repositories.ExamRepository;
import org.appmockito.examples.repositories.ExamRepositoryImpl2;
import org.appmockito.examples.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ExamServiceImplTest {
    ExamRepository repository;
    ExamService service;
    QuestionRepository questionRepository;

    @BeforeEach
    void setUp() {
        repository = mock(ExamRepositoryImpl2.class);
        questionRepository = mock(QuestionRepository.class);
        service = new ExamServiceImpl(repository, questionRepository);
    }

    @Test
    void findExamByName() {
        when(repository.findAll()).thenReturn(Data.EXAMS);

        Optional<Exam> exam = service.findExamByName("Math");

        assertTrue(exam.isPresent());
        assertEquals(5L, exam.orElseThrow().getId());
        assertEquals("Math", exam.orElseThrow().getName());
    }

    @Test
    void findExamByNameEmptyList() {
        List<Exam> data =  Collections.emptyList();
        when(repository.findAll()).thenReturn(data);

        Optional<Exam> exam = service.findExamByName("Math");

        assertFalse(exam.isPresent());
    }

    @Test
    void testExamQuestions() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        Exam exam = service.findExamByNameWithQuestions("History");
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("arithmetic"));

    }
}