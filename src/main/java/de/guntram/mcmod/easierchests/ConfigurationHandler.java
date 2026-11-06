package de.guntram.mcmod.easierchests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigurationHandler {

    private static ConfigurationHandler instance;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private File configFile;

    // Configurable options
    private boolean extraLargeChests = false;
    private boolean halfSizeButtons = false;
    private boolean toneDownButtons = true;
    private boolean enableSearch = true;
    private boolean enableRowButtons = true;
    private boolean enableColumnButtons = true;
    private String matchHighlightColor = "4000ff00";
    private int clickDelayTicks = 1;

    public static ConfigurationHandler getInstance() {
        if (instance == null) {
            instance = new ConfigurationHandler();
        }
        return instance;
    }

    public void load(final File file) {
        this.configFile = file;
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigurationHandler loaded = GSON.fromJson(reader, ConfigurationHandler.class);
                if (loaded != null) {
                    this.extraLargeChests = loaded.extraLargeChests;
                    this.halfSizeButtons = loaded.halfSizeButtons;
                    this.toneDownButtons = loaded.toneDownButtons;
                    this.enableSearch = loaded.enableSearch;
                    this.enableRowButtons = loaded.enableRowButtons;
                    this.enableColumnButtons = loaded.enableColumnButtons;
                    if (loaded.matchHighlightColor != null) {
                        this.matchHighlightColor = loaded.matchHighlightColor;
                    }
                    if (loaded.clickDelayTicks >= 0) {
                        this.clickDelayTicks = loaded.clickDelayTicks;
                    }
                }
            } catch (IOException e) {
                System.err.println("[EasierChests] Could not read configuration file: " + e.getMessage());
            }
        } else {
            save();
        }
    }

    public void save() {
        if (configFile == null) {
            return;
        }
        try {
            File parent = configFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            System.err.println("[EasierChests] Could not save configuration file: " + e.getMessage());
        }
    }

    public static String getConfigFileName() {
        return getInstance().configFile != null ? getInstance().configFile.getPath() : "";
    }

    public static boolean allowExtraLargeChests() {
        return getInstance().extraLargeChests;
    }

    public static void setAllowExtraLargeChests(boolean value) {
        getInstance().extraLargeChests = value;
    }

    public static boolean toneDownButtons() {
        return getInstance().toneDownButtons;
    }

    public static void setToneDownButtons(boolean value) {
        getInstance().toneDownButtons = value;
    }

    public static boolean halfSizeButtons() {
        return getInstance().halfSizeButtons;
    }

    public static void setHalfSizeButtons(boolean value) {
        getInstance().halfSizeButtons = value;
    }

    public static boolean enableSearch() {
        return getInstance().enableSearch;
    }

    public static void setEnableSearch(boolean value) {
        getInstance().enableSearch = value;
    }

    public static boolean enableColumnButtons() {
        return getInstance().enableColumnButtons;
    }

    public static void setEnableColumnButtons(boolean value) {
        getInstance().enableColumnButtons = value;
    }

    public static boolean enableRowButtons() {
        return getInstance().enableRowButtons;
    }

    public static void setEnableRowButtons(boolean value) {
        getInstance().enableRowButtons = value;
    }

    public static void toggleSearchBox() {
        getInstance().enableSearch = !getInstance().enableSearch;
        getInstance().save();
    }

    public static String getHighlightColor() {
        return getInstance().matchHighlightColor;
    }

    public static void setHighlightColor(String value) {
        getInstance().matchHighlightColor = value;
    }

    public static int getClickDelayTicks() {
        return getInstance().clickDelayTicks;
    }

    public static void setClickDelayTicks(int value) {
        getInstance().clickDelayTicks = Math.max(0, value);
    }
}