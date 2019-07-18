package com.clairvoyant.naijamenu.bean;

public class QuizResponseBean {

    private String status;
    private String message;
    private int contest_id;
    private QuizLevels[] levels;
    private PrizeList[] prizeList;

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

    public int getContest_id() {
        return contest_id;
    }

    public void setContest_id(int contest_id) {
        this.contest_id = contest_id;
    }

    public QuizLevels[] getLevels() {
        return levels;
    }

    public void setLevels(QuizLevels[] levels) {
        this.levels = levels;
    }

    public PrizeList[] getPrizeList() {
        return prizeList;
    }

    public void setPrizeList(PrizeList[] prizeList) {
        this.prizeList = prizeList;
    }
}