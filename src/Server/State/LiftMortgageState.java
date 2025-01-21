package Server.State;

import Server.Field.Property.Property;
import Server.Game;

import java.util.HashMap;
import java.util.Map;

/**
 * Zustand, der es einem Spieler ermöglicht, eine Hypothek auf einem Eigentum aufzuheben.
 */
public class LiftMortgageState extends SelectState{
    /**
     * Konstruktor für LiftMortgageState.
     * @param game Das aktuelle Spielobjekt.
     */
    public LiftMortgageState(Game game) {
        super(game);
    }

    @Override
    protected Map<Integer, Object> getEligibleObjects() {
        Map<Integer, Object> eligibleObjects = new HashMap<>();
        int mapIndex = 1;
        for (Property property : game.getActivePlayer().getProperties()) {
            if (property.hasHypothek()) {
                eligibleObjects.put(mapIndex++, property);
            }
        }
        return eligibleObjects;
    }

    @Override
    protected void performAction(Object object) {
        ((Property) object).redeemProperty();
    }

    @Override
    public String getNoSuitableObjectsMessage() {
        return "You don't have a mortgage on any of your properties";
    }

    @Override
    public String getSelectPropertyMessage() {
        return "Select the property you want to lift the mortgage from:";
    }

    @Override
    public String generateObjectInfoMessage(int index, Map<Integer, Object> objectMap) {
        return " Price to lift mortgage: " + Math.round(((Property) objectMap.get(index)).getHypothek() * 1.1);
    }

    @Override
    public String getChooseMessage() {
        return "Choose a property:";
    }
}
