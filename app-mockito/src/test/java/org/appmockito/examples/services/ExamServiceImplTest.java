package org.appmockito.examples.services;

import org.appmockito.examples.models.Exam;
import org.appmockito.examples.repositories.ExamRepository;
import org.appmockito.examples.repositories.ExamRepositoryImpl2;
import org.appmockito.examples.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {
    @Mock
    ExamRepository repository;
    @Mock
    QuestionRepository questionRepository;
    @InjectMocks
    ExamServiceImpl service;

    @BeforeEach
    void setUp() {
        //repository = mock(ExamRepositoryImpl2.class);
        //questionRepository = mock(QuestionRepository.class);
        //service = new ExamServiceImpl(repository, questionRepository);

        //MockitoAnnotations.openMocks(this);

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

        Exam exam = service.findExamByNameWithQuestions("Math");
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("arithmetic"));
    }

    @Test
    void testExamQuestionsVerify() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        Exam exam = service.findExamByNameWithQuestions("Math");

        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("arithmetic"));

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(anyLong());
    }

    @Test
    void testDoesNotExistExamVerify() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        Exam exam = service.findExamByNameWithQuestions("Math");

        assertNull(exam);

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(5L);
    }

    @Test
    void testSaveExam() {
        Exam newExam = Data.EXAM;
        newExam.setQuestions(Data.QUESTIONS);

        when(repository.save(any(Exam.class))).thenReturn(Data.EXAM);
        Exam exam = service.save(newExam);

        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Physics", exam.getName());

        verify(repository).save(any(Exam.class));
        verify(questionRepository).saveQuestions(anyList());
    }
}