package main.model;


public class OutputMessage {

    private String author;
    private String textMessage;
    private String sendTime;

    public OutputMessage(String author, String textMessage, String sendTime) {
        this.author = author;
        this.textMessage = textMessage;
        this.sendTime = sendTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}