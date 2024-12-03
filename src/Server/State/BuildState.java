package Server.State;

import Server.Field.Property.Property;
import Server.Field.Property.Street;
import Server.Game;
import Server.Message;
import Server.MsgType;

import java.util.ArrayList;
import java.util.List;

public class BuildState implements GameState {

    private final Game game;

    public BuildState(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        List<Street> streets = new ArrayList<>();
        for (Property property : game.getActivePlayer().getProperties()) {
            if (property instanceof Street && ((Street) property).getHouses() < 5
                    && ((Street) property).getColorGroup().isComplete()) {
                streets.add((Street) property);
            }
        }

        if (streets.isEmpty()) {
            game.getActivePlayer().sendObject(new Message(MsgType.INFO, "Du besitzt keine Grundstücke!"));
        }
        else {
            game.getActivePlayer().sendObject(new Message(MsgType.BUILD_SELECT_PROPERTY, null));
            int index = Integer.parseInt(game.getActivePlayer().recieveMessage());
            if (game.getActivePlayer().getProperties().get(index) instanceof Street) {
                ((Street) game.getActivePlayer().getProperties().get(index)).buyHouses();
                game.getActivePlayer().sendObject(game);
            }
        }
    }
}
