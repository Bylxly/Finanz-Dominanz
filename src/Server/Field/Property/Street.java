package Server.Field.Property;

import Server.GameUtilities;
import Server.Player;

public class Street extends Property {

    private int houses;
    private final ColorGroup colorGroup;
    private int housePrice;

    public Street(String name, int price, int housePrice, int[] rent, int hypothek, ColorGroup colorGroup) {
        super(name, price, rent, hypothek);
        this.housePrice = housePrice;
        this.colorGroup = colorGroup;
        colorGroup.addStreet(this);
    }

    @Override
    public boolean startAction(Player player) {
        if (GameUtilities.checkIfEnoughMoney(player, getRent(getHouses()))) {
            payRent(player);
            return true;
        }
        return false;
    }

    @Override
    public void payRent(Player player) {
        if (getOwner() != null && getOwner() != player) {
            GameUtilities.transferMoney(player, getOwner(), getRent(getHouses()));
        }
    }

    public void buyHouses(){
        if(colorGroup.isComplete() && houses < 5){
            if(GameUtilities.checkIfEnoughMoney(getOwner(), housePrice)){
                GameUtilities.payBank(getOwner(), housePrice);
                houses++;
            }
        }
    }

    public void sellHouse(){
        if (houses > 0){
            GameUtilities.receiveFromBank(getOwner(), (housePrice/2));
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
        StringBuilder name;
        name = new StringBuilder(colorGroup.getColorCode() + super.getName() + "\u001B[0m" + " ");
        if (getHouses() == 5) {
            name.append("\uD83C\uDFE8");
        }
        else {
            name.append("\uD83C\uDFE0 ".repeat(Math.max(0, getHouses())));
        }
        return name.toString();
    }

}
