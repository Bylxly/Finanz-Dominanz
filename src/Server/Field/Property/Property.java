package Server.Field.Property;

import Server.Field.Field;
import Server.GameUtilities;
import Server.Message;
import Server.MsgType;
import Server.Player;

/**
 * Abstrakte Klasse, die ein Grundstück im Spiel repräsentiert.
 * Enthält gemeinsame Funktionalitäten für Straßen, Bahnhöfe und Versorgungswerke.
 */
public abstract class Property extends Field {

    private Player owner; // Der Besitzer des Grundstücks.
    private final int price; // Der Kaufpreis des Grundstücks.
    private final int[] rent; // Die Miete, die basierend auf der Entwicklung des Grundstücks gezahlt wird.
    private final int hypothek; // Der Hypothekenwert des Grundstücks.
    private boolean hasHypothek; // Gibt an, ob das Grundstück hypothekarisch belastet ist.

    /**
     * Konstruktor für ein Grundstück.
     *
     * @param name     Der Name des Grundstücks.
     * @param price    Der Kaufpreis des Grundstücks.
     * @param rent     Die Mietstufen des Grundstücks.
     * @param hypothek Der Hypothekenwert des Grundstücks.
     */
    public Property(String name, int price, int[] rent, int hypothek) {
        super(name);
        owner = null;
        this.price = price;
        this.rent = rent;
        this.hypothek = hypothek;
        this.hasHypothek = false;
    }

    /**
     * Kauft das Grundstück für einen Spieler.
     */
    public void buy(Player player) {
        if (owner == null) {
            GameUtilities.payBank(player, price);
            owner = player;
            player.addProperty(this);
        }
    }

    /**
     * Kauft das Grundstück für einen Spieler während einer Auktion.
     *
     * @param player Der Spieler, der das Grundstück kauft.
     * @param amount Der Betrag, den der Spieler bietet.
     */
    public void buy(Player player, int amount) {
        if (owner == null) {
            GameUtilities.payBank(player, amount);
            owner = player;
            player.addProperty(this);
        }
    }

    /**
     * Abstrakte Methode zur Berechnung der Miete.
     */
    public abstract void payRent(Player player);

    /**
     * Löst die Hypothek auf dem Grundstück auf.
     */
    public void redeemProperty() {
        // 10 % interest rate
        GameUtilities.payBank(owner, (int) Math.round((getHypothek() * 1.1)));
        this.hasHypothek = false;
    }

    /**
     * Belastet das Grundstück mit einer Hypothek.
     */
    public void mortgageProperty() {
        GameUtilities.receiveFromBank(owner, hypothek);
        this.hasHypothek = true;
    }

    /**
     * Fragt den Spieler, ob er die Hypothek behalten oder lösen möchte.
     */
    public void askMortgage(Player player) {
        player.sendObject(new Message(MsgType.GET_ANSWER_KEEP_LIFT, getName() + "is mortgaged.\n"
                + "You can either keep the mortgage and pay 10 % of the mortgage value \n"
                + "or you can lift the mortgage and pay the mortgage value + 10 % interest fee. \n"
                + "Choose an option: KEEP or LIFT"));

        String selection = player.receiveMessage();
        if (selection.equals("KEEP")) {
            GameUtilities.payBank(player, (int) Math.round(getHypothek() * 0.1));
        }
        else if (selection.equals("LIFT")) {
            redeemProperty();
        }
    }

    @Override
    public String getName() {
        String name = super.getName();
        if (hasHypothek()) {
            name += " ❌";
        }
        return name;
    }

    public int getPrice() {
        return price;
    }

    public boolean isOwned() {
        return owner != null;
    }

    public int getRent(int index) {
        return rent[index];
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean hasHypothek() {
        return hasHypothek;
    }

    public int getHypothek() {
        return hypothek;
    }
}
