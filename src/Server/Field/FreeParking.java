package Server.Field;

import Server.Player;

public class FreeParking extends Field {
    public FreeParking(String name) {
        super(name);
    }

    @Override
    public boolean startAction(Player p) {
        return true;
    }
}
