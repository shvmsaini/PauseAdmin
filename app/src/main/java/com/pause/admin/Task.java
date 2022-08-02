package com.pause.admin;

public class Task {
    public String key;
    public String detail, deadline, status, response, doneDate, taskType, typeDetail;

    public Task(String detail, String deadline, String status, String taskType, String typeDetail) {
        this.detail = detail;
        this.deadline = deadline;
        this.status = status;
        this.taskType = taskType;
        this.typeDetail = typeDetail;
    }

    public Task(String detail, String deadline, String status, String taskType,
                String typeDetail, String response, String doneDate) {
        this.detail = detail;
        this.deadline = deadline;
        this.status = status;
        this.taskType = taskType;
        this.typeDetail = typeDetail;
        this.response = response;
        this.doneDate = doneDate;

    }

    public String getKEY() {
        return key;
    }

    public void setKEY(String key) {
        this.key = key;
    }

    public String getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(String doneDate) {
        this.doneDate = doneDate;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTypeDetail() {
        return typeDetail;
    }

    public void setTypeDetail(String typeDetail) {
        this.typeDetail = typeDetail;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
