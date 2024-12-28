package Server.State;

import Server.Field.Property.Property;
import Server.Field.Property.Street;
import Server.Game;

import java.util.HashMap;
import java.util.Map;

public class BuildState extends SelectState {

    public BuildState(Game game) {
        super(game);
    }

    @Override
    protected Map<Integer, Object> getEligibleObjects() {
        int mapIndex = 1;
        Map<Integer, Object> buildableStreets = new HashMap<>();
        for (Property property : game.getActivePlayer().getProperties()) {
            if (property instanceof Street && ((Street) property).getHouses() < 5
                    && ((Street) property).getColorGroup().isComplete()) {
                buildableStreets.put(mapIndex++, property);
            }
        }
        return buildableStreets;
    }

    @Override
    protected void performAction(Object property) {
        ((Street) property).buyHouse();
    }

    @Override
    public String getNoSuitableObjectsMessage() {
        return "You don't own any property which you can build a house on.";
    }

    @Override
    public String getSelectPropertyMessage() {
        return "Select a property to build a house on:";
    }

    @Override
    public String generateObjectInfoMessage(int index, Map<Integer, Object> properties) {
        return "House cost: " + ((Street) properties.get(index)).getHousePrice();
    }

    @Override
    public String getChooseMessage() {
        return "Choose a property:";
    }
}

