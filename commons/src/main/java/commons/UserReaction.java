package commons;

import lombok.Getter;
import lombok.Setter;

public class UserReaction {
    @Getter @Setter
    private int gameID;
    @Getter @Setter
    private String username;
    @Getter
    private String reaction;

    /**
     * An empty constructor for a UserReaction
     */
    public UserReaction() {}

    /**
     * Constructor for a UserReaction
     * @param gameID - the game ID of the game the reaction was sent in
     * @param username - the username of the player who sent the reaction
     * @param reaction - which reaction was sent
     */
    public UserReaction(int gameID, String username, String reaction) {
        this.gameID = gameID;
        this.username = username;
        this.reaction = reaction;
        validate();
    }

    /**
     * Setter for a reaction
     * @param reaction - a reaction
     */
    public void setReaction(String reaction) {
        this.reaction = reaction;
        validate();
    }

    /**
     * Check if the reaction is one of the possible types
     */
    public void validate() {
        if (this.reaction == null || !(this.reaction.equals("angry") || this.reaction.equals("happy") || this.reaction.equals("angel"))) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @return Human-readable format of this Class
     */
    @Override
    public String toString() {
        return this.username+" reacted with an "+this.reaction+" emoji in game "+this.gameID;
    }
}
