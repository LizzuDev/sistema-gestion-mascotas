package gestionmascotas;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("No se pudo establecer el Look and Feel.");
            }
        }

        SwingUtilities.invokeLater(() -> {
            gestionmascotas.gui.VentanaLogin login = new gestionmascotas.gui.VentanaLogin();
            login.setVisible(true);
        });
    }
}