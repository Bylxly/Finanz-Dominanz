// HandleAction.java
package Client;

import Client.Action.ServerMessage;

public class HandleAction {
    private static Client client;
    private static String currentField;
    private static String currentPlayer;

    public static void initialize(Client clientInstance, String field, String player) {
        client = clientInstance;
        currentField = field;
        currentPlayer = player;
    }

    public enum ActionType {
        OPTION1 {
            @Override
            public void action() {
                System.out.println("Option 1 selected for field: " + currentField);
            }
        },
        OPTION2 {
            @Override
            public void action() {
                System.out.println("Option 2 selected for field: " + currentField);
            }
        },
        OPTION3 {
            @Override
            public void action() {
                System.out.println("Option 3 selected for field: " + currentField);
            }
        },
        ROLL {
            @Override
            public void action() {
                System.out.println("Rolling dice for player: " + currentPlayer);
                Action.ServerMessage.doRollGUI(client);
                client.getDraw().setButtonActive("btnRoll", false);
            }
        },
        BUY_Y {
            @Override
            public void action() {
                System.out.println("Buy Yes selected");
                Action.ServerMessage.doBuyGUI(client, true);
                client.getDraw().setButtonActive("btnBuyY", false);
            }
        },
        BUY_N {
            @Override
            public void action() {
                System.out.println("Buy No selected");
                Action.ServerMessage.doBuyGUI(client, false);
                client.getDraw().setButtonActive("btnBuyN", false);
            }
        };

        public abstract void action();
    }
}
