package com.example.biblicalstudies;

import java.io.Serializable;

public class Lock implements Serializable {

    private String question;
    private Integer CorrectAnswer;
    private String op1, op2, op3, op4;

    public Lock() {
    }

    public Lock(String question, Integer correctAnswer, String op1, String op2, String op3, String op4) {
        this.question = question;
        CorrectAnswer = correctAnswer;
        this.op1 = op1;
        this.op2 = op2;
        this.op3 = op3;
        this.op4 = op4;
    }

    public String getQuestion() {
        return question;
    }

    public Integer getCorrectAnswer() {
        return CorrectAnswer;
    }

    public String getOp1() {
        return op1;
    }

    public String getOp2() {
        return op2;
    }

    public String getOp3() {
        return op3;
    }

    public String getOp4() {
        return op4;
    }
}
