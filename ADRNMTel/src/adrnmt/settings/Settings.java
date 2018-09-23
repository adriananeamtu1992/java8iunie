/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adrnmt.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.*;
import adrnmt.logger.Logger;
import adrnmt.logger.Loggers;

/**
 *
 * @author Adriana
 */
public class Settings {
    
    public static final JFileChooser FILECHOOSER = new JFileChooser();
    public static final String DEFAULT_DIR_PATH = FILECHOOSER.getFileSystemView().getDefaultDirectory().toString();
    private static final String BUNDLE_NAME = "Messages";
    private static final Logger LOGGER = Loggers.getDefaultLogger();
    private static final Properties PROPERTIES = new Properties();
    private static final Settings SINGLETON_SETTINGS = new Settings();
    private ResourceBundle resourceBundle;
    private String dirPath = null;
    private String propFileName = null;
    private String lastUsedFile = null;
    private Locale currentLocale = Locale.getDefault();
    private String lastFileChooserPath;
    private boolean appRegistered = false;

    public static final String DATA_NASTERII_FORMAT = "dd.MM.yyyy";
    
    private Settings() {
        dirPath = DEFAULT_DIR_PATH + File.separator + "8_iunie_adriana_neamtu_app";
        propFileName = dirPath + File.separator + "adrnmtel.properties";
        lastFileChooserPath = dirPath;
        makeSettingsDir();
        loadProperties();

        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
        } catch (Exception e) {
            LOGGER.message(e);
        }

    }

    public static String[] getAvailableLanguages() {
        return new String[] { Locale.ENGLISH.getLanguage(), "ro" };
    }

    public static Settings getInstance() {
        return SINGLETON_SETTINGS;
    }

    public Locale getCurrentLocale() {
        if (PROPERTIES.containsKey("locale.language")) {
            currentLocale = new Locale(PROPERTIES.getProperty("locale.language"));
        }
        return currentLocale;
    }

    public void setCurrentLocale(String language) {
        PROPERTIES.put("locale.language", language);
        saveSettings();
        this.currentLocale = new Locale(language);
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }

    public String getLastUsedFile() {
        if (PROPERTIES.containsKey("last.used.file")) {
            return PROPERTIES.getProperty("last.used.file");
        }
        return lastUsedFile;
    }

    public void setLastUsedFile(String lastUsedFile) {
        PROPERTIES.setProperty("last.used.file", lastUsedFile);
        this.lastUsedFile = lastUsedFile;
        saveSettings();
    }

    public String getTempDbBackup() {
        return dirPath + File.separator + "_tempback.adrnmt";
    }

    public boolean isAppRegistered() {
        if (PROPERTIES.containsKey("app.registered")) {
            appRegistered = (PROPERTIES.getProperty("app.registered")).equals("true");
        }
        return appRegistered;
    }

    public void setAppRegistered(boolean appRegistered) {
        this.appRegistered = appRegistered;
        PROPERTIES.put("app.registered", "true");
        saveSettings();
    }

    public String getLastFileChooserPath() {
        if (PROPERTIES.containsKey("last.file.chooser.path")) {
            return PROPERTIES.getProperty("last.file.chooser.path");
        }
        return lastFileChooserPath;
    }

    public void setLastFileChooserPath(String lastFileChooserPath) {
        this.lastFileChooserPath = lastFileChooserPath;
        PROPERTIES.setProperty("last.file.chooser.path", lastFileChooserPath);
        saveSettings();
    }

    /**
     * creeaza directorul de lucru, unde se vor salva setarile si fisierul de backuo
     */
    private void makeSettingsDir() {
        File settingDir = new File(dirPath);
        if (!settingDir.exists()) {
            if (settingDir.mkdir()) {
                LOGGER.message(dirPath + " folder created");
            }
            else {
                LOGGER.message(dirPath + " folder failed to create");
            }
        }
        else {
            LOGGER.message(dirPath + " folder already exist");
        }
    }

    private void loadProperties() {
        try {
            File file = new File(propFileName);
            if (file.exists()) {
                PROPERTIES.load(new FileInputStream(propFileName));
                LOGGER.message("props loaded " + isAppRegistered());
                getCurrentLocale();
            } else {
                LOGGER.message(" file " + propFileName + " does not exist");
            }

        } catch (IOException ex) {
            LOGGER.message("load props failed", ex);
        } catch (Exception ex) {
            LOGGER.message("load props failed", ex);
        }
    }

    public void saveSettings() {
        try {
            File file = new File(propFileName);
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                PROPERTIES.store(fileOut, propFileName);
            }
        } catch (FileNotFoundException e) {
            LOGGER.message("save settings failed", e);
        } catch (IOException e) {
            LOGGER.message("save settings failed", e);
        }
    }

    public String getBundle(String propName) {
        return resourceBundle.getString(propName);
    }
}
