package commons;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Activity implements Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Getter
    private String title;
    @Getter
    private String imagePath;
    @Getter
    private String source;

    @Getter
    private int consumption; // in Wh

    /**
     * Empty constructor for an Activity
     */
    public Activity() {
    }

    /**
     * Activity constructor
     *
     * @param title - title
     * @param consumption - consumption in Wh
     * @param imgPath - path to image
     * @param source - source
     */
    public Activity(String title, int consumption, String imgPath, String source) {
        this.imagePath = imgPath;
        this.title = title;
        this.consumption = consumption;
        this.source = source;
    }

    /*
     * Compares the consumption of two activities.
     * @parameters Activity to be compared to 'this'.
     * @return int -1 if this is greater than the input activity, 0 if both are equal
     * and 1 if the input activity is greater.
     */
    @Override
    public int compareTo(Object o) {
        if(!(o instanceof Activity)) {
            throw new IllegalArgumentException();
        }
        Activity a = (Activity) o;
        if (a.consumption > this.consumption) {
            return 1;
        }
        return this.consumption > a.consumption ? -1 : 0;
    }

    /**
     * Compares class to o
     *
     * @param o - object for comparison
     * @return if this and o are equal
     */
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Activity)) return false;
        Activity that = (Activity) o;
        if(source == null) return false;
        return this.title.equals(that.title)
                && this.consumption == that.consumption
                && this.imagePath.equals(that.imagePath)
                && this.source.equals(that.source);
    }

    /**
     * @return Human-readable format of this Class
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(title);
        str.append(":\n");
        str.append(consumption);
        str.append(" WH\n");
        str.append(imagePath);
        return str.toString();
    }

}
