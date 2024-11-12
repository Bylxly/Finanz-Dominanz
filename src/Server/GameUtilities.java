package Server;

import java.util.ArrayList;

public class GameUtilities {

    public void transferMoney(Player fromPlayer, Player toPlayer, int amount){
        fromPlayer.takeMoney(amount);
        toPlayer.giveMoney(amount);
    }

    public void payBank(Player p, int amount){
        p.takeMoney(amount);
    }

    public void receiveFromBank(Player p, int amount){
        p.giveMoney(amount);
    }

    /*public void shuffleCards(ArrayList<Card> cards){

    }

    public void drawCard(ArrayList<Card> cards){

    }*/
}
