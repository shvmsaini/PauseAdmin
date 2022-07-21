package com.pause.admin;

public class Task {
    String detail;
    String deadline;
    String approve;
    String comment;

    public Task(String detail, String deadline, String approve) {
        this.detail = detail;
        this.deadline = deadline;
        this.approve = approve;
    }

    public Task(String detail, String deadline, String approve, String comment) {
        this.detail = detail;
        this.deadline = deadline;
        this.approve = approve;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }
}
