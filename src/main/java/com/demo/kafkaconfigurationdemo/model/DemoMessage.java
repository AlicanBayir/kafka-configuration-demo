package com.demo.kafkaconfigurationdemo.model;

import java.util.Date;

public class DemoMessage {
    private String demoName;
    private Date demoDate;

    public String getDemoName() {
        return demoName;
    }

    public void setDemoName(String demoName) {
        this.demoName = demoName;
    }

    public Date getDemoDate() {
        return demoDate;
    }

    public void setDemoDate(Date demoDate) {
        this.demoDate = demoDate;
    }

    @Override
    public String toString() {
        return "DemoMessage{" +
                "demoName='" + demoName + '\'' +
                ", demoDate=" + demoDate +
                '}';
    }
}