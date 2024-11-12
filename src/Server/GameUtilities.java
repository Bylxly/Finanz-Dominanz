package Server;

import java.util.ArrayList;

public class GameUtilities {

    public static void transferMoney(Player fromPlayer, Player toPlayer, int amount){
        fromPlayer.takeMoney(amount);
        toPlayer.giveMoney(amount);
    }

    public static void payBank(Player p, int amount){
        p.takeMoney(amount);
    }

    public static void receiveFromBank(Player p, int amount){
        p.giveMoney(amount);
    }

    /*public void shuffleCards(ArrayList<Card> cards){

    }

    public void drawCard(ArrayList<Card> cards){

    }*/

    public static boolean checkIfEnoughMoney(Player p, int amount){
        return p.getMoney() >= amount;
    }
}
