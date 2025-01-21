package Server.Field;

import Server.*;
import Server.Field.Property.Property;
import Server.Field.Property.Street;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Die Klasse CommunityField repräsentiert ein Gemeinschaftsfeld im Spiel.
 * Wenn ein Spieler auf diesem Feld landet, zieht er eine Karte, die eine spezielle Aktion auslöst.
 * Die Karten werden gemischt und nacheinander gezogen.
 */
public class CommunityField extends Field {

    private static final List<Card> cards = new ArrayList<>();
    private static final List<Card> drawnCards = new ArrayList<>();
    private final Game game;

    /**
     * Konstruktor für die Klasse CommunityField.
     *
     * @param name Der Name des Feldes.
     * @param game Die Referenz auf das Spiel.
     */
    public CommunityField(String name, Game game) {
        super(name);
        this.game = game;
    }

    static {
        cards.add(new Card("You set aside time every week to hang out with your elderly neighbor – you’ve heard some amazing stories! COLLECT $100", 100, Card.CardAction.COLLECT));
        cards.add(new Card("You organize a group to clean up your town’s footpaths. COLLECT $50", 50, Card.CardAction.COLLECT));
        cards.add(new Card("You volunteered at a blood donation. There were free cookies! COLLECT $10", 10, Card.CardAction.COLLECT));
        cards.add(new Card("You buy a few bags of cookies from that school bake sale. Yum! PAY $50", 50, Card.CardAction.PAY));
        //TODO: Out of Jail Card same in EventField
        //cards.add(new Card("You rescue a puppy – and you feel rescued, too!", 0, Card.CardAction.GET_OUT_OF_JAIL_FREE));
        cards.add(new Card("You organize a street party so people on your road can get to know each other. COLLECT $10", 10, Card.CardAction.COLLECT));
        cards.add(new Card("Blasting music late at night? Your neighbors do not approve. GO TO JAIL.", 0, Card.CardAction.GO_TO_JAIL));
        cards.add(new Card("You help your neighbor bring in her groceries. She makes you lunch to say thanks! COLLECT $20", 20, Card.CardAction.COLLECT));
        cards.add(new Card("You help build a new school playground – then you get to test the slide! COLLECT $100", 100, Card.CardAction.COLLECT));
        cards.add(new Card("You spend the day playing games with kids at a local children’s hospital. COLLECT $100", 100, Card.CardAction.COLLECT));
        cards.add(new Card("You go to the local school’s car wash fundraiser – but you forget to close your windows! PAY $100", 100, Card.CardAction.PAY));
        cards.add(new Card("Just when you think you can’t go another step, you finish that foot race – and raise money for your local hospital! Go to GO! ADVANCE TO GO. COLLECT $200", 200, Card.CardAction.ADVANCE_TO));
        cards.add(new Card("You help your neighbors clean up their Gardens after a big storm. COLLECT $200", 200, Card.CardAction.COLLECT));
        cards.add(new Card("Your fuzzy friends at the animal shelter will be thankful for your donation. PAY $50", 50, Card.CardAction.PAY));
        cards.add(new Card("You should have volunteered for that home improvement project – you would have learned valuable skills! FOR EACH HOUSE YOU OWN, PAY $40. FOR EACH HOTEL YOU OWN, PAY $115.", 0, Card.CardAction.PAY_PER_PROPERTY));
        cards.add(new Card("You organize a bake sale for your local school. COLLECT $25", 25, Card.CardAction.COLLECT));
        shuffleCards();
    }

    /**
     * Führt die Aktion aus, wenn ein Spieler auf diesem Feld landet.
     * Der Spieler zieht eine Karte und die entsprechende Aktion wird ausgeführt.
     *
     * @param player Der Spieler, der auf dem Feld landet.
     * @return true, da die Aktion erfolgreich ausgeführt wurde.
     */
    @Override
    public boolean startAction(Player player) {
        Card drawnCard = drawCard();
        player.sendObject(new Message(MsgType.INFO, "You landed on a community field! \n" +
                drawnCard.description()));
        switch (drawnCard.action()) {
            case COLLECT: collect(player, drawnCard.amount()); break;
            case PAY: pay(player, drawnCard.amount()); break;
            case GO_TO_JAIL: goToJail(player); break;
            case ADVANCE_TO: advanceToStart(); break;
            case PAY_PER_PROPERTY: payPerProperty(player); break;
            default: return false;
        }
        return true;
    }

    private static void shuffleCards() {
        Collections.shuffle(cards);
    }

    /**
     * Zieht eine Karte aus dem Stapel.
     * Wenn der Stapel leer ist, werden die gezogenen Karten zurückgelegt und gemischt.
     *
     * @return Die gezogene Karte.
     */
    public Card drawCard() {
        if (cards.isEmpty()) {
            cards.addAll(drawnCards);
            drawnCards.clear();
            shuffleCards();
        }
        drawnCards.add(cards.get(0));
        return cards.remove(0);
    }

    private void collect(Player player, int amount) {
        GameUtilities.receiveFromBank(player, amount);
    }

    private void pay(Player player, int amount) {
        GameUtilities.payBank(player, amount);
    }

    private void payPerProperty(Player player) {
        for (Property property : player.getProperties()) {
            if (property instanceof Street street) {
                if (street.getHouses() == 5) {
                    GameUtilities.payBank(player, 115);
                }
                else {
                    GameUtilities.payBank(player, 40 * street.getHouses());
                }
            }
        }
    }

    private void goToJail(Player player) {
        game.movePlayerToKnast(player);
    }

    private void advanceToStart() {
        Field desiredField = null;
        for (Field field : game.getBoard()) {
            if (field instanceof Start) {
                desiredField = field;
                break;
            }
        }
        if (desiredField != null) {
            game.movePlayerToField(desiredField);
        }
    }

}