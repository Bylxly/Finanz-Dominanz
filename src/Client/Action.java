package Client;

import Server.State.GameState;

public class Action {
    private int actionId;
    private String description;

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

    public void execute() {
        System.out.println("Executing action: " + description);
    }

    public boolean allowAction(GameState state) {
        if (state != null) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Action ID: " + actionId + ", Description: " + description;
    }
}

