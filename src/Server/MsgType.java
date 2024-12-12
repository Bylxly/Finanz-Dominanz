package Server;

import java.io.Serializable;

public enum MsgType implements Serializable {
    ASK_SERVER,
    ASK_ROLL,
    ASK_BUY,
    ASK_NEXT,
    ASK_KNAST,
    INFO,
    BUILD_SELECT_PROPERTY,
    DO_AUCTION,
    NEW_BID,
    END_AUCTION
}
