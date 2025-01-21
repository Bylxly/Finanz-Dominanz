package Server.State;

import Server.Field.Property.Property;
import Server.Game;
import Server.Message;
import Server.MsgType;
import Server.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstrakte Klasse, die die Logik zum Auswählen eines Objekts für eine Aktion definiert.
 * Konkrete Unterklassen implementieren spezifische Aktionen und Filterlogik.
 */
public abstract class SelectState implements GameState {

    protected final Game game;

    /**
     * Konstruktor für SelectState.
     * @param game Das aktuelle Spielobjekt.
     */
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

    /**
     * Ermöglicht dem Spieler, ein Objekt aus einer Liste von verfügbaren Objekten auszuwählen.
     * @return Das ausgewählte Objekt oder null, wenn kein gültiges Objekt ausgewählt wurde.
     */
    public Object selectObject() {
        Map<Integer, Object> objectMap = getEligibleObjects(); // Filterlogik wird von Unterklassen definiert.

        game.getActivePlayer().sendObject(new Message(MsgType.SELECT_OBJECT, null));
        if (objectMap.isEmpty()) {
            game.getActivePlayer().sendObject(new Message(MsgType.INFO, getNoSuitableObjectsMessage()));
            return null;
        } else {
            List<String> message = new ArrayList<>();
            message.add(getSelectPropertyMessage());

            Class<?> instanceOf = getInstanceOf(objectMap);

            if (instanceOf == Property.class) {
                for (Integer index : objectMap.keySet()) {
                    message.add(index + ": " + ((Property)objectMap.get(index)).getName() + generateObjectInfoMessage(index, objectMap));
                }
            }
            else if (instanceOf == Player.class) {
                for (Integer index : objectMap.keySet()) {
                    message.add(index + ": " + ((Player)objectMap.get(index)).getName());
                }
            }
            message.add("Quit: Quit this menu");
            message.add(getChooseMessage());
            game.getActivePlayer().sendObject(message);

            // Spieler wählt ein Objekt aus.
            int index = Integer.parseInt(game.getActivePlayer().receiveMessage());

            // If player selects quit
            if (index == -1) return null;

            return objectMap.get(index);
        }
    }


    /**
     * Bestimmt den gemeinsamen Typ der Objekte in der Map.
     * @param objectMap Map der verfügbaren Objekte.
     * @return Der gemeinsame Typ der Objekte oder null, wenn kein Typ bestimmt werden kann.
     */
    public Class<?> getInstanceOf(Map<Integer, Object> objectMap) {
        Class<?> instanceOf;
        if (Property.class.isAssignableFrom(objectMap.get(1).getClass())) {
            instanceOf = Property.class;
        }
        else if (Player.class.isAssignableFrom(objectMap.get(1).getClass())) {
            instanceOf = Player.class;
        }
        else {
            return null;
        }

        for (Object object : objectMap.values()) {
            if (!instanceOf.isAssignableFrom(object.getClass())) {
                instanceOf = null;
                break;
            }
        }
        return instanceOf;
    }

    public abstract String getNoSuitableObjectsMessage();

    public abstract String getSelectPropertyMessage();

    public abstract String generateObjectInfoMessage(int index, Map<Integer, Object> objectMap);

    public abstract String getChooseMessage();

}