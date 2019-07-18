package com.clairvoyant.naijamenu.bean;

public class QuizPromotionResponse {

    private String status;
    private String message;
    private int contest_id;
    private String contest_name;
    private String contest_banner;

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

    public String getContest_name() {
        return contest_name;
    }

    public void setContest_name(String contest_name) {
        this.contest_name = contest_name;
    }

    public String getContest_banner() {
        return contest_banner;
    }

    public void setContest_banner(String contest_banner) {
        this.contest_banner = contest_banner;
    }
}