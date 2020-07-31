package com.example.hometrainng.entity;

import java.io.Serializable;

public class ConfirmBooKBean implements Serializable {
    private int firstCandidate;
    private String firstCandidateDatetime;
    private int secondCandidate;
    private String secondCandidateDatetime;
    private int thirdCandidate;
    private String thirdCandidateDatetime;
    private int userId;

    @Override
    public String toString() {
        return "ConfirmBooKBean{" +
                "firstCandidate=" + firstCandidate +
                ", firstCandidateDatetime='" + firstCandidateDatetime + '\'' +
                ", secondCandidate=" + secondCandidate +
                ", secondCandidateDatetime='" + secondCandidateDatetime + '\'' +
                ", thirdCandidate=" + thirdCandidate +
                ", thirdCandidateDatetime='" + thirdCandidateDatetime + '\'' +
                ", userId=" + userId +
                '}';
    }

    public void setFirstCandidate(int firstCandidate) {
        this.firstCandidate = firstCandidate;
    }

    public void setSecondCandidate(int secondCandidate) {
        this.secondCandidate = secondCandidate;
    }

    public void setThirdCandidate(int thirdCandidate) {
        this.thirdCandidate = thirdCandidate;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFirstCandidate() {
        return firstCandidate;
    }

    public int getSecondCandidate() {
        return secondCandidate;
    }

    public int getThirdCandidate() {
        return thirdCandidate;
    }

    public int getUserId() {
        return userId;
    }

    public String getFirstCandidateDatetime() {
        return firstCandidateDatetime;
    }

    public void setFirstCandidateDatetime(String firstCandidateDatetime) {
        this.firstCandidateDatetime = firstCandidateDatetime;
    }

    public String getSecondCandidateDatetime() {
        return secondCandidateDatetime;
    }

    public void setSecondCandidateDatetime(String secondCandidateDatetime) {
        this.secondCandidateDatetime = secondCandidateDatetime;
    }

    public String getThirdCandidateDatetime() {
        return thirdCandidateDatetime;
    }

    public void setThirdCandidateDatetime(String thirdCandidateDatetime) {
        this.thirdCandidateDatetime = thirdCandidateDatetime;
    }
}
