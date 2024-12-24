package Server.State;

import Server.Field.Property.Property;
import Server.Field.Property.Street;
import Server.Game;
import Server.Message;
import Server.MsgType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildState implements GameState {

    private final Game game;

    public BuildState(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        int mapIndex = 1;
        Map<Integer, Street> buildableStreets = new HashMap<>();
        for (Property property : game.getActivePlayer().getProperties()) {
            if (property instanceof Street && ((Street) property).getHouses() < 5
                    && ((Street) property).getColorGroup().isComplete()) {
                buildableStreets.put(mapIndex++, (Street) property);
            }
        }

        game.getActivePlayer().sendObject(new Message(MsgType.SELECT_PROPERTY, null));
        if (buildableStreets.isEmpty()) {
            game.getActivePlayer().sendObject(new Message(MsgType.INFO, "You don't own any properties where you can build on"));
        }
        else {
            List<String> message = new ArrayList<>();
            message.add("You can build on following properties:");
            for (Integer index : buildableStreets.keySet()) {
                message.add(index + ": " + buildableStreets.get(index).getName() +
                        " Kosten: " + buildableStreets.get(index).getHousePrice());
            }
            game.getActivePlayer().sendObject(message);

            int index = Integer.parseInt(game.getActivePlayer().recieveMessage());
            Street streetToBuildOn = buildableStreets.get(index);
            if (game.getActivePlayer().getProperties().contains(streetToBuildOn)) {
                streetToBuildOn.buyHouses();
                game.getActivePlayer().sendObject(game);
            }
        }
    }
}
