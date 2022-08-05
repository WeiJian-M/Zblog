package com.example.zblog.domain;

public class Concern {

    private int meId;
    private int heId;
    private String userName;

    public int getMeId() {
        return meId;
    }

    public void setMeId(int meId) {
        this.meId = meId;
    }

    public int getHeId() {
        return heId;
    }

    public void setHeId(int heId) {
        this.heId = heId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Concern{" +
                "meId=" + meId +
                ", heId=" + heId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
