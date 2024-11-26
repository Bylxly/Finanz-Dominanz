package Server;

import java.io.Serializable;

public enum MsgType implements Serializable {
    ASK_ROLL,
    ASK_BUY,
    ASK_NEXT,
    INFO
}
