package kr.ac.ewha.java2.domain.pojo;

public class ChatMessage {
    private String text;
    private String sender;

    // 기본 생성자
    public ChatMessage() {}

    // 생성자
    public ChatMessage(String text, String sender) {
        this.text = text;
        this.sender = sender;
    }

    // Getter & Setter
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
}
