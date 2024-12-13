package Client;

import Server.Field.Property.Knast;
import Server.Field.Property.Property;
import Server.Field.Property.Street;
import Server.Message;
import Server.MsgType;
import Server.State.AuctionState;
import Server.State.GameState;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Action {
    private int actionId;
    private String description;
    public static boolean rollTriggered = false;
    private static String currentAction = "";

    public enum ServerMessage {
        ASK_SERVER {
            @Override
            public void execute(Client client, Message message) {
                doServer(client, message);
            }
        },
        ASK_ROLL {
            @Override
            public void execute(Client client, Message message) {
                //doRoll(client);
                client.getDraw().setButtonActive("btnRoll",true);
            }
        },
        ASK_BUY {

            @Override
            public void execute(Client client, Message message) {
                if (message != null && message.message() != null && !message.message().isEmpty()) {
                    //doBuy(client, message.message());
                    client.getDraw().setButtonActive("btnBuyY",true);
                    client.getDraw().setButtonActive("btnBuyN",true);
                }
            }
        },
        ASK_NEXT {
            @Override
            public void execute(Client client, Message message) {
                //doNext(client);
                client.getDraw().setButtonActive("btnNextEND", true);
                client.getDraw().setButtonActive("btnNextBUILD", true);
                client.getDraw().setButtonActive("btnNextBANKRUPT", true);
            }
        },
        BUILD_SELECT_PROPERTY {
            @Override
            public void execute(Client client, Message message) {
                doBuild(client);
            }
        },
        DO_AUCTION{
            @Override
            public void execute(Client client, Message message) {
                doAuction(client, message);
            }
        }
        ;

        public static void doServer(Client client, Message message) {
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
                PrintWriter writer = client.getWriter();
                if (writer != null) {
                    writer.println("ROLL");
                }
            } catch (Exception e) {
                System.out.println("Error during roll: " + e.getMessage());
            } finally {
                rollTriggered = false;
                setCurrentAction("");
            }
        }

        public static synchronized void doRollGUI(Client client) {
            if (rollTriggered) {
                System.out.println("Roll already in progress.");
                return;
            }

            rollTriggered = true;
            try {
                PrintWriter writer = client.getWriter();
                if (writer != null) {
                    writer.println("ROLL");
                    System.out.println("ROLL command sent to server.");
                }
            } catch (Exception e) {
                System.out.println("Error during roll: " + e.getMessage());
            } finally {
                rollTriggered = false;
                setCurrentAction("");
            }
        }

        public static synchronized void doBuyGUI(Client client, boolean buy) {
            setCurrentAction("BUY");
            try {
                PrintWriter writer = client.getWriter();
                if (writer != null) {
                    if (buy) {
                        writer.println("BUY");
                    } else {
                        writer.println("DO_AUCTION");
                    }
                }
            } finally {
                setCurrentAction("");
            }
        }

        public static void doBuy(Client client, String propertyName) {
            setCurrentAction("BUY");
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                System.out.println("The property " + propertyName + " is available for purchase.");
                System.out.print("Do you want to buy this property? (y/n): ");
                String response = consoleReader.readLine().trim().toLowerCase();

                PrintWriter writer = client.getWriter();
                if (writer != null) {
                    if ("y".equals(response)) {
                        writer.println("BUY");
                        System.out.println("You chose to buy the property: " + propertyName);
                    } else if ("n".equals(response)) {
                        writer.println("DO_AUCTION");
                        System.out.println("You chose not to buy the property: " + propertyName + ". An auction will start.");
                    } else {
                        System.out.println("Invalid input. Please enter 'y' or 'n'.");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error during buy decision: " + e.getMessage());
            } finally {
                setCurrentAction("");
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
            setCurrentAction("END");
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                System.out.println("Choose an action: END, BUILD, BANKRUPT, or just press Enter to END.");
                String input = consoleReader.readLine().trim();
                String response = "";

                if ("build".equalsIgnoreCase(input) || "2".equalsIgnoreCase(input)) {
                    response = "BUILD";
                } else if ("bankrupt".equalsIgnoreCase(input) || "end me".equalsIgnoreCase(input) || "3".equalsIgnoreCase(input)) {
                    response = "BANKRUPT";
                } else if ("end".equalsIgnoreCase(input) || input.isEmpty()) {
                    response = "END";
                }

                PrintWriter writer = client.getWriter();
                if (writer != null) {
                    writer.println(response);
                }
            } catch (IOException e) {
                System.out.println("Error during next action selection: " + e.getMessage());
            } finally {
                setCurrentAction("");
            }
        }

        public static void doNextGUI(Client client, String option) {
            setCurrentAction("END");
            try {
                PrintWriter writer = client.getWriter();
                if (writer != null) {
                    String response = "";

                    if ("BUILD".equals(option) ) {
                        response = "BUILD";
                    } else if ("BANKRUPT".equals(option)) {
                        response = "BANKRUPT";
                    } else if ("END".equals(option)) {
                        response = "END";
                    }

                    writer.println(response);
                    System.out.println("Sent action to server: " + response);
                }
            } finally {
                setCurrentAction("");
            }
        }

        public static void doBuild(Client client) {
            setCurrentAction("BUILD");
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            //Check on which properties the player can build
            List<Street> streets = new ArrayList<>();
            for (Property property : client.getGame().getActivePlayer().getProperties()) {
                if (property instanceof Street street && street.getHouses() < 5 && street.getColorGroup().isComplete()) {
                    streets.add(street);
                }
            }

            //Print dialogue to player
            if (!streets.isEmpty()) {
                System.out.println("You can build on the following properties:");
                int index = 1;
                Map<Integer, Property> propertyMap = new HashMap<>();
                for (Street street : streets) {
                    if (street.getColorGroup().isComplete()) {
                        System.out.println(index + ": " + street.getName() + " Kosten: " + street.getHousePrice());
                        sortedProperties.put(index, street);
                        index++;
                    }
                }

                //Player selects property
                System.out.print("Choose a property: ");
                try {
                    int selection = Integer.parseInt(consoleReader.readLine());
                    PrintWriter writer = client.getWriter();
                    if (writer != null) {
                        writer.println(client.getGame().getActivePlayer().getProperties().indexOf(propertyMap.get(selection)));
                    }
                } catch (IOException e) {
                    System.out.println("Error during building: " + e.getMessage());
                }
            } else {
                System.out.println("No properties available for building.");
                doNext(client);
            }
            setCurrentAction("");
        }


        public static void doAuction(Client client, Message message) {
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

    // currentAction Management
    public static synchronized void setCurrentAction(String action) {
        currentAction = action;
    }

    public static synchronized String getCurrentAction() {
        return currentAction;
    }
}
