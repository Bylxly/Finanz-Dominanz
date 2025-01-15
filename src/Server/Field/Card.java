package Server.Field;

public record Card(String description, int amount, CardAction action) {
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
