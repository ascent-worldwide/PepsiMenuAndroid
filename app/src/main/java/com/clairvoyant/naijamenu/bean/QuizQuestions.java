package com.clairvoyant.naijamenu.bean;

public class QuizQuestions {

    private int questionId;
    private String questionText;
    private QuizAnswers[] answerList;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuizAnswers[] getAnswerList() {
        return answerList;
    }

    public void setAnswerList(QuizAnswers[] answerList) {
        this.answerList = answerList;
    }
}