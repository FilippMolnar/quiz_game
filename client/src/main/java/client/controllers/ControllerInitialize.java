package client.controllers;

/**
 * Interface that client controllers can implement
 */
public interface ControllerInitialize {

    /**
     * Function called when the scene that belongs to the controller is set to be active
     */
    void initializeController();
}
