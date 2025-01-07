package Client;

import Server.Field.Property.Knast;
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
                System.out.println("It's your turn. Choose an action: END, BUILD, MORTGAGE, TRADE, BANKRUPT");

                String input = consoleReader.readLine().trim();
                String response = "";

                //TODO: simplify

                if (input.equals("build") || input.equals("2") || input.equals("BUILD")) {
                    response = "BUILD";
                } else if (input.equalsIgnoreCase("mortgage") || input.equals("3")) {
                    response = "MORTGAGE";
                } else if (input.equalsIgnoreCase("trade") || input.equals("4")) {
                    response = "TRADE";
                } else if (input.equals("bankrupt") || input.equals("end me") || input.equals("5") || input.equals("BANKRUPT")) {
                    response = "BANKRUPT";
                } else if (input.equals("end") || input.equals("1") || input.equals("endturn") || input.equals("END") || input.isEmpty()) {
                    response = "END";
                }
                PrintWriter writer = client.getWriter();
                if ( writer!= null) {

                switch (response) {
                    case "BUILD":writer.println("BUILD");
                        break;
                    case "MORTGAGE":writer.println("MORTGAGE");
                        break;
                    case "TRADE":writer.println("TRADE");
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

        public static void doSelect(Client client) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            ObjectInputStream objectReader = client.getObjectReader();

            try {
                Object o = objectReader.readObject();
                if (o instanceof List<?>) {
                    //TODO: Check warning
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
                        Message msg = (Message) objectReader.readObject();
                        if (msg.messageType() == MsgType.NEW_BID) {
                            int newBid = Integer.parseInt(msg.message());
                            lastBid.set(newBid);
                            System.out.println("New Bid: " + newBid);
                        } else if (msg.messageType() == MsgType.END_AUCTION) {
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
                        System.out.println("You left the auction.");
                        break;
                    }

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
                auctionEnded.set(true); // Ensure thread ends
                printBid.join();
            } catch (IOException e) {
                System.out.println("Error during auction decision: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("Auction interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
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
