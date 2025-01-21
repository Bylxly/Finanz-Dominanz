package Client;

import Server.Field.Property.Knast;
import Server.Game;
import Server.Message;
import Server.MsgType;
import Server.State.GameState;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Action {
    private int actionId;
    private String description;

    public enum ServerMessage {
        ASK_SERVER {
            @Override
            public void execute(Client client, Message message) {
                doServer(client);
            }
        },
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
        ASK_KNAST {
            @Override
            public void execute(Client client, Message message) {
                doKnast(client);
            }
        },
        ASK_NEXT{
            @Override
            public void execute(Client client, Message message) {
                doNext(client);
            }
        },
        ASK_NO_MONEY{
            @Override
            public void execute(Client client, Message message) {
                doNoMoney(client, message);
            }
        },
        SELECT_OBJECT {
            @Override
            public void execute(Client client, Message message) {
                doSelect(client);
            }
        },
        DO_AUCTION{
            @Override
            public void execute(Client client, Message message) {
                doAuction(client);
            }
        },
        SELECT_TRADE{
            @Override
            public void execute(Client client, Message message) {
                doSelectTrade(client, message);
            }
        },
        GET_ANSWER {
            @Override
            public void execute(Client client, Message message) {
                doGetAnswer(client, message);
            }
        },
        GET_ANSWER_KEEP_LIFT {
            @Override
            public void execute(Client client, Message message) {
                doGetAnswerKeepLift(client, message);
            }
        },
        CLOSE_CONNECTION {
            @Override
            public void execute(Client client, Message message) {
                doCloseConnection(client);
            }
        }
        ;

        public static void doServer(Client client) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            // Create or join a game
            try {
                String msgClient;
                System.out.println("Would you like to CREATE or JOIN a game?");
                msgClient = consoleReader.readLine();
                if (msgClient.equalsIgnoreCase("CREATE")) {
                    client.getWriter().println("CREATE");
                }
                //Temp for quick join
                else if (msgClient.isEmpty()) {
                    client.getWriter().println("CREATE_CUSTOM");
                }
                else if (msgClient.equalsIgnoreCase("JOIN") || msgClient.equalsIgnoreCase("j")) {
                    System.out.print("Enter code of the game: ");
                    msgClient = consoleReader.readLine();
                    //Temp for quick join
                    if (msgClient.isEmpty()) {
                        client.getWriter().println("ABCDEF");
                    }
                    else {
                        client.getWriter().println(msgClient);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

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

        public static void doKnast(Client client) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("You are in the Knast!");
            try {
                if (((Knast) client.getGame().getActivePlayer().getCurrentField()).getRollAmount(client.getGame().getActivePlayer()) < 3) {
                    System.out.println("What do you want to do? ROLL or PAY");
                    if (consoleReader.readLine().equalsIgnoreCase("ROLL")) {
                        client.getWriter().println("ROLL");
                    }
                    else {
                        client.getWriter().println("PAY");
                    }
                }
                else {
                    System.out.println("You've already rolled three times, you now can only buy yourself a way out");
                    consoleReader.readLine();
                    client.getWriter().println("PAY");
                }
            } catch (IOException e) {
                System.out.println("Error during knast decision: " + e.getMessage());
            }

        }

        public static void doNext(Client client) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                System.out.println("It's your turn. Choose an action: END, BUILD, SELL, MORTGAGE, LIFT, TRADE, BANKRUPT");

                String input = consoleReader.readLine().trim().toUpperCase();
                PrintWriter writer = client.getWriter();

                if (writer != null) {
                    switch (input) {
                        case "1":
                        case "END":
                        case "ENDTURN":
                        case "":
                            writer.println("END");
                            break;
                        case "2":
                        case "BUILD":
                            writer.println("BUILD");
                            break;
                        case "3":
                        case "SELL":
                            writer.println("SELL");
                            break;
                        case "4":
                        case "MORTGAGE":
                            writer.println("MORTGAGE");
                            break;
                        case "5":
                        case "LIFT":
                            writer.println("LIFT");
                            break;
                        case "6":
                        case "TRADE":
                            writer.println("TRADE");
                            break;
                        case "7":
                        case "BANKRUPT":
                        case "END ME":
                            writer.println("BANKRUPT");
                            break;
                        default:
                            System.out.println("Invalid Input");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error during next action selection: " + e.getMessage());
            }
        }

        public static void doNoMoney(Client client, Message message) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            boolean includeQuit = message.message().toLowerCase().contains("quit");

            System.out.println(message.message());
            do {
                try {
                    String selection = consoleReader.readLine();
                    if (selection.equalsIgnoreCase("mortgage")) {
                        client.getWriter().println("MORTGAGE");
                        return;
                    } else if (selection.equalsIgnoreCase("trade")) {
                        client.getWriter().println("TRADE");
                        return;
                    } else if (selection.equalsIgnoreCase("bankrupt")) {
                        client.getWriter().println("BANKRUPT");
                        return;
                    } else if (includeQuit && selection.equalsIgnoreCase("quit")) {
                        client.getWriter().println("QUIT");
                        return;
                    } else {
                        System.out.println("Invalid selection. Please try again.");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } while (true);
        }

        public static void doSelect(Client client) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            ObjectInputStream objectReader = client.getObjectReader();

            try {
                Object o = objectReader.readObject();
                if (o instanceof List<?>) {
                    List<String> message = (List<String>) o;

                    for (String s : message) {
                        System.out.println(s);
                    }

                    //TODO: add check for invalid inputs
                    String input = consoleReader.readLine();
                    PrintWriter writer = client.getWriter();
                    if (input.isEmpty() || input.equalsIgnoreCase("quit")) {
                        writer.println("-1");
                    }
                    else {
                        int selection = Integer.parseInt(input);
                        writer.println(selection);
                    }
                }
                else if (o instanceof Message) {
                    System.out.println(((Message) o).message());
                }
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }

        }


        public static void doAuction(Client client) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = client.getWriter();
            System.out.println(client.getGame().getActivePlayer().getCurrentField().getName() + " will now be auctioned!");
            System.out.println("You can either write the amount you want to pay or 'QUIT' to quit the auction");

            AtomicBoolean auctionEnded = new AtomicBoolean(false);
            AtomicInteger lastBid = new AtomicInteger(0); // Local storage for the latest bid

            Thread printBid = new Thread(() -> {
                try {
                    ObjectInputStream objectReader = client.getObjectReader();
                    while (!auctionEnded.get()) {
                        Object o = objectReader.readObject();
                        if (o instanceof Game) {
                            return;
                        }
                        Message msg = (Message) o;
                        if (msg.messageType() == MsgType.NEW_BID) {
                            int newBid = Integer.parseInt(msg.message());
                            lastBid.set(newBid);
                            System.out.println("New Bid: " + newBid);
                        }
                        else if (msg.messageType() == MsgType.QUIT_AUCTION) {
                            System.out.println(msg.message());
                            System.out.println("Press ENTER to continue");
                            auctionEnded.set(true);
                        }
                        else if (msg.messageType() == MsgType.INFO) {
                            System.out.println(msg.message());
                        }
                        else if (msg.messageType() == MsgType.END_AUCTION) {
                            writer.println("QUIT_AUCTION");
                            System.out.println(msg.message());
                            System.out.println("Press ENTER to continue");
                            auctionEnded.set(true);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error in auction thread: " + e.getMessage());
                }
            });

            try {
                printBid.start();
                while (!auctionEnded.get()) {

                    String userInput = consoleReader.readLine();

                    if (auctionEnded.get()) break; // Double-check flag in case of race condition

                    if (userInput.equalsIgnoreCase("QUIT")) {
                        writer.println("QUIT_AUCTION");
                    }
                    else {
                        try {
                            int bid = Integer.parseInt(userInput);
                            if (bid > lastBid.get()) { // Compare with the locally stored last bid
                                writer.println(bid);
                            } else {
                                System.out.println("Your bid is lower than the current bid.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a valid number or 'QUIT'.");
                        }
                    }
                }
                auctionEnded.set(true); // Ensure thread ends
                printBid.join();
            } catch (IOException e) {
                System.out.println("Error during auction decision: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("Auction interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        public static void doSelectTrade(Client client, Message option) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = client.getWriter();
            ObjectInputStream objectReader = client.getObjectReader();

            try {
                Object obj = objectReader.readObject();
                List<String> message = List.of();

                if (obj instanceof List<?>) {
                    message = (List<String>) obj;
                }

                for (String s : message) {
                    System.out.println(s);
                }

                // Validierung der Benutzereingabe
                String input;
                do {
                    System.out.print("Enter your choice: ");
                    input = consoleReader.readLine();

                    if (option.message().equals("build") ? isValidInputBuild(input) : isValidInputRequest(input)) {
                        System.out.println("Invalid input. Please try again.");
                    }
                } while (option.message().equals("build") ? isValidInputBuild(input) : isValidInputRequest(input));

                // Sende gültige Eingabe an den Server
                writer.println(input);

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        private static boolean isValidInputBuild(String input) {
            String regex = "a[B|M]\\d+|r[A|M]\\d+|[c|s|f|q]";
            return !input.matches(regex);
        }

        private static boolean isValidInputRequest(String input) {
            String regex = "[yn]";
            return !input.matches(regex);
        }

        public static void doGetAnswer(Client client, Message message) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = client.getWriter();

            try {
                String input;
                do {
                    System.out.println(message.message());
                    input = consoleReader.readLine();

                    if (isValidInputRequest(input)) {
                        System.out.println("Invalid input. Please try again.");
                    }
                } while (isValidInputRequest(input));

                writer.println(input);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static void doGetAnswerKeepLift(Client client, Message message) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(message.message());
            do {
                try {
                    String selection = consoleReader.readLine();
                    if (selection.equalsIgnoreCase("keep")) {
                        client.getWriter().println("KEEP");
                        return;
                    }
                    else if (selection.equalsIgnoreCase("lift")) {
                        client.getWriter().println("LIFT");
                        return;
                    }
                    else {
                        System.out.println("Invalid selection. Please try again.");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } while (true);
        }

        public static void doCloseConnection(Client client) {
            client.disconnect();
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
