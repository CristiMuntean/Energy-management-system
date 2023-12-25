package ro.tuc.ds2020.dtos;

public class MessageDTO {
    private String text;
    private String sender;
    private String receiver;
    private String type;

    public MessageDTO() {
    }

    public MessageDTO(String text, String sender, String receiver, String type) {
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
