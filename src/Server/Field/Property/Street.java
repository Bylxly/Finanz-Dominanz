package Server.Field.Property;

import Server.GameUtilities;
import Server.Player;

/**
 * Repräsentiert eine Straße im Spiel.
 * Eine Straße kann Häuser und Hotels haben, und die Miete hängt von der Anzahl der Häuser ab.
 */
public class Street extends Property {

    private int houses; // Die Anzahl der Häuser auf der Straße.
    private final ColorGroup colorGroup; // Die Farbgruppe, zu der die Straße gehört.
    private int housePrice; // Der Preis für den Kauf eines Hauses.

    /**
     * Konstruktor für eine Straße.
     *
     * @param name       Der Name der Straße.
     * @param price      Der Kaufpreis der Straße.
     * @param housePrice Der Preis für den Kauf eines Hauses.
     * @param rent       Die Mietstufen der Straße.
     * @param hypothek   Der Hypothekenwert der Straße.
     * @param colorGroup Die Farbgruppe, zu der die Straße gehört.
     */
    public Street(String name, int price, int housePrice, int[] rent, int hypothek, ColorGroup colorGroup) {
        super(name, price, rent, hypothek);
        this.housePrice = housePrice;
        this.colorGroup = colorGroup;
        colorGroup.addStreet(this); // Fügt die Straße zur Farbgruppe hinzu.
    }

    @Override
    public boolean startAction(Player player) {
        if (!hasHypothek()) {
            if (GameUtilities.checkIfEnoughMoney(player, getRent(getHouses()))) {
                payRent(player);
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Berechnet die Miete, die ein Spieler zahlen muss.
     */
    @Override
    public void payRent(Player player) {
            if (getOwner() != null && getOwner() != player) {
                GameUtilities.transferMoney(player, getOwner(), getRent(getHouses()));
            }
    }

    /**
     * Belastet die Straße mit einer Hypothek, wenn keine Häuser darauf stehen.
     */
    @Override
    public void mortgageProperty() {
        if (getHouses() == 0) {
            super.mortgageProperty();
        }
    }

    /**
     * Kauft ein Haus auf der Straße, wenn die Farbgruppe vollständig ist und der Spieler genug Geld hat.
     */
    public void buyHouse() {
        if (colorGroup.isComplete() && houses < 5) {
            if (GameUtilities.checkIfEnoughMoney(getOwner(), housePrice)) {
                GameUtilities.payBank(getOwner(), housePrice);
                houses++;
            }
        }
    }

    /**
     * Verkauft ein Haus auf der Straße und gibt dem Spieler die Hälfte des Kaufpreises zurück.
     */
    public void sellHouse() {
        if (houses > 0) {
            GameUtilities.receiveFromBank(getOwner(), (housePrice / 2));
            houses--;
        }
    }

    public int getHouses(){
        return houses;
    }

    public ColorGroup getColorGroup() {
        return colorGroup;
    }

    /**
     * Gibt den Namen der Straße zurück, inklusive der Anzahl der Häuser.
     */
    @Override
    public String getName() {
        StringBuilder name;
        name = new StringBuilder(colorGroup.getColorCode() + super.getName() + "\u001B[0m" + " ");
        if (getHouses() == 5) {
            name.append("\uD83C\uDFE8");
        }
        else {
            name.append("\uD83C\uDFE0 ".repeat(getHouses()));
        }
        return name.toString();
    }

    public int getHousePrice() {
        return housePrice;
    }
}
