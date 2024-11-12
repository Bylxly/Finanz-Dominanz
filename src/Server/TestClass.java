package Server;

public class TestClass {

    public static void main(String[] args) {
        Game game = new Game();

        game.makePlayer("Nikolas");
        game.makePlayer("Samuel");
        game.makePlayer("Tim");

        game.printBoard();

        for (int i = 0; i < 9; i++) {
            game.move();
            game.printBoard();
        }
    }
}
