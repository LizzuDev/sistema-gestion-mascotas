package gestionmascotas;

import gestionmascotas.gui.VentanaPrincipal;

import javax.swing.*;

/**
 * Punto de entrada de la aplicación.
 * Inicializa el tema Nimbus Look and Feel y abre la ventana principal.
 */
public class Main {
    public static void main(String[] args) {
        // Configurar un Look and Feel agradable (Nimbus)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fallback al sistema nativo si Nimbus no está disponible
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("No se pudo establecer el Look and Feel.");
            }
        }

        // Ejecutar en el hilo de despacho de eventos de Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal frame = new VentanaPrincipal();
            frame.setVisible(true);
        });
    }
}
