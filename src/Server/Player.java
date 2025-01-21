package Server;


import Server.Field.Field;
import Server.Field.Property.Property;
import Server.State.GameState;
import Server.State.MortgageState;
import Server.State.TradeState;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert einen Spieler im Spiel.
 * Verwaltet das Geld, die Eigenschaften und die Kommunikation mit dem Client.
 */
public class Player implements Serializable {

    private int money; // Das Geld des Spielers.
    private final String name; // Der Name des Spielers.
    private final Game game; // Das Spiel, an dem der Spieler teilnimmt.
    private Field currentField; // Das aktuelle Feld, auf dem der Spieler steht.
    private List<Property> properties; // Die Grundstücke, die der Spieler besitzt.
    private boolean arrested; // Gibt an, ob der Spieler im Gefängnis ist.
    private transient Socket client; // Der Socket für die Client-Kommunikation.
    private transient ObjectOutputStream objectOutputStream; // Der Output-Stream für die Kommunikation.
    private transient BufferedReader bufferedReader; // Der Input-Stream für die Kommunikation.

    /**
     * Konstruktor für den Spieler.
     *
     * @param money              Das Startgeld des Spielers.
     * @param name               Der Name des Spielers.
     * @param game               Das Spiel, an dem der Spieler teilnimmt.
     * @param client             Der Socket für die Client-Kommunikation.
     * @param objectOutputStream Der Output-Stream für die Kommunikation.
     * @param bufferedReader     Der Input-Stream für die Kommunikation.
     */
    public Player(int money, String name, Game game, Socket client, ObjectOutputStream objectOutputStream, BufferedReader bufferedReader) {
        this.money = money;
        this.name = name;
        this.game = game;
        properties = new ArrayList<>();
        this.client = client;
        this.objectOutputStream = objectOutputStream;
        this.bufferedReader = bufferedReader;
    }

    /**
     * Sendet ein Objekt an den Client.
     */
    public void sendObject(Object object) {
        try {
            if (objectOutputStream != null) {
                objectOutputStream.reset(); // Puffer zurücksetzen
                objectOutputStream.writeObject(object);
                objectOutputStream.flush(); // Sicherstellen, dass die Nachricht gesendet wird
                if (object instanceof Message) {
                    System.out.println("Sent to " + name + " :" + object);
                }
                else if (object instanceof Game){
                    System.out.println("Game was sent to " + name);
                }
                else  {
                    System.out.println("Object was sent to " + name + " (" + object.getClass() + ")");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Empfängt eine Nachricht vom Client.
     */
    public String receiveMessage() {
        try {
            if (bufferedReader != null) {
                String message = bufferedReader.readLine();
                System.out.println(message);
                return message;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Nimmt Geld vom Spieler.
     *
     * @param amount Der Betrag, der vom Spieler genommen wird.
     */
    public void takeMoney(int amount) {
        while (amount > money) {
            showOptions(false);
        }
        money -= amount;
    }

    /**
     * Zeigt Optionen an, wenn der Spieler nicht genug Geld hat.
     *
     * @param includeQuit Gibt an, ob die Option "QUIT" angezeigt werden soll.
     * @return true, wenn der Spieler eine gültige Option gewählt hat, sonst false.
     */
    public boolean showOptions(boolean includeQuit) {
        GameState lastState = game.getCurrentGameState();

        String message = includeQuit
                ? """
                You can't afford to pay this
                What would you like to do?
                MORTGAGE, TRADE, BANKRUPT, QUIT"""
                : """
                You can't afford to pay this
                What would you like to do?
                MORTGAGE, TRADE, BANKRUPT""";

        sendObject(new Message(MsgType.ASK_NO_MONEY, message));
        String response = receiveMessage();

        switch (response) {
            case "MORTGAGE":
                game.setCurrentGameState(new MortgageState(game));
                break;
            case "TRADE":
                game.setCurrentGameState(new TradeState(game));
                break;
            case "BANKRUPT":
                if (currentField instanceof Property && ((Property) currentField).getOwner() != null) {
                    game.declareBankruptcy(((Property) currentField).getOwner());
                    return true;
                }
                game.declareBankruptcy();
                return true;
            case "QUIT":
                if (includeQuit) {
                    return true;
                }
                // Falls "QUIT" eingegeben wird, aber nicht erlaubt ist:
                sendObject(new Message(MsgType.INFO, "Invalid option."));
                return showOptions(false);
            default:
                sendObject(new Message(MsgType.INFO, "Invalid option."));
                return showOptions(includeQuit);
        }

        game.getCurrentGameState().execute();
        game.setCurrentGameState(lastState);
        return false;
    }

    /**
     * Gibt dem Spieler Geld.
     */
    public void giveMoney(int amount) {
        money += amount;
    }

    /**
     * Schließt die Verbindung zum Client.
     */
    public void closeConnection() {
        sendObject(new Message(MsgType.CLOSE_CONNECTION, null));
        try {
            objectOutputStream.close();
            bufferedReader.close();
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Field getCurrentField() {
        return currentField;
    }

    public void setCurrentField(Field currentField) {
        this.currentField = currentField;
    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void addProperty(Property property) {
        properties.add(property);
    }

    public void removeProperty(Property property) {
        properties.remove(property);
    }

    public Socket getClient() {
        return client;
    }

    public boolean isArrested() {
        return arrested;
    }

    public void setArrested(boolean arrested) {
        this.arrested = arrested;
    }
}
