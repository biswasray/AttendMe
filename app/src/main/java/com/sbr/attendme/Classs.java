package com.sbr.attendme;

public class Classs {
    private int id;
    private String subject;
    private String branch;
    private String stream;
    private int session;

    public Classs(int id, String subject, String branch, String stream, int session) {
        this.id = id;
        this.subject = subject;
        this.branch = branch;
        this.stream = stream;
        this.session = session;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }
}
