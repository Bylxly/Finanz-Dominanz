package Server;


public class Player {

    private int money;
    private final String name;
    private Field currentField;

    public Player(int money, String name) {
        this.money = money;
        this.name = name;
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
}
