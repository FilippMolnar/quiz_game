package commons;

import java.util.Objects;

/**
 * This class is needed in order to convert JSON files to it and then to the actual Activity one
 */

public class TemplateActivity {
    public String title;
    public int consumption_in_wh;
    public String source;

    /**
     * Constructor for TemplateActivity
     * @param title - the title of an activity
     * @param consumption_in_wh - the consumption of an activity
     * @param source - the source of an activity
     */
    public TemplateActivity(String title, int consumption_in_wh, String source) {
        this.title = title;
        this.consumption_in_wh = consumption_in_wh;
        this.source = source;
    }

    /**
     * Getter for the title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title
     * @param title - the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the source
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Setter for the source
     * @param source - the source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Getter for the consumption
     * @return the consumption
     */
    public int getConsumptionInWh() {
        return consumption_in_wh;
    }

    /**
     * Setter for the consumption
     * @param consumption_in_wh - the consumption
     */
    public void setConsumptionInWh(int consumption_in_wh) {
        this.consumption_in_wh = consumption_in_wh;
    }

    /**
     * Compares if o is equal to this instance
     * @param o - the object we are comparing
     * @return if this is equal to o
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemplateActivity)) return false;
        TemplateActivity that = (TemplateActivity) o;
        return consumption_in_wh == that.consumption_in_wh && Objects.equals(title, that.title) && Objects.equals(source, that.source);
    }

    /**
     * @return Human-readable format of this Class
     */
    @Override
    public String toString() {
        return "TemplateActivity{" +
                "title='" + title + '\'' +
                ", consumption_in_wh=" + consumption_in_wh +
                ", source='" + source + '\'' +
                '}';
    }
}
