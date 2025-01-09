package Server.State;

import Server.*;
import Server.Field.Property.Property;

import java.util.*;

public class TradeState extends SelectState {

    private Player tradingPartner;
    private Player requestingPlayer;
    private boolean check;
    private final Map<String, Object> demands;
    private final Map<String, Object> offers;

    public TradeState(Game game) {
        super(game);
        check = true;
        demands = new HashMap<>();
        offers = new HashMap<>();
        requestingPlayer = game.getActivePlayer();
    }

    @Override
    protected Map<Integer, Object> getEligibleObjects() {
        int mapIndex = 1;
        Map<Integer, Object> selectablePlayers = new HashMap<>();
        for (Player player : game.getPlayers()) {
            if (requestingPlayer != player) {
                selectablePlayers.put(mapIndex++, player);
            }
        }
        return selectablePlayers;
    }

    @Override
    protected void performAction(Object player) {
        /*
          You're trading with {Playername}

          //Part 1

          You're now selecting the things you demand from {Playername}

          Things selected:
          {Index : Name}    (e.g. A1 : Theaterstraße)
          {Index : Name}
          {money}           (e.g. AM: 1200)

          or none (if nothing selected)

          Their owned Properties:
          {Index : Name}
          {Index : Name}    (e.g. B2: Schillerstraße)
          {Index : Name}

          Their money:
          {money}           (e.g. M: 3250)

          Choose one of the following options (e.g. rA1 (Remove Property 1); aM100 (add 100 Money))
          a: add, r: remove, c: clear offer, s: switch to your offer, q: quit

          Part 2

          You're now selecting the things you offer to {Playername}

          Your current offer:
          {Index : Name}    (e.g. A1 : Berliner Platz)
          {Index : Name}
          {money}           (e.g. AM: 500)

          Your owned Properties:
          {Index : Name}
          {Index : Name}    (e.g. B2: Schlossallee)
          {Index : Name}

          Your money:
          {money}           (e.g. M: 700)

          Choose one of the following options (e.g. r1 (Remove Property 1); am100 (add 100 Money))
          a: add, r: remove, c: clear offer, s: switch to what you demand, q: quit



          other Player:
          {Playername} wants to trade with you.
          They demand from you:
          {Property}
          {Property}
          {money}

          They offer you:
          {Property}
          {Property}
          {money}

          Do you accept this trade (y/n)
          Do you want to make a counteroffer? (y/n)
         */
        tradingPartner = (Player) player;
        dialog(tradingPartner);
    }

    private void dialog(Player player) {
        do {
            List<String> message = buildTradeMessage(player);
            requestingPlayer.sendObject(new Message(MsgType.SELECT_TRADE, "build"));
            requestingPlayer.sendObject(message);

            String selection = requestingPlayer.recieveMessage();
            processSelection(selection, player);
        } while (check);
    }

    private List<String> buildTradeMessage(Player player) {
        List<String> message = new ArrayList<>();
        message.add("You're trading with " + tradingPartner.getName());
        message.add(player == requestingPlayer
                ? "You're now selecting the things you offer " + tradingPartner.getName()
                : "You're now selecting the things you demand from " + player.getName());

        message.add("");
        message.add("Things selected:");
        buildSelectedMessage(message, player == requestingPlayer ? offers : demands);

        message.add("");
        message.add(player == requestingPlayer ? "Your owned properties:" : "Their owned properties:");
        message.addAll(getAvailableProperties(player));

        message.add("");
        message.add(getMoneyInfo(player));

        message.add("");
        message.add("Choose one of the following options (e.g. rA1 (Remove Property 1); aM100 (add 100 Money))");
        message.add(player == requestingPlayer
                ? "a: add, r: remove, c: clear offer, s: switch to your demands, f: finish trade, q: quit"
                : "a: add, r: remove, c: clear demands, s: switch to your offer, f: finish trade, q: quit");
        return message;
    }

    private List<String> getAvailableProperties(Player player) {
        List<String> availableProperties = new ArrayList<>();

        int index = 1;
        for (Property property : player.getProperties()) {
            if (!(demands.containsValue(property) || offers.containsValue(property))) {
                availableProperties.add("B" + index++ + ": " + property.getName());
            }
        }
        return availableProperties;
    }

    private String getMoneyInfo(Player player) {
        //Only shows money available to add to trade
        int moneyAddedToTrade = 0;
        if (player == requestingPlayer && offers.containsKey("M")) {
            moneyAddedToTrade = (Integer) offers.get("M");
        }
        else if (player != requestingPlayer && demands.containsKey("M")) {
            moneyAddedToTrade = (Integer) demands.get("M");
        }

        return (player == requestingPlayer
                ? "Your money: " + (player.getMoney() - moneyAddedToTrade)
                : "Their money: " + (player.getMoney() - moneyAddedToTrade));
    }

    private void buildSelectedMessage(List<String> message, Map<String, Object> selectionMap) {
        if (selectionMap.isEmpty()) {
            message.add("None");
        }
        for (String index : selectionMap.keySet()) {
            if (selectionMap.get(index) instanceof Property) {
                message.add(index + ": " + ((Property) selectionMap.get(index)).getName());
            } else {
                message.add(index + ": " + selectionMap.get(index));
            }
        }
    }

    private void processSelection(String selection, Player player) {
        Map<String, Object> selectionMap = (player == requestingPlayer) ? offers : demands;
        char action = selection.charAt(0);

        switch (action) {
            case 'a': processAddAction(selection, player, selectionMap); break;
            case 'r': processRemoveAction(selection, selectionMap); break;
            case 'c': selectionMap.clear(); break;
            case 's': dialog(player == requestingPlayer ? tradingPartner : requestingPlayer); break;
            case 'f': finalizeTrade(); break;
            case 'q': quitTrade(); break;
            default: requestingPlayer.sendObject(new Message(MsgType.INFO, "Invalid selection")); break;
        }
    }

    private void processAddAction(String selection, Player player, Map<String, Object> selectionMap) {
        char type = selection.charAt(1);
        if (type == 'M') {
            int amount = Integer.parseInt(selection.substring(2));
            if ((Integer) selectionMap.getOrDefault("M", 0) + amount > player.getMoney()) {
                requestingPlayer.sendObject(new Message(MsgType.INFO, player.getName() + " has not enough money"));
            }
            else {
                selectionMap.put("M", (Integer) selectionMap.getOrDefault("M", 0) + amount);
            }
        } else if (type == 'B') {
            int propertyIndex = Integer.parseInt(selection.substring(2));
            if (propertyIndex <= player.getProperties().size() && propertyIndex > 0) {
                selectionMap.put("A" + (selectionMap.containsKey("M") ? (selectionMap.size()) : selectionMap.size() + 1),
                                player.getProperties().get(propertyIndex - 1));
            } else {
                requestingPlayer.sendObject(new Message(MsgType.INFO, "Invalid index"));
            }
        }
    }

    private void processRemoveAction(String selection, Map<String, Object> selectionMap) {
        char type = selection.charAt(1);
        if (type == 'M') {
            int amount = Integer.parseInt(selection.substring(2));
            if (selectionMap.containsKey("M")) {
                int newAmount = (int) selectionMap.get("M") - amount;
                if (newAmount > 0) {
                    selectionMap.put("M", newAmount);
                } else {
                    selectionMap.remove("M");
                }
            }
        } else if (type == 'A') {
            String key = "A" + selection.substring(2);
            if (selectionMap.containsKey(key)) {
                selectionMap.remove(key);
            }
            else {
                requestingPlayer.sendObject(new Message(MsgType.INFO, "Invalid index"));
            }
        }
    }

    private void finalizeTrade() {
        // Prevents empty trading requests
        if (demands.isEmpty() && offers.isEmpty()) {
            requestingPlayer.sendObject(new Message(MsgType.INFO, "You can't request empty trades"));
            return;
        }

        tradingPartner.sendObject(new Message(MsgType.SELECT_TRADE, "request"));
        tradingPartner.sendObject(generateTradingRequest());
        String answer = tradingPartner.recieveMessage();

        if (answer.equalsIgnoreCase("y")) {
            requestingPlayer.sendObject(
                    new Message(MsgType.INFO, tradingPartner.getName() + " accepted you trade request"));
            startTransfer();
            check = false;
        }
        else if (answer.equalsIgnoreCase("n")) {
            requestingPlayer.sendObject(
                    new Message(MsgType.INFO, tradingPartner.getName() + " rejected you trade request"));
            tradingPartner.sendObject(
                    new Message(MsgType.GET_ANSWER, "Do you want to make a counteroffer? (y/n)"));
            answer = tradingPartner.recieveMessage();
            if (answer.equalsIgnoreCase("y")) {
                demands.clear();
                offers.clear();
                Player tmp = requestingPlayer;
                requestingPlayer = tradingPartner;
                tradingPartner = tmp;
                performAction(tradingPartner);
            }
            else {
                check = false;
            }
        }
    }

    private void quitTrade() {
        requestingPlayer.sendObject(new Message(MsgType.INFO, "Trade ended"));
        check = false;
    }

    private List<String> generateTradingRequest() {
        List<String> message = new ArrayList<>();
        message.add(requestingPlayer.getName() + " sent you a trade request");

        message.add("They demand from you:");
        addItemsToMessage(message, demands.values());
        message.add("");

        message.add("You own the following properties:");
        addPropertiesToMessage(message, tradingPartner);
        message.add("Your money: " + tradingPartner.getMoney());
        message.add("");

        message.add("They offer you:");
        addItemsToMessage(message, offers.values());
        message.add("");

        message.add("They own the following properties:");
        addPropertiesToMessage(message, requestingPlayer);
        message.add("Their money: " + requestingPlayer.getMoney());
        message.add("");

        message.add("Do you accept the trade (y/n)");
        return message;
    }

    private void addItemsToMessage(List<String> message, Collection<Object> items) {
        for (Object obj : items) {
            if (obj instanceof Property) {
                message.add(((Property) obj).getName());
            } else {
                message.add(String.valueOf(obj));
            }
        }
    }

    private void addPropertiesToMessage(List<String> message, Player player) {
        for (Property property : player.getProperties()) {
            message.add(property.getName());
        }
    }

    private void startTransfer() {
        transferObjects(demands, tradingPartner, requestingPlayer);
        transferObjects(offers, requestingPlayer, tradingPartner);
    }

    private void transferObjects(Map<String, Object> objects, Player fromPlayer, Player toPlayer) {
        for (Object object : objects.values()) {
            if (object instanceof Property property) {
                GameUtilities.transferProperty(fromPlayer, toPlayer, property);
            } else if (object instanceof Integer amount) {
                GameUtilities.transferMoney(fromPlayer, toPlayer, amount);
            }
        }
        objects.clear();
    }

    @Override
    public String getNoSuitableObjectsMessage() {
        // shouldn't happen
        return "There are no players to trade with";
    }

    @Override
    public String getSelectPropertyMessage() {
        return "Select a player to trade with:";
    }

    @Override
    public String generateObjectInfoMessage(int index, Map<Integer, Object> objectMap) {
        return "";
    }

    @Override
    public String getChooseMessage() {
        return "Choose a player:";
    }
}

