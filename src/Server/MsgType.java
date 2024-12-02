package Server;

import java.io.Serializable;

public enum MsgType implements Serializable {
    ASK_ROLL,
    ASK_BUY,
    ASK_NEXT,
    INFO,
    BUILD_SELECT_PROPERTY,
    DO_AUCTION,
    NEW_BID
}
