package Server;

import java.io.Serializable;

public enum MsgType implements Serializable {
    ASK_ROLL,
    DO_ROLL,
    ASK_BUY,
    DO_BUY,
}
