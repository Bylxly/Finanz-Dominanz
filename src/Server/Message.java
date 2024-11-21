package Server;

import java.io.Serializable;

public class Message implements Serializable {
    private final MsgType message;

    Message(MsgType message) {
        this.message = message;
    }

    public MsgType getMessage() {
        return message;
    }
}
