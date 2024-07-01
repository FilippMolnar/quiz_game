package commons;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Score{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    private String name;
    private int score;

    /**
     * An empty constructor for a Score
     */
    private Score() {
        // for object mappers
    }

    /**
     * Constructor for a Score
     * @param name - the name of the player the score belongs to
     * @param score - the score
     */
    public Score(String name, int score) {
        this.name = name;
        this.score = score;
    }

    /**
     * Getter for the score
     * @return the score
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Setter for the score
     * @param score - the score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Getter for the name
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Add to the score
     * @param toAdd - the number to add to the score
     */
    public void addScore(int toAdd) {
        this.score += toAdd;
    }

    /**
     * Compares if other is equal to this instance
     * @param other - the object we are comparing
     * @return if this is equal to other
     */
    public boolean equals(Object other){
        if(other == this)
            return true;
        if(other instanceof Score that){
            return that.score == this.score && that.name.equals(this.getName());
        }
        return false;
    }

    /**
     * @return Human-readable format of this Class
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(name);
        str.append(": ");
        str.append(score);
        return str.toString();
    }
}
