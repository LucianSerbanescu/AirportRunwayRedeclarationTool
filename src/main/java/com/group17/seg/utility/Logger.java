package com.group17.seg.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class Logger {
    private static boolean disableLogging = false;
    private static final byte[] newLine = System.lineSeparator().getBytes();
    public static final String cwd = Path.of("").toAbsolutePath().toString();
    private static final String date = LocalDate.now().toString();
    public static final String logFileString = String.format("%s/airport-tool/logs/%s.airporttool", cwd, date);
    public static final Path logFilePath = Path.of(logFileString);
    public static final String exportViewFileName = String.format("%s/airport-tool/screenshots/%s", cwd, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")));
    public static final String exportedViewFolderPath = String.format("%s/airport-tool/", cwd);

    public static boolean isLogging() {
        return !Logger.disableLogging;
    }

    public static void disableLogging(boolean arg) {
        Logger.disableLogging = arg;
    }

    public static void writeToLogFile(String... args) {
        if (disableLogging) return;
        if (args.length == 0) return;
        try {
            var prepend = String.format("[%s] - ", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))).getBytes();
            Files.write(logFilePath, prepend, StandardOpenOption.APPEND);
            for (var string : args) {
                Files.write(logFilePath, string.getBytes(), StandardOpenOption.APPEND);
            }
            Files.write(logFilePath, newLine, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportCurrentView(List<String> args) {
        if (args.size() == 0) return;
        var path = Path.of(String.format("%s/airport-tool/calculations/%s.breakdown",
                                         Logger.cwd,
                                         LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))));
        try {
            Files.createFile(path);
            System.err.println("Created new current view file.");
        } catch (IOException e) {
            System.err.println("File exists already.");
        }
        try {
            for (var string : args) {
                Files.write(path, string.getBytes(), StandardOpenOption.APPEND);
                Files.write(path, newLine, StandardOpenOption.APPEND);
                Files.write(path, newLine, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

