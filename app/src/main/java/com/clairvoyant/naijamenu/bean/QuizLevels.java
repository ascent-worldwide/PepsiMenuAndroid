package com.clairvoyant.naijamenu.bean;

public class QuizLevels {

    private int levelId;
    private int levelNo;
    private long time;
    private int minQuesToPass;
    private int perQuestionscore;
    private QuizQuestions[] quizQuestions;

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getLevelNo() {
        return levelNo;
    }

    public void setLevelNo(int levelNo) {
        this.levelNo = levelNo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getMinQuesToPass() {
        return minQuesToPass;
    }

    public void setMinQuesToPass(int minQuesToPass) {
        this.minQuesToPass = minQuesToPass;
    }

    public int getPerQuestionscore() {
        return perQuestionscore;
    }

    public void setPerQuestionscore(int perQuestionscore) {
        this.perQuestionscore = perQuestionscore;
    }

    public QuizQuestions[] getQuizQuestions() {
        return quizQuestions;
    }

    public void setQuizQuestions(QuizQuestions[] quizQuestions) {
        this.quizQuestions = quizQuestions;
    }
}