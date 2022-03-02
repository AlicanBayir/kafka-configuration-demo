package com.demo.kafkaconfigurationdemo.util;

import java.util.Map;

public class Consumer {
    private String dataClass;
    private String topic;
    private String errorTopic;
    private Map<String, Object> props;
    private String factoryBeanName;
    private Integer concurrency;
    private Integer retryCount;
    private Long backoffIntervalMillis;
    private Integer timeoutMillis;
    private Integer syncCommitTimeoutSecond;
    private Boolean syncCommit;
    private Boolean missingTopicAlertEnable;
    private Boolean autoStartup;
    private String failoverHandlerBeanName;

    public String getDataClass() {
        return dataClass;
    }

    public void setDataClass(String dataClass) {
        this.dataClass = dataClass;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getErrorTopic() {
        return errorTopic;
    }

    public void setErrorTopic(String errorTopic) {
        this.errorTopic = errorTopic;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public Integer getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(Integer concurrency) {
        this.concurrency = concurrency;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Long getBackoffIntervalMillis() {
        return backoffIntervalMillis;
    }

    public void setBackoffIntervalMillis(Long backoffIntervalMillis) {
        this.backoffIntervalMillis = backoffIntervalMillis;
    }

    public Integer getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(Integer timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public Integer getSyncCommitTimeoutSecond() {
        return syncCommitTimeoutSecond;
    }

    public void setSyncCommitTimeoutSecond(Integer syncCommitTimeoutSecond) {
        this.syncCommitTimeoutSecond = syncCommitTimeoutSecond;
    }

    public Boolean getSyncCommit() {
        return syncCommit;
    }

    public void setSyncCommit(Boolean syncCommit) {
        this.syncCommit = syncCommit;
    }

    public Boolean getMissingTopicAlertEnable() {
        return missingTopicAlertEnable;
    }

    public void setMissingTopicAlertEnable(Boolean missingTopicAlertEnable) {
        this.missingTopicAlertEnable = missingTopicAlertEnable;
    }

    public Boolean getAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(Boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public String getFailoverHandlerBeanName() {
        return failoverHandlerBeanName;
    }

    public void setFailoverHandlerBeanName(String failoverHandlerBeanName) {
        this.failoverHandlerBeanName = failoverHandlerBeanName;
    }
}