package Server.State;

import Server.Field.Property.Property;
import Server.Field.Property.Street;
import Server.Game;

import java.util.HashMap;
import java.util.Map;

public class HypothekState extends SelectState {

    public HypothekState(Game game) {
        super(game);
    }

    @Override
    protected Map<Integer, Property> getEligibleProperties() {
        int mapIndex = 1;
        Map<Integer, Property> mortageableProperties = new HashMap<>();
        for (Property property : game.getActivePlayer().getProperties()) {
            if (!(property instanceof Street) || ((Street) property).getHouses() == 0) {
                mortageableProperties.put(mapIndex++, property);
            }
        }
        return mortageableProperties;
    }

    @Override
    protected void performAction(Property property) {
        property.mortgageProperty();
    }

    @Override
    public String getNoSuitablePropertiesMessage() {
        return "There are no properties where you can take a mortgage on";
    }

    @Override
    public String getSelectPropertyMessage() {
        return "Select a property to take a mortgage on:";
    }

    @Override
    public String generatePropertyInfoMessage(int index, Map<Integer, ? extends Property> properties) {
        return "Mortgage value: " + properties.get(index).getHypothek();
    }

}