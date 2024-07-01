package client;

import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;

public class LinkedScene {

    private final Scene current;
    private List<LinkedScene> next;
    private final String title;
    private Object controller;

    /**
     * Constructor for a LinkedScene
     * @param current - the current scene
     */
    public LinkedScene(Scene current) {
        this.current = current;
        this.title = null;
        this.next = new ArrayList<>();
    }

    /**
     * Constructor for a LinkedScene
     * @param current - the current scene
     */
    public LinkedScene(Scene current, Object controller) {
        this.current = current;
        this.title = null;
        this.next = new ArrayList<>();
        this.controller = controller;
    }

    /**
     * Constructor for a LinkedScene
     * @param current - the current scene
     */
    public LinkedScene(Scene current, ArrayList<LinkedScene> next) {
        this.title = null;
        this.current = current;
        this.next = next;
    }

    /**
     * Constructor for a LinkedScene
     * @param current - the current scene
     */
    public LinkedScene(Scene current, String title, List<LinkedScene> next) {
        this.title = title;
        this.current = current;
        this.next = next;
    }

    /**
     * Getter for the controller
     * @return the controller
     */
    public Object getController() {
        return controller;
    }

    /**
     * Getter for the next scene
     * @return the next scene
     */
    public LinkedScene getNext() {
        return this.next.get(0);
    }

    /**
     * Getter for the next scene
     * @param i - the index for the scene we want
     * @return the next scene
     */
    public LinkedScene getNext(int i) {
        return this.next.get(i);
    }

    /**
     * Reset the LinkedScenes
     * @param i - index to reset at
     */
    public void reset(int i) {
        if(next.size() > i) {
            next.get(i).next = new ArrayList(); 
        }
    }

    /**
     * Adds next LinkedScene
     * @param next - the scene to add
     */
    public void addNext(LinkedScene next) {
        this.next.add(next);
    }

    /**
     * Getter for the current scene
     * @return the current scene
     */
    public Scene getScene() {
        return this.current;
    }

    /**
     * Getter for the title of the current scene
     * @return the title of the current scene
     */
    public String getTitle() {
        return this.title;
    }

}
