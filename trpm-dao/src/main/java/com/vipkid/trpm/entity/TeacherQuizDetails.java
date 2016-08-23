package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.community.dao.support.Entity;

public final class TeacherQuizDetails extends Entity implements Serializable {

    private static final long serialVersionUID = -6323652224684105945L;
    /*  */
    private int id;
    /*  */
    private long teacherId;
    /*  */
    private String sn;
    /*  */
    private int teacherAnswer;
    /*  */
    private int correctAnswer;
    /*  */
    private int quizId;
    /*  */
    private int score;

    public int getId() {
        return this.id;
    }

    public TeacherQuizDetails setId(int id) {
        this.id = id;
        return this;
    }

    public long getTeacherId() {
        return this.teacherId;
    }

    public TeacherQuizDetails setTeacherId(long teacherId) {
        this.teacherId = teacherId;
        return this;
    }

    public String getSn() {
        return this.sn;
    }

    public TeacherQuizDetails setSn(String sn) {
        this.sn = sn;
        return this;
    }

    public int getTeacherAnswer() {
        return this.teacherAnswer;
    }

    public TeacherQuizDetails setTeacherAnswer(int teacherAnswer) {
        this.teacherAnswer = teacherAnswer;
        return this;
    }

    public int getCorrectAnswer() {
        return this.correctAnswer;
    }

    public TeacherQuizDetails setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
        return this;
    }

    public int getQuizId() {
        return this.quizId;
    }

    public TeacherQuizDetails setQuizId(int quizId) {
        this.quizId = quizId;
        return this;
    }

    public int getScore() {
        return this.score;
    }

    public TeacherQuizDetails setScore(int score) {
        this.score = score;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
