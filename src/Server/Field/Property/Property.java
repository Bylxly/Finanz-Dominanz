package Server.Field.Property;

import Server.Field.Field;
import Server.GameUtilities;
import Server.Player;

public abstract class Property extends Field {

    private Player owner;
    private final int price;
    private final int rent;
    private final int hypothek;
    private boolean hasHypothek;

    public Property(String name, int price, int rent, int hypothek) {
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

    public void payRent(Player player) {
        if (owner != null && owner != player) {
            GameUtilities.transferMoney(player, owner, rent);
        }
    }

    public void calculateRent() {

    }

    public void redeemProperty() {

    }

    public void mortageProperty() {

    }

    public int getPrice() {
        return price;
    }

    public boolean isOwned() {
        return owner != null;
    }
}
