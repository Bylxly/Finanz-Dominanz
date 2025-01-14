package Server.Field.Property;

import Server.Field.Field;
import Server.GameUtilities;
import Server.Message;
import Server.MsgType;
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
        // 10 % interest rate
        GameUtilities.payBank(owner, (int) Math.round((getHypothek() * 1.1)));
        this.hasHypothek = false;
    }

    public void mortgageProperty() {
        GameUtilities.receiveFromBank(owner, hypothek);
        this.hasHypothek = true;
    }

    public void askMortgage(Player player) {
        //TODO: Player könnte 10 % nicht bezahlen können und muss selber eine Hypothek aufnehmen
        player.sendObject(new Message(MsgType.GET_ANSWER_KEEP_LIFT, getName() + "is mortgaged.\n"
                + "You can either keep the mortgage and pay 10 % of the mortgage value \n"
                + "or you can lift the mortgage and pay the mortgage value + 10 % interest fee. \n"
                + "Choose an option: KEEP or LIFT"));

        String selection = player.recieveMessage();
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
