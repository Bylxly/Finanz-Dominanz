package Server;

import java.io.Serializable;
import java.util.Random;

public class Roll implements Serializable {

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
        total = number1 + number2;
    }

    public boolean getPasch() {
        if (number1 == number2){
            return true;
        } else {
            return false;
        }
    }

    public int getTotal() {
        return total;
    }

    public int getNumber1() {
        return number1;
    }

    public int getNumber2() {
        return number2;
    }
}
