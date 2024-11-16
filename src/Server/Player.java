package Server;


import Server.Field.Field;
import Server.Field.Property.Property;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private int money;
    private final String name;
    private Field currentField;
    private List<Property> properties;

    public Player(int money, String name) {
        this.money = money;
        this.name = name;
        properties = new ArrayList<>();
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
}
