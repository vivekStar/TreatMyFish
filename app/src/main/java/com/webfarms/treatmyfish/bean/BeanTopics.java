package com.webfarms.treatmyfish.bean;

import java.io.Serializable;

public class BeanTopics implements Serializable {

    String topicId;
    String topicName;

    public BeanTopics(String topicId, String topicName) {
        this.topicId = topicId;
        this.topicName = topicName;
    }


    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
