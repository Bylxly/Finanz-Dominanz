package Server;

public class TestClass {

    public static void main(String[] args) {
        Game game = new Game();

        game.makePlayer("Nikolas");
        game.makePlayer("Samuel");
        game.makePlayer("Tim");

        game.printBoard();

        game.move();
        game.printBoard();

        game.move();
        game.printBoard();

        game.move();
        game.printBoard();
    }
}
