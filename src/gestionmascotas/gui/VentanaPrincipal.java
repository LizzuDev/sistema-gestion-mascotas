package gestionmascotas.gui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import gestionmascotas.dp.controladores.SesionUsuario;
import gestionmascotas.md.ConexionBD;

public class VentanaPrincipal extends JFrame {

    private VentanaMascota pnlMascotas;
    private VentanaAdoptante pnlAdoptantes;
    private VentanaVeterinario pnlVeterinarios;
    private VentanaPersonal pnlPersonal;
    private JTabbedPane tabbedPane;
    private JLabel lblEstadoBD;

    public VentanaPrincipal() {
        setTitle("Sistema de Gestión de Adopciones (Multi-sede)");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                salir();
            }
        });
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(new Color(30, 41, 59));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblHeader = new JLabel(" Sistema de Gestión de Adopciones", JLabel.LEFT);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        pnlTop.add(lblHeader, BorderLayout.CENTER);
        
        JButton btnSalir = new JButton("Salir");
        btnSalir.setBackground(new Color(220, 38, 38));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalir.addActionListener(e -> salir());
        pnlTop.add(btnSalir, BorderLayout.EAST);
        
        lblEstadoBD = new JLabel("Verificando conexión...");
        lblEstadoBD.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstadoBD.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(lblEstadoBD, BorderLayout.SOUTH);
        
        verificarConexionBD();

        add(pnlTop, BorderLayout.NORTH);

        String cargo = SesionUsuario.getUsuarioActual().getCargo();
        
        if ("VETERINARIO_JEFE".equals(cargo)) {
            JPanel pnlMensaje = new JPanel(new BorderLayout());
            pnlMensaje.setBackground(new Color(248, 250, 252));
            JLabel lblMensaje = new JLabel("Módulo de Veterinarios en construcción (Acceso Restringido)", SwingConstants.CENTER);
            lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 24));
            pnlMensaje.add(lblMensaje, BorderLayout.CENTER);
            add(pnlMensaje, BorderLayout.CENTER);
        } else {
            tabbedPane = new JTabbedPane();

            Runnable onDataChanged = () -> {
                pnlMascotas.recargarTabla();
                pnlAdoptantes.recargarTabla();
                pnlVeterinarios.recargarTabla();
                pnlPersonal.recargarTabla();
            };

            pnlMascotas = new VentanaMascota(onDataChanged);
            pnlAdoptantes = new VentanaAdoptante(onDataChanged);
            pnlVeterinarios = new VentanaVeterinario(onDataChanged);
            pnlPersonal = new VentanaPersonal(onDataChanged);

            tabbedPane.addTab("Mascotas", pnlMascotas);
            tabbedPane.addTab("Adoptantes", pnlAdoptantes);
            tabbedPane.addTab("Veterinarios", pnlVeterinarios);
            tabbedPane.addTab("Personal", pnlPersonal);

            add(tabbedPane, BorderLayout.CENTER);
        }
    }

    private void verificarConexionBD() {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            lblEstadoBD.setText("Conectado: " + ConexionBD.getUrl() + " | Usuario: " + ConexionBD.getUser());
            lblEstadoBD.setForeground(new Color(16, 185, 129));
        } catch (Exception e) {
            lblEstadoBD.setText("Desconectado: Falla en la conexión a la Base de Datos");
            lblEstadoBD.setForeground(Color.RED);
        }
    }

    private void salir() {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea salir del sistema?", "Confirmar Salida", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
