package sk.upjs.ics.spendwise.ui.util;

import javafx.scene.Scene;

/**
 * Central place to manage runtime theme switching between bundled stylesheets.
 */
public final class ThemeManager {

    public enum Theme {
        DARK("/ui/style.css"),
        LIGHT("/ui/style-light.css");

        private final String stylesheet;

        Theme(String stylesheet) {
            this.stylesheet = stylesheet;
        }

        public String getStylesheet() {
            return stylesheet;
        }
    }

    private static Theme activeTheme = Theme.DARK;

    private ThemeManager() {
    }

    public static Theme getActiveTheme() {
        return activeTheme;
    }

    public static void toggleTheme() {
        activeTheme = activeTheme == Theme.DARK ? Theme.LIGHT : Theme.DARK;
        applyTheme(SceneSwitcher.getScene());
    }

    public static void setTheme(Theme theme) {
        activeTheme = theme;
        applyTheme(SceneSwitcher.getScene());
    }

    public static void applyTheme(Scene scene) {
        if (scene == null) {
            return;
        }

        String stylesheetUrl = ThemeManager.class.getResource(activeTheme.getStylesheet()).toExternalForm();
        scene.getStylesheets().setAll(stylesheetUrl);
    }
}
