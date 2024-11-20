package Server.State;

import java.io.Serializable;

public interface GameState extends Serializable {

    public void execute();
}
