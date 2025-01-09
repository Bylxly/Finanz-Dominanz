package Server.Field;

import Server.*;
import Server.Field.Property.Property;
import Server.Field.Property.Street;
import Server.Field.Property.TrainStation;
import Server.Field.Property.Utility;
import Server.State.BuyFieldState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventField extends Field {

    private static final List<Card> cards = new ArrayList<>();
    private static final List<Card> drawnCards = new ArrayList<>();
    private final Game game;

    public EventField(String name, Game game) {
        super(name);
        this.game = game;
    }

    static {
        //Amount ist bei Advance to Position im Board Array
        cards.add(new Card("Advance to \"Go\". (Collect $200)", 0, Card.CardAction.ADVANCE_TO));
        cards.add(new Card("Advance to Opernplatz. If you pass Go, collect $200.", 24, Card.CardAction.ADVANCE_TO));
        cards.add(new Card("Advance to Seestraße. If you pass Go, collect $200.", 11, Card.CardAction.ADVANCE_TO));
        cards.add(new Card("Advance token to the nearest Utility. If unowned, you may buy it from the Bank. If owned, throw dice and pay owner a total 10 times the amount thrown.", 0, Card.CardAction.NEAREST_UTILITY));
        cards.add(new Card("Advance to the nearest Railroad. If unowned, you may buy it from the Bank. If owned, pay owner twice the rent.", 0, Card.CardAction.NEAREST_RAILROAD));
        cards.add(new Card("Bank pays you dividend of $50.", 50, Card.CardAction.COLLECT));
        //cards.add(new Card("Get out of Jail Free. This card may be kept until needed or traded/sold.", 0, Card.CardAction.GET_OUT_OF_JAIL_FREE));
        cards.add(new Card("Go Back Three Spaces.", -3, Card.CardAction.GO_BACK));
        cards.add(new Card("Go to Jail. Go directly to Jail. Do not pass GO, do not collect $200.", 0, Card.CardAction.GO_TO_JAIL));
        cards.add(new Card("Make general repairs on all your property: For each house pay $25, For each hotel pay $100.", 0, Card.CardAction.PAY_PER_PROPERTY));
        cards.add(new Card("Take a trip to Südbahnhof. If you pass Go, collect $200.", 5, Card.CardAction.ADVANCE_TO));
        cards.add(new Card("Pay Poor Tax of $15.", 15, Card.CardAction.PAY));
        cards.add(new Card("Take a walk on the Schlossallee. Advance token to Schlossallee.", 39, Card.CardAction.ADVANCE_TO));
        cards.add(new Card("You have been elected Chairman of the Board. Pay each player $50.", 50, Card.CardAction.PAY));
        cards.add(new Card("Your building and loan matures. Receive $150.", 150, Card.CardAction.COLLECT));
        shuffleCards();
    }

    @Override
    public boolean startAction(Player player) {
        Card drawnCard = drawCard();
        player.sendObject(new Message(MsgType.INFO, "You landed on a event field! \n" +
                drawnCard.description()));
        switch (drawnCard.action()) {
            case ADVANCE_TO: advanceTo(drawnCard.amount()); break;
            case NEAREST_UTILITY: nearestField(Utility.class); break;
            case NEAREST_RAILROAD: nearestField(TrainStation.class); break;
            case COLLECT: collect(player, drawnCard.amount()); break;
            case PAY: pay(player, drawnCard.amount()); break;
            case GO_BACK: goBack(drawnCard.amount()); break;
            case GO_TO_JAIL: goToJail(player); break;
            case PAY_PER_PROPERTY: payPerProperty(player); break;
            default: return false;
        }
        return true;
    }

    private static void shuffleCards() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            cards.addAll(drawnCards);
            drawnCards.clear();
            shuffleCards();
        }
        drawnCards.add(cards.get(0));
        return cards.remove(0);
    }

    private void advanceTo(int newPos) {
        game.movePlayerToPosition(newPos);
        if (newPos != 0) {
            game.setBuildOrExecuteState();
        }
    }

    private void goBack(int amount) {
        int newPos = game.getPlayerPosition() + amount;
        if (newPos < 0) {
            newPos = game.getBOARD_SIZE() + newPos;
        }
        game.movePlayerToPosition(newPos);
        game.setBuildOrExecuteState();
    }

    private void collect(Player player, int amount) {
        GameUtilities.receiveFromBank(player, amount);
    }

    private void pay(Player player, int amount) {
        GameUtilities.payBank(player, amount);
    }

    private void nearestField(Class<?> type) {
        for (int i = game.getPlayerPosition() + 1; i != game.getPlayerPosition(); i = (i + 1) % game.getBOARD_SIZE()) {
            if (type.isInstance(game.getBoard()[i])) {
                game.movePlayerToPosition(i);
                break;
            }
        }

        Property property = (Property) game.getActivePlayer().getCurrentField();
        if (!property.isOwned()) {
            game.setCurrentGameState(new BuyFieldState(game));
            game.getCurrentGameState().execute();
        }
        else if (property instanceof Utility utility) {
            utility.payRentTimes(game.getActivePlayer(), 10);
        }
        else if (property instanceof TrainStation trainStation) {
            trainStation.payRentCard(game.getActivePlayer());
        }
    }

    private void goToJail(Player player) {
        game.movePlayerToKnast(player);
    }

    private void payPerProperty(Player player) {
        for (Property property : player.getProperties()) {
            if (property instanceof Street street) {
                if (street.getHouses() == 5) {
                    GameUtilities.payBank(player, 100);
                }
                else {
                    GameUtilities.payBank(player, 25 * street.getHouses());
                }
            }
        }
    }

}
