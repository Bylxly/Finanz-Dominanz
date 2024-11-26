package Client;

import Server.Message;
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
            public void execute(Client client, Message message) {
                doRoll(client);
            }
        },
        ASK_BUY {

            @Override
            public void execute(Client client, Message message) {
                if (message != null && message.message() != null && !message.message().isEmpty()) {
                    doBuy(client, message.message());
                }
            }


        },
        ASK_NEXT{
            @Override
            public void execute(Client client, Message message) {
                doNext(client);
            }
        };

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

        public static void doBuy(Client client, String propertyName) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    System.out.println("The property " + propertyName + " is available for purchase.");
                    System.out.println("Do you want to buy this property? (y/n): ");
                    String response = consoleReader.readLine().trim().toLowerCase();

                    PrintWriter writer = client.getWriter();
                    if (writer != null) {
                        if ("y".equals(response)) {
                            writer.println("BUY");
                            System.out.println("You chose to buy the property: " + propertyName + ".");
                            break;
                        } else if ("n".equals(response)) {
                            writer.println("DO_AUCTION");
                            System.out.println("You chose not to buy the property: " + propertyName + ". An auction will start.");
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter 'y' or 'n'.");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error during buy decision: " + e.getMessage());
                }
            }
        }

        public void doNext(Client client) {
            try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
                System.out.println("It's your turn. Choose an action: END, BUILD, BANKRUPT");

                String input = consoleReader.readLine().trim().toUpperCase();
                String response = "";

                if (input.equals("build") || input.equals("2") || input.equals("BUILD")) {
                    response = "BUILD";
                } else if (input.equals("bankrupt") || input.equals("end me") || input.equals("3") || input.equals("BANKRUPT")) {
                    response = "BANKRUPT";
                } else if (input.equals("end") || input.equals("1") || input.equals("endturn") || input.equals("END")) {
                    response = "END";
                }
                PrintWriter writer = client.getWriter();
                if ( writer!= null) {

                switch (response) {
                    case "BUILD":writer.println("BUILD");
                        break;
                    case "BANKRUPT":writer.println("BANKRUPT");
                        break;
                    case "END":writer.println("END");
                        break;
                    default:
                        System.out.println("Invalid Input");
                }}
            } catch (IOException e) {
                System.out.println("Error during next action selection: " + e.getMessage());
            }
        }

        public abstract void execute(Client client, Message message);
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
