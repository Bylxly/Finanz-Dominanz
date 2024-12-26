package Server.State;

import Server.Field.Property.Property;
import Server.Game;
import Server.Message;
import Server.MsgType;

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
     * Definiert, welche Grundstücke für die Aktion infrage kommen.
     */
    protected abstract Map<Integer, ? extends Property> getEligibleProperties();

    /**
     * Abstrakte Methode, die die spezifische Aktion auf der gewählten Eigenschaft ausführt.
     */
    protected abstract void performAction(Property property);

    @Override
    public void execute() {
        Map<Integer, ? extends Property> properties = getEligibleProperties(); // Filterlogik wird von Unterklassen definiert.

        game.getActivePlayer().sendObject(new Message(MsgType.SELECT_PROPERTY, null));
        if (properties.isEmpty()) {
            game.getActivePlayer().sendObject(new Message(MsgType.INFO, getNoSuitablePropertiesMessage()));
        } else {
            List<String> message = new ArrayList<>();
            message.add(getSelectPropertyMessage());
            for (Integer index : properties.keySet()) {
                message.add(index + ": " + properties.get(index).getName() + generatePropertyInfoMessage(index, properties));
            }
            message.add("Quit: Quit this menu");
            game.getActivePlayer().sendObject(message);

            // Spieler wählt eine Grundstück aus.
            int index = Integer.parseInt(game.getActivePlayer().recieveMessage());

            // If player selects quit
            if (index == -1) return;

            Property selectedProperty = properties.get(index);
            if (selectedProperty != null && game.getActivePlayer().getProperties().contains(selectedProperty)) {
                performAction(selectedProperty); // Aktion auf dem gewählten Grundstück durchführen.
                game.getActivePlayer().sendObject(game); // Aktualisiertes Spielobjekt senden.
            }
        }
    }

    public abstract String getNoSuitablePropertiesMessage();

    public abstract String getSelectPropertyMessage();

    public abstract String generatePropertyInfoMessage(int index, Map<Integer, ? extends Property> properties);

}