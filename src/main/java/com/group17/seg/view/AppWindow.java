package com.group17.seg.view;


import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * The window class defines the main window of the application.
 * <p>
 * This is where all the UI actions and interactions take place.
 */

public class AppWindow {
    /**
     * Width/Height of the window
     */
    public final int width, height;

    /**
     * Stage of the window
     */
    private final Stage stage;

    /**
     * The current UI that is shown in the window
     */
    private Scene currentScene;

    /**
     * Creates a window attached to the given stage
     * with the specified width and height.
     */
    public AppWindow(Stage stage, int width, int height) {
        this.stage = stage;
        this.width = width;
        this.height = height;

        // Setup window
        this.stage.setTitle("Runway Declaration Tool");
        this.stage.setMinWidth(width);
        this.stage.setMinHeight(height);
        this.stage.setAlwaysOnTop(false);

//        try {
//            System.setProperty("prism.lcdtext", "false");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Load font
        Font.loadFont(getClass().getResourceAsStream("/USN_Stencil.ttf"), 32);
        Font.loadFont(getClass().getResourceAsStream("/Inconsolata.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/JetBrainsMono.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/InputMono.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/Informe.ttf"), 10);

        // Load default scene
//        this.currentScene = new Scene(new Pane(), width, height);
        this.currentScene = new Scene(new StackPane(), width, height);
    }

    /**
     * Loads a given UI into the window
     *
     * @param newUI new scene to load
     */
    public void loadUI(BaseUI newUI) {
        newUI.build();
        this.currentScene = newUI.initialize();
        this.stage.setScene(this.currentScene);
    }

    /**
     * Returns the size of the current scene in display as an array
     *
     * @return width and height in this format: {width, height}
     */
    public double[] getSceneSize() {
        return new double[]{
                this.currentScene.getWidth(),
                this.currentScene.getHeight(),
        };
    }

    public Stage getPrimaryStage() {
        return stage;
    }
}