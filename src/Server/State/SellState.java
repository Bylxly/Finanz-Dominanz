package Server.State;

import Server.Field.Property.Property;
import Server.Field.Property.Street;
import Server.Game;

import java.util.HashMap;
import java.util.Map;

public class SellState extends SelectState{

    public SellState(Game game) {
        super(game);
    }

    @Override
    protected Map<Integer, Object> getEligibleObjects() {
        int mapIndex = 1;
        Map<Integer, Object> sellableHouses = new HashMap<>();
        for (Property property : game.getActivePlayer().getProperties()) {
            if (property instanceof Street && ((Street) property).getHouses() != 0) {
                sellableHouses.put(mapIndex++, property);
            }
        }
        return sellableHouses;
    }

    @Override
    protected void performAction(Object Street) {
        ((Street)Street).sellHouse();
    }

    @Override
    public String getNoSuitableObjectsMessage() {
        return "There are no properties where you can sell houses.";
    }

    @Override
    public String getSelectPropertyMessage() {
        return "Select a property to sell houses:";
    }

    @Override
    public String generateObjectInfoMessage(int index, Map<Integer, Object> Street) {
        return "Sell value: " + ((Property)Street.get(index)).getPrice();
    }

    @Override
    public String getChooseMessage() {
        return "Choose a property:";
    }
}
