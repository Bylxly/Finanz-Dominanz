package Server;

import java.io.Serializable;

enum MsgTypes implements Serializable {
    ASK_ROLL,
    ASK_BUY
}

public class Message implements Serializable {
    private final MsgTypes message;

    Message(MsgTypes message) {
        this.message = message;
    }

    public MsgTypes getMessage() {
        return message;
    }
}
