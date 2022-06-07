package org.appmockito.examples.services;

import org.appmockito.examples.models.Exam;
import org.appmockito.examples.repositories.ExamRepository;
import org.appmockito.examples.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

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
    @Captor
    ArgumentCaptor<Long> captor;

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
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        // When
        Exam exam = service.findExamByNameWithQuestions("Math");

        // Then
        assertNull(exam);

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(5L);
    }

    @Test
    void testSaveExam() {
        // Given
        Exam newExam = Data.EXAM;
        newExam.setQuestions(Data.QUESTIONS);

        when(repository.save(any(Exam.class))).then(new Answer<Exam>(){
            Long sequence = 8L;
            @Override
            public Exam answer(InvocationOnMock invocation) throws Throwable {
                Exam exam = invocation.getArgument(0);
                exam.setId(sequence++);
                return exam;
            }
        });

        // When
        Exam exam = service.save(newExam);

        // Then
        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Physics", exam.getName());

        verify(repository).save(any(Exam.class));
        verify(questionRepository).saveQuestions(anyList());
    }

    @Test
    void manageExceptions() {
        when(repository.findAll()).thenReturn(Data.EXAMS_ID_NULL);
        when(questionRepository.findQuestionsByExamId(isNull())).thenThrow(IllegalArgumentException.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamByNameWithQuestions("Math");
        });
        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        service.findExamByNameWithQuestions("Math");

        verify(repository).findAll();
        //verify(questionRepository).findQuestionsByExamId(argThat(arg -> arg != null && arg.equals(5L)));
        verify(questionRepository).findQuestionsByExamId(argThat(arg -> arg != null && arg >= 5L));
        //verify(questionRepository).findQuestionsByExamId(eq(5L));
    }

    @Test
    void testArgumentMatchers2() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        service.findExamByNameWithQuestions("Math");

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(argThat(new MyArgumentMatchers()));
    }

    @Test
    void testArgumentMatchers3() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        service.findExamByNameWithQuestions("Math");

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(argThat( argument -> argument != null && argument > 0));
    }

    public static class MyArgumentMatchers implements ArgumentMatcher<Long> {
        private Long argument;
        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "Personalized error message that mockito prints " +
                    " in case that the test fail " + argument +
                    " must be a positive integer";
        }
    }

    @Test
    void testArgumentCapture() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        //when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        service.findExamByNameWithQuestions("Math");

        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(questionRepository).findQuestionsByExamId(captor.capture());

        assertEquals(5L, captor.getValue());
    }

    @Test
    void testDoThrow() {
        Exam exam = Data.EXAM;
        exam.setQuestions(Data.QUESTIONS);

        doThrow(IllegalArgumentException.class).when(questionRepository).saveQuestions(anyList());
        assertThrows(IllegalArgumentException.class, () -> {
            service.save(exam);
        });
    }
}