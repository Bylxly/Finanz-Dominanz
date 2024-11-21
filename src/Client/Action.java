package Client;

import Server.State.GameState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Random;

public class Action {
    private int actionId;
    private String description;

    public enum ServerMessage {
        ASK_ROLL {
            @Override
            public void execute(Client client) {
                doRoll(client);
            }
        };

        public abstract void execute(Client client);

        public static void doRoll(Client client) {
            try {
                System.out.println("Press Enter to roll the dice...");
                new BufferedReader(new InputStreamReader(System.in)).readLine();

                Random random = new Random();
                int[] rolls = new int[5];
                System.out.print("Rolling: ");
                for (int i = 0; i < rolls.length; i++) {
                    rolls[i] = random.nextInt(6) + 1;
                    System.out.print(rolls[i] + (i < rolls.length - 1 ? ", " : ""));
                }
                System.out.println();

                // Notify the server
                PrintWriter writer = client.getWriter();
                if (writer != null) {
                    writer.println("ROLL");
                }

            } catch (IOException e) {
                System.out.println("Error during dice roll: " + e.getMessage());
            }
        }
    }

    public Action(int actionId, String description) {
        this.actionId = actionId;
        this.description = description;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean allowAction(GameState state) {
        return state != null;
    }

    @Override
    public String toString() {
        return "Action ID: " + actionId + ", Description: " + description;
    }
}
