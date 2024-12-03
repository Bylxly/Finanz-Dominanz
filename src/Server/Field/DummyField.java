package Server.Field;

import Server.Player;

public class DummyField extends Field {
    public DummyField(String name) {
        super(name);
    }

    @Override
    public boolean startAction(Player p) {
        return true;
    }
}
