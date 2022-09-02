package com.pause.admin.pojo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task implements Comparable<Task> {
    public String detail, deadline, response, doneDate, taskType, typeDetail;
    private String key, attendedDate, status;

    public Task(String detail, String deadline, String taskType, String typeDetail) {
        this.detail = detail;
        this.deadline = deadline;
        this.taskType = taskType;
        this.typeDetail = typeDetail;
    }

    public Task(String detail, String deadline, String taskType,
                String typeDetail, String response, String doneDate) {
        this.detail = detail;
        this.deadline = deadline;
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


    @Override
    public int compareTo(Task task) {
        Task t1 = this;
        boolean ddt1 = t1.getDoneDate() != null;
        boolean ddt2 = task.getDoneDate() != null;
        if (!ddt1 && !ddt2) {
            LocalDate d1 = LocalDate.parse(t1.getDeadline(), DateTimeFormatter.ofPattern("dd/MM/yy"));
            LocalDate d2 = LocalDate.parse(task.getDeadline(), DateTimeFormatter.ofPattern("dd/MM/yy"));
            return d1.compareTo(d2); // Nearest Deadline first
        }
        if (!ddt1) return 1;
        if (!ddt2) return -1;
        LocalDate d1 = LocalDate.parse(t1.getDoneDate(), DateTimeFormatter.ofPattern("dd/MM/yy"));
        LocalDate d2 = LocalDate.parse(task.getDoneDate(), DateTimeFormatter.ofPattern("dd/MM/yy"));
        return d2.compareTo(d1); // Latest done task first
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttendedDate() {
        return attendedDate;
    }

    public void setAttendedDate(String attendedDate) {
        this.attendedDate = attendedDate;
    }
}
