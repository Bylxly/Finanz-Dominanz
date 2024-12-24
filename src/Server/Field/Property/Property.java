package Server.Field.Property;

import Server.Field.Field;
import Server.GameUtilities;
import Server.Player;

public abstract class Property extends Field {

    private Player owner;
    private final int price;
    private final int[] rent;
    private final int hypothek;
    private boolean hasHypothek;

    public Property(String name, int price, int[] rent, int hypothek) {
        super(name);
        owner = null;
        this.price = price;
        this.rent = rent;
        this.hypothek = hypothek;
        this.hasHypothek = false;
    }

    public void buy(Player player) {
        if (owner == null) {
            GameUtilities.payBank(player, price);
            owner = player;
            player.addProperty(this);
        }
    }

    // Buy for auctions
    public void buy(Player player, int amount) {
        if (owner == null) {
            GameUtilities.payBank(player, amount);
            owner = player;
            player.addProperty(this);
        }
    }

    public abstract void payRent(Player player);

    public void redeemProperty() {

    }

    public void mortgageProperty() {
        GameUtilities.receiveFromBank(owner, hypothek);
        this.hasHypothek = true;
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
