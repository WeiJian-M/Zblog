package com.example.zblog.domain;

public class BlogWithUserName {

    private int blogId;
    private int userId;
    private String userName;
    private String blogContent;
    private String blogTime;

    public int getBlogId() {
        return blogId;
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBlogContent() {
        return blogContent;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    public String getBlogTime() {
        return blogTime;
    }

    public void setBlogTime(String blogTime) {
        this.blogTime = blogTime;
    }

    @Override
    public String toString() {
        return "BlogInMainWithUserName{" +
                "blogId=" + blogId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", blogContent='" + blogContent + '\'' +
                ", blogTime='" + blogTime + '\'' +
                '}';
    }
}
