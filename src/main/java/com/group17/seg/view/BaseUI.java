package com.group17.seg.view;


import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public abstract class BaseUI {
    /**
     * Default window the UI is loaded into
     */
    public final AppWindow window;
    /**
     * The scene on the Screen that is going to be loaded
     */
    protected Scene scene;

    /**
     * Root element of the UI (is Pane type atm, but can be changed
     * later on).
     */
    protected Pane root;

    /**
     * Set up the default window
     */
    public BaseUI(AppWindow window) {
        this.window = window;
        this.root = new StackPane();
    }

    /**
     * Set up new scene using the root contained within this scene.
     * New scene must be the same size as the previous one.
     *
     * @return the new scene that has been just set up
     */
    public Scene initialize() {
        double[] size = this.window.getSceneSize();
        this.scene = new Scene(this.root, size[0], size[1]);
        this.scene.getStylesheets().add("styles.css");
        return this.scene;
    }

    /**
     * Loads a given scene if something happens (i.e. a button is clicked, etc.)
     *
     * @param <T> is the class name of the next UI (i.e. TestUI.class)
     */
    public <T extends BaseUI> void nextUI(Class<T> ui) {
        try {
            this.window.loadUI(ui.getDeclaredConstructor(AppWindow.class).newInstance(this.window));
        } catch (Exception e) {
            System.out.println("Cannot load new scene");
            e.printStackTrace();
        }
    }

    /**
     * Loads a given scene if something happens (i.e. a button is clicked, etc.)
     *
     * @param ui is the next scene
     */
    public void nextUI(BaseUI ui) {
        this.window.loadUI(ui);
    }

    /**
     * Build the layout of the scene
     */
    public abstract void build();

    public Stage getPrimaryStage() {
        return window.getPrimaryStage();
    }
}