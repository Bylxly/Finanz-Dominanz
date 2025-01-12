package Server;


import Server.Field.Field;
import Server.Field.Property.Property;
import Server.State.GameState;
import Server.State.HypothekState;
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

    public String recieveMessage() {
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
        GameState last_state = game.getCurrentGameState();
        while (amount > money) {
            sendObject(new Message(MsgType.ASK_NO_MONEY, """
                    You can't afford to pay this \
                    What would you like to do?\
                    MORTGAGE, TRADE, BANKRUPT"""));
            String response = recieveMessage();
            switch (response) {
                case "MORTGAGE":
                    game.setCurrentGameState(new HypothekState(game));
                    break;
                case "TRADE":
                    game.setCurrentGameState(new TradeState(game));
                    break;
                case "BANKRUPT":
                    if (currentField instanceof Property && ((Property) currentField).getOwner() != null) {
                        game.declareBankruptcy(((Property) currentField).getOwner());
                        return;
                    }
                    game.declareBankruptcy();
                    return;
            }
            game.getCurrentGameState().execute();
        }
        game.setCurrentGameState(last_state);
        money -= amount;
    }

    public void giveMoney(int amount) {
        money += amount;
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
