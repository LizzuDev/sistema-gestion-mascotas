package gestionmascotas.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class GestorMensajes {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    public static String get(String key, Object... args) {
        try {
            String pattern = bundle.getString(key);
            return MessageFormat.format(pattern, args);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }
}
