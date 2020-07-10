package com.webfarms.treatmyfish.bean;

public class BeanDescription {

    String type;
    String content;

    public BeanDescription(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "BeanDescription{ " +
                "type='" + type +
                ", content='" + content +
                '}';
    }
}
