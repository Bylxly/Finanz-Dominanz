package Server;


import Server.Field.Field;
import Server.Field.Property.Property;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {

    private int money;
    private final String name;
    private Field currentField;
    private List<Property> properties;
    private transient Socket client;
    private transient ObjectOutputStream objectOutputStream;

    public Player(int money, String name, Socket client) {
        this.money = money;
        this.name = name;
        properties = new ArrayList<>();
        this.client = client;
        try {
            this.objectOutputStream = new ObjectOutputStream(client.getOutputStream()); // Stream initialisieren
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Object message) {
        try {
            if (objectOutputStream != null) {
                objectOutputStream.reset(); // Puffer zurücksetzen
                objectOutputStream.writeObject(message);
                objectOutputStream.flush(); // Sicherstellen, dass die Nachricht gesendet wird
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takeMoney(int amount) {
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
}
