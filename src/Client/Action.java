package Client;

import Server.Field.Property.Knast;
import Server.Game;
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
    private static boolean debug = false;

    public enum ServerMessage {
//        ASK_SERVER {
//            @Override
//            public void execute(Client client, Message message) {
//                doServer(client, message);
//            }
//        },
        ASK_ROLL {
            @Override
            public void execute(Client client, Message message) {
                client.getDraw().setButtonActive("btnRoll",true);
            }
        },
        ASK_BUY {

            @Override
            public void execute(Client client, Message message) {
                if (message != null && message.message() != null && !message.message().isEmpty()) {
                    if (debug){ doBuy(client, message.message());}else{
                    client.getDraw().setButtonActive("btnBuyY",true);
                    client.getDraw().setButtonActive("btnBuyN",true);}
                }
            }


        },
        ASK_KNAST {
            @Override
            public void execute(Client client, Message message) {
                doKnast(client);
            }
        },
        ASK_NEXT {
            @Override
            public void execute(Client client, Message message) {
                if (debug){ doNext(client);} else{
                client.getDraw().setButtonActive("btnNextEND", true);
                client.getDraw().setButtonActive("btnNextBUILD", true);
                client.getDraw().setButtonActive("btnNextBANKRUPT", true);}
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

//        public static void doServer(Client client) {
//            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
//            // Create or join a game
//            try {
//                PrintWriter writer = client.getWriter();
//                if (writer != null) {
//                    writer.println("ROLL");
//                }
//            } catch (Exception e) {
//                System.out.println("Error during roll: " + e.getMessage());
//            } finally {
//                rollTriggered = false;
//                setCurrentAction("");
//            }
//        }

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
                System.out.println("It's your turn. Choose an action: END, BUILD, SELL, MORTGAGE, LIFT, TRADE, BANKRUPT");

                String input = consoleReader.readLine().trim();
                String response = "";

                //TODO: simplify

                if (input.equals("build") || input.equals("2") || input.equals("BUILD")) {
                    response = "BUILD";
                } else if (input.equalsIgnoreCase("sell") || input.equals("3")) {
                    response = "SELL";
                } else if (input.equalsIgnoreCase("mortgage") || input.equals("4")) {
                    response = "MORTGAGE";
                } else if (input.equalsIgnoreCase("lift") || input.equals("5")) {
                    response = "LIFT";
                } else if (input.equalsIgnoreCase("trade") || input.equals("6")) {
                    response = "TRADE";
                } else if (input.equals("bankrupt") || input.equals("end me") || input.equals("7") || input.equals("BANKRUPT")) {
                    response = "BANKRUPT";
                } else if (input.equals("end") || input.equals("1") || input.equals("endturn") || input.equals("END") || input.isEmpty()) {
                    response = "END";
                }
                PrintWriter writer = client.getWriter();
                if ( writer!= null) {

                switch (response) {
                    case "BUILD":writer.println("BUILD");
                        break;
                    case "SELL":writer.println("SELL");
                        break;
                    case "MORTGAGE":writer.println("MORTGAGE");
                        break;
                    case "LIFT":writer.println("LIFT");
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

        public static void doNoMoney(Client client, Message message)  {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(message.message());
            do {
                try {
                    String selection = consoleReader.readLine();
                    if (selection.equalsIgnoreCase("mortgage")) {
                        client.getWriter().println("MORTGAGE");
                        return;
                    }
                    else if (selection.equalsIgnoreCase("trade")) {
                        client.getWriter().println("TRADE");
                        return;
                    }
                    else if (selection.equalsIgnoreCase("bankrupt")) {
                        client.getWriter().println("BANKRUPT");
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

    // currentAction Management
    public static synchronized void setCurrentAction(String action) {
        currentAction = action;
    }

    public static synchronized String getCurrentAction() {
        return currentAction;
    }
}
