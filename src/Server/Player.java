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

public class Player implements Serializable {

    private int money;
    private final String name;
    private final Game game;
    private Field currentField;
    private List<Property> properties;
    private boolean arrested;
    private transient Socket client;
    private transient ObjectOutputStream objectOutputStream;
    private transient BufferedReader bufferedReader;

    public Player(int money, String name, Game game, Socket client, ObjectOutputStream objectOutputStream, BufferedReader bufferedReader) {
        this.money = money;
        this.name = name;
        this.game = game;
        properties = new ArrayList<>();
        this.client = client;
        this.objectOutputStream = objectOutputStream;
        this.bufferedReader = bufferedReader;
    }

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

    public void takeMoney(int amount) {
        while (amount > money) {
            showOptions(false);
        }
        money -= amount;
    }

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

    public void giveMoney(int amount) {
        money += amount;
    }

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
