package com.group17.seg;

import com.group17.seg.utility.Grayscale;
import com.group17.seg.utility.Logger;
import com.group17.seg.view.AppWindow;
import com.group17.seg.view.selectairport.SelectAirportView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static com.group17.seg.utility.Logger.cwd;


public class App extends Application {
    /**
     * The stage that is used for the app
     */
    private Stage stage;

    /**
     * Loads window into the stage
     */
    public void loadWindow() {
        var window = new AppWindow(this.stage, 1050, 650);
        window.loadUI(new SelectAirportView(window));
        this.stage.show();
    }

    private void loadSettings() {
        var settingsFilePath = Path.of(cwd + "/airport-tool/settings.properties");
        try {
            Files.createFile(settingsFilePath);
        } catch (IOException ignored) {
        }
        Properties properties = new Properties(2);
        try {
            properties.load(new FileInputStream(settingsFilePath.toFile()));
            var logging = Boolean.parseBoolean(properties.getProperty("logging", String.valueOf(false)));
            var grayscale = Boolean.parseBoolean(properties.getProperty("grayscale", String.valueOf(false)));
            Logger.disableLogging(!logging);
            Grayscale.setGrayscale(grayscale);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.loadSettings();
        this.loadWindow();
    }

    public static void main(String[] args) {
        var cwd = Path.of("").toAbsolutePath().toString();
        var folder = Path.of(cwd + "/airport-tool/");
        if (!Files.isDirectory(folder)) {
            try {
                Files.createDirectory(folder);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        var logFolder = Path.of(cwd + "/airport-tool/logs/");
        if (!Files.isDirectory(logFolder)) {
            try {
                Files.createDirectory(logFolder);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        var calculationsFolder = Path.of(cwd + "/airport-tool/calculations/");
        if (!Files.isDirectory(calculationsFolder)) {
            try {
                Files.createDirectory(calculationsFolder);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        var screenshotsFolder = Path.of(cwd + "/airport-tool/screenshots/");
        if (!Files.isDirectory(screenshotsFolder)) {
            try {
                Files.createDirectory(screenshotsFolder);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        // Check for the log file, if it doesn't exist, create one.
        try {
            Files.createFile(Logger.logFilePath);
            System.err.println("Created new log file.");
        } catch (IOException e) {
            System.err.println("Log file exists already.");
        }

        Logger.writeToLogFile("Airport Tool Launched");
        Logger.disableLogging(true);

        launch(args);
    }
}



