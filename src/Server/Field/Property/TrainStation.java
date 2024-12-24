package Server.Field.Property;

import Server.GameUtilities;
import Server.Player;

public class TrainStation extends Property {

    public TrainStation(String name, int price, int[] rent, int hypothek) {
        super(name, price, rent, hypothek);
    }

    public int getAnzahlBahnhöfe() {
        int anzahlDerBesitzendenBahnhöfeEinesSpielers = 0;
        for(Property property : getOwner().getProperties()){
            if(property instanceof TrainStation){
                anzahlDerBesitzendenBahnhöfeEinesSpielers++;
            }
        }
        return anzahlDerBesitzendenBahnhöfeEinesSpielers;
    }

    @Override
    public void payRent(Player p) {
        if (getOwner() != null && getOwner() != p) {
            GameUtilities.transferMoney(p, getOwner(), getRent(getAnzahlBahnhöfe()));
        }
    }

    @Override
    public boolean startAction(Player p) {
        if (GameUtilities.checkIfEnoughMoney(p, getRent(getAnzahlBahnhöfe()))) {
            payRent(p);
            return true;
        }
        return false;
    }
}

