package Client;

import Server.Field.Field;
import Server.Field.Property.Property;
import Server.Game;
import Server.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TestClient {

    private static String host = "localhost";
    private static int port = 18718;
    private static Game game;

    public static void main(String[] args) {
        try {
            Socket client = new Socket(host, port);

            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());

            while (!client.isClosed()) {
                game = (Game) in.readObject();
                printBoard();
            }


            client.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printBoard() {
        System.out.println("Status:");
        System.out.println("Spieleranzahl: " + game.getPlayers().size());
        System.out.println("Felderanzahl: " + game.getBoard().length);
        System.out.println("nächster Spieler: " + game.getActivePlayer().getName());
        System.out.println("Augenanzahl vom letzten Wurf: " +
                game.getRoll().getNumber1() + "+" + game.getRoll().getNumber2() + "=" + game.getRoll().getTotal());

        System.out.println();
        System.out.println();

        for (Player player : game.getPlayers()) {
            System.out.println("Status von Spieler " + player.getName());
            System.out.println("Geld: " + player.getMoney());
            System.out.print("Felder im Besitz: ");
            if (player.getProperties().isEmpty()) {
                System.out.println("keine");
            }
            else {
                for (Property property : player.getProperties()) {
                    if (player.getProperties().indexOf(property) == 0) {
                        System.out.print(property.getName());
                    }
                    else {
                        System.out.print(", " + property.getName());
                    }
                }
                System.out.println();
            }
            System.out.println("Aktuelles Feld: " + player.getCurrentField().getName());
            System.out.println();
        }

        System.out.println();
        System.out.println();

        System.out.println("Status vom Spielbrett");
        for (Field f : game.getBoard()) {
            int playerAmountOnField = 0;
            for (Player p : game.getPlayers()) {
                if (f == p.getCurrentField() && playerAmountOnField == 0) {
                    System.out.print(f.getName() + " <-- " + p.getName());
                    playerAmountOnField++;
                }
                else if (f == p.getCurrentField()) {
                    System.out.print(", " + p.getName());
                    playerAmountOnField++;
                }
            }
            if (playerAmountOnField == 0) {
                System.out.print(f.getName());
            }
            System.out.println();
        }

        for (int i = 0; i < 5; i++) {
            System.out.println();
        }
    }
}
