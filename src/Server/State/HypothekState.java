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

public class HypothekState implements GameState{

    private Game game;

    public HypothekState(Game game){
        this.game = game;
    }

    @Override
    public void execute() {
        int mapIndex = 1;
        Map<Integer, Property> mortageableProperties = new HashMap<>();
        for (Property property : game.getActivePlayer().getProperties()) {
            if (property instanceof Street && ((Street) property).getHouses() != 0) {
                break;
            }
            else {
                mortageableProperties.put(mapIndex++, property);
            }
        }

        game.getActivePlayer().sendObject(new Message(MsgType.SELECT_PROPERTY, null));
        if (mortageableProperties.isEmpty()) {
            game.getActivePlayer().sendObject(new Message(MsgType.INFO, "You don't own any properties which you can mortgage"));
        }
        else {
            List<String> message = new ArrayList<>();
            message.add("You can mortgage on following properties:");
            for (Integer index : mortageableProperties.keySet()) {
                message.add(index + ": " + mortageableProperties.get(index).getName() +
                        " Hypothek: " + mortageableProperties.get(index).getHypothek());
            }
            game.getActivePlayer().sendObject(message);

            int index = Integer.parseInt(game.getActivePlayer().recieveMessage());
            Property propertyToMortage = mortageableProperties.get(index);
            if (game.getActivePlayer().getProperties().contains(propertyToMortage)) {
                propertyToMortage.mortgageProperty();
                game.getActivePlayer().sendObject(game);
            }
        }
    }
}
