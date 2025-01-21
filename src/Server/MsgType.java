package Server;

import java.io.Serializable;

public enum MsgType implements Serializable {
    ASK_SERVER,
    ASK_ROLL,
    ASK_BUY,
    ASK_NEXT,
    ASK_KNAST,
    ASK_NO_MONEY,
    INFO,
    SELECT_OBJECT,
    DO_AUCTION,
    QUIT_AUCTION,
    NEW_BID,
    END_AUCTION,
    REQUEST_TRADE,
    SELECT_TRADE,
    GET_ANSWER, //TODO: Mby ändern => @TradeState
    GET_ANSWER_KEEP_LIFT,
    CLOSE_CONNECTION
}
