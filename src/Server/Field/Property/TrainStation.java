package Server.Field.Property;

import Server.GameUtilities;
import Server.Player;

public class TrainStation extends Property {

    public int anzahlDerBesitzendenBahnhöfeEinesSpielers = 0;

    public TrainStation(String name, int price, int[] rent, int hypothek) {
        super(name, price, rent, hypothek);
    }

    public int getAnzahlBahnhöfe(Player p) {
        for(Property property : p.getProperties()){
            if(property instanceof TrainStation){
                anzahlDerBesitzendenBahnhöfeEinesSpielers++;
            }
        }
        return anzahlDerBesitzendenBahnhöfeEinesSpielers;
    }

    @Override
    public void payRent(Player p) {
        if (getOwner() != null && getOwner() != p) {
            GameUtilities.transferMoney(p, getOwner(), getRent(getAnzahlBahnhöfe(getOwner())));
        }
    }

    @Override
    public boolean startAction(Player p) {
        if (GameUtilities.checkIfEnoughMoney(p, getRent(getAnzahlBahnhöfe(getOwner())))) {
            payRent(p);
            return true;
        }
        return false;
    }
}

