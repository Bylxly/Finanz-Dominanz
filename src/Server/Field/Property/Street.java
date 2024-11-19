package Server.Field.Property;

import Server.GameUtilities;
import Server.Player;

public class Street extends Property {

    private int houses;
    private final ColorGroup colorGroup;
    private int housePrice;

    public Street(String name, int price, int housePrice, int rent, int hypothek, ColorGroup colorGroup) {
        super(name, price, rent, hypothek);
        this.housePrice = housePrice;
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

    public void buyHouses(Player player){
        if(colorGroup.isComplete() && houses < 5){
            if(GameUtilities.checkIfEnoughMoney(player, housePrice)){
                GameUtilities.payBank(player, housePrice);
                houses++;
            }
        }
    }

    public void sellHouse(Player player){
        if (houses > 0){
            GameUtilities.receiveFromBank(player, (housePrice/2));
            houses--;
        }
    }

    public int getHouses(){
        return houses;
    }

    public ColorGroup getColorGroup() {
        return colorGroup;
    }

    @Override
    public String getName() {
        return colorGroup.getColorCode() + super.getName() + "\u001B[0m";
    }
}
