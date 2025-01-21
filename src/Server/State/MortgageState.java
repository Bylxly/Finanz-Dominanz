package Server.State;

import Server.Field.Property.Property;
import Server.Field.Property.Street;
import Server.Game;

import java.util.HashMap;
import java.util.Map;

/**
 * Zustand, der es einem Spieler ermöglicht, eine Hypothek auf einer Immobilie aufzunehmen.
 */
public class MortgageState extends SelectState {

    /**
     * Konstruktor für MortgageState.
     * @param game Das aktuelle Spielobjekt.
     */
    public MortgageState(Game game) {
        super(game);
    }

    @Override
    protected Map<Integer, Object> getEligibleObjects() {
        int mapIndex = 1;
        Map<Integer, Object> mortgageableProperties = new HashMap<>();
        for (Property property : game.getActivePlayer().getProperties()) {
            if (!property.hasHypothek() && (!(property instanceof Street) || ((Street) property).getHouses() == 0)) {
                mortgageableProperties.put(mapIndex++, property);
            }
        }
        return mortgageableProperties;
    }

    @Override
    protected void performAction(Object property) {
        ((Property)property).mortgageProperty();
    }

    @Override
    public String getNoSuitableObjectsMessage() {
        return "There are no properties where you can take a mortgage on";
    }

    @Override
    public String getSelectPropertyMessage() {
        return "Select a property to take a mortgage on:";
    }

    @Override
    public String generateObjectInfoMessage(int index, Map<Integer, Object> properties) {
        return "Mortgage value: " + ((Property)properties.get(index)).getHypothek();
    }

    @Override
    public String getChooseMessage() {
        return "Choose a property:";
    }

}