package Server.State;

import Server.Field.Property.Property;
import Server.Game;
import Server.Message;
import Server.MsgType;
import Server.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SelectState implements GameState {

    protected final Game game;

    public SelectState(Game game) {
        this.game = game;
    }

    /**
     * Abstrakte Methode, die von Unterklassen implementiert wird.
     * Definiert, welche Objekte für die Aktion infrage kommen.
     */
    protected abstract Map<Integer, Object> getEligibleObjects();

    /**
     * Abstrakte Methode, die die spezifische Aktion auf dem gewählten Objekt ausführt.
     */
    protected abstract void performAction(Object object);

    @Override
    public void execute() {
        Object object = selectObject();
        if (object != null) {
            performAction(object);
        }
    }

    public Object selectObject() {
        Map<Integer, Object> objectMap = getEligibleObjects(); // Filterlogik wird von Unterklassen definiert.

        game.getActivePlayer().sendObject(new Message(MsgType.SELECT_OBJECT, null));
        if (objectMap.isEmpty()) {
            game.getActivePlayer().sendObject(new Message(MsgType.INFO, getNoSuitableObjectsMessage()));
            return null;
        } else {
            List<String> message = new ArrayList<>();
            message.add(getSelectPropertyMessage());

            Class<?> instaceOf = getInstanceOf(objectMap);

            if (Property.class.isAssignableFrom(instaceOf)) {
                for (Integer index : objectMap.keySet()) {
                    message.add(index + ": " + ((Property)objectMap.get(index)).getName() + generateObjectInfoMessage(index, objectMap));
                }
            }
            else if (Player.class.isAssignableFrom(instaceOf)) {
                for (Integer index : objectMap.keySet()) {
                    message.add(index + ": " + ((Player)objectMap.get(index)).getName());
                }
            }
            message.add("Quit: Quit this menu");
            message.add(getChooseMessage());
            game.getActivePlayer().sendObject(message);

            // Spieler wählt ein Objekt aus.
            int index = Integer.parseInt(game.getActivePlayer().recieveMessage());

            // If player selects quit
            if (index == -1) return null;

            return objectMap.get(index);
        }
    }

    public Class<?> getInstanceOf(Map<Integer, Object> objectMap) {
        Class<?> instaceOf = objectMap.get(1).getClass();
        for (Object object : objectMap.values()) {
            if (!object.getClass().equals(instaceOf)) {
                instaceOf = null;
                break;
            }
        }
        return instaceOf;
    }

    public abstract String getNoSuitableObjectsMessage();

    public abstract String getSelectPropertyMessage();

    public abstract String generateObjectInfoMessage(int index, Map<Integer, Object> objectMap);

    public abstract String getChooseMessage();

}