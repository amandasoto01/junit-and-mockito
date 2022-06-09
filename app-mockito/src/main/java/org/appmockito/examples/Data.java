package org.appmockito.examples;

import org.appmockito.examples.models.Exam;

import java.util.Arrays;
import java.util.List;

public class Data {
    public final static List<Exam> EXAMS = Arrays.asList(new Exam(5L, "Math"), new Exam(6L, "Languages"), new Exam(7L, "History"));
    public final static List<String> QUESTIONS = Arrays.asList("arithmetic","integrals","derivatives","trigonometry","geometry");
    public final static Exam EXAM = new Exam(null, "Physics");
    public final static List<Exam> EXAMS_ID_NULL = Arrays.asList(new Exam(null, "Math"), new Exam(null, "Languages"), new Exam(null, "History"));
    public final static List<Exam> EXAMS_NEGATIVE_ID = Arrays.asList(new Exam(-5L, "Math"), new Exam(-6L, "Languages"), new Exam(-7L, "History"));

}
