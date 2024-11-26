package Server;

import java.io.Serializable;

public record Message(MsgType messageType, String message) implements Serializable {
    //FERTIG NICHTS MEHR HINZUFÜGEN
}
