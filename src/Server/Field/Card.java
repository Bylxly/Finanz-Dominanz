package Server.Field;

/**
 * Die Klasse Card repräsentiert eine Karte, die in Event- oder Community-Feldern gezogen werden kann.
 * Jede Karte hat eine Beschreibung, einen Betrag und eine Aktion, die ausgeführt wird.
 */
public record Card(String description, int amount, CardAction action) {

    /**
     * Enum, das die möglichen Aktionen einer Karte definiert.
     */
    public enum CardAction {
        COLLECT,
        PAY,
        GET_OUT_OF_JAIL_FREE,
        GO_TO_JAIL,
        ADVANCE_TO,
        GO_BACK,
        PAY_PER_PROPERTY,
        NEAREST_UTILITY,
        NEAREST_RAILROAD
    }
}
