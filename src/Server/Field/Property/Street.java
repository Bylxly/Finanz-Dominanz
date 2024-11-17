package Server.Field.Property;

import Server.GameUtilities;
import Server.Player;

public class Street extends Property {

    private int houses;
    private boolean hasHotel = false;
    private ColorGroup colorGroup;

    public Street(String name, int price, int rent, int hypothek, ColorGroup colorGroup) {
        super(name, price, rent, hypothek);
        this.colorGroup = colorGroup;
    }

    @Override
    public boolean startAction(Player player) {
        if (GameUtilities.checkIfEnoughMoney(player, getRent())) {
            super.payRent(player);
            return true;
        }
        return false;
    }

    public void buildHouses(){
        houses++;
    }

    public void buildHotel(){
        hasHotel = true;
    }

    public void removeHouse(){
        if (houses > 0){
            houses--;
        }
    }

    public void removeHotel(){
        hasHotel = false;
    }

    public int getHouses(){
        return houses;
    }

    public boolean getHasHotel(){
        return hasHotel;
    }

    public ColorGroup getColorGroup() {
        return colorGroup;
    }
}
