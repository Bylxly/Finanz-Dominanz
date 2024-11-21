package Server;

import java.io.Serializable;

public class Message implements Serializable {
    private final MsgType messageType;
    private final String message;

    Message(MsgType messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public MsgType getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }
}
