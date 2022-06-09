package org.appmockito.examples.services;

import org.appmockito.examples.Data;
import org.appmockito.examples.models.Exam;
import org.appmockito.examples.repositories.ExamRepository;
import org.appmockito.examples.repositories.ExamRepositoryImpl;
import org.appmockito.examples.repositories.QuestionRepository;
import org.appmockito.examples.repositories.QuestionRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplSpyTest {
    @Spy
    ExamRepositoryImpl repository;
    @Spy
    QuestionRepositoryImpl questionRepository;
    @InjectMocks
    ExamServiceImpl service;

    @BeforeEach
    void setUp() {
        //repository = mock(ExamRepositoryImpl.class);
        //questionRepository = mock(QuestionRepositoryImpl.class);
        //service = new ExamServiceImpl(repository, questionRepository);

        //MockitoAnnotations.openMocks(this);

    }

    @Test
    void testSpy() {
        List<String> questions = Arrays.asList("arithmetic");
        //when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(questions);
        doReturn(questions).when(questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = service.findExamByNameWithQuestions("Math");

        assertEquals(5L, exam.getId());
        assertEquals("Math", exam.getName());
        assertEquals(1, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("arithmetic"));

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(anyLong());
    }
}