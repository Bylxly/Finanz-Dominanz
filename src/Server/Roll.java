package Server;

import java.util.Random;

public class Roll {

    private int number1;
    private int number2;
    private int total;
    private Random rand = new Random();

    public Roll() {

    }

    public void generate() {
        number1 = rand.nextInt(6) + 1;
        number2 = rand.nextInt(6) + 1;
        calcTotal();
    }

    public void calcTotal(){
        total = number1 ;
    }

    public int getTotal() {
        return total;
    }
}
