package com.clairvoyant.naijamenu.bean;

public class SurveyQuestionResponse {

    private String status;
    private String message;
    private SurveyQuestionList[] surveyQuestionList;
    private AnswerList[] answerList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SurveyQuestionList[] getSurveyQuestionList() {
        return surveyQuestionList;
    }

    public void setSurveyQuestionList(SurveyQuestionList[] surveyQuestionList) {
        this.surveyQuestionList = surveyQuestionList;
    }

    public AnswerList[] getAnswerList() {
        return answerList;
    }

    public void setAnswerList(AnswerList[] answerList) {
        this.answerList = answerList;
    }
}