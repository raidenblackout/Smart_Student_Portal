package ssp.smartstudentportal.Structures;

import java.io.Serializable;

public class Message implements Serializable {
    private final String sender;
    private final String receiver;
    private final String message;
    private final String time;
    private final byte[] image;

    public Message(String sender, String receiver, String message, byte[] image, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.image = image;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public int compareTo(Message object) {
        return this.time.compareTo(object.getTime());
    }

    @Override
    public boolean equals(Object object) {
        return this.time.equals(((Message) object).getTime());
    }

    public byte[] getImage() {
        return image;
    }
}
