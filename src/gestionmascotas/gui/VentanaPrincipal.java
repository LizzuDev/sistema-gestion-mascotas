package gestionmascotas.gui;

import gestionmascotas.dp.controllers.SesionUsuario;
import gestionmascotas.dp.models.PersonalDP;
import gestionmascotas.md.ConexionBD;
import gestionmascotas.md.IPersonalMD;
import gestionmascotas.md.PersonalMD;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana Principal del Sistema de Gestión de Mascotas.
 * Sigue la Arquitectura de Tres Capas (Capa GUI).
 * Implementa la paleta de diseño moderna (Slate & Mint),
 * el control de sesiones en tiempo real (STD-05) y el banner de estado de la base de datos (STD-09).
 */
public class VentanaPrincipal extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pnlContent = new JPanel(cardLayout);

    private VentanaDashboard pnlDashboard;
    private VentanaMascota pnlMascotas;
    private VentanaAdoptante pnlAdoptantes;
    private VentanaPersonal pnlPersonal;
    private VentanaVeterinario pnlVeterinarios;

    private JComboBox<PersonalDP> cmbUsuarioSesion;
    private JLabel lblEstadoBD;
    private JLabel lblRolBadge;

    private final List<JButton> sidebarButtons = new ArrayList<>();
    private String activePanelName = "DASHBOARD";

    public VentanaPrincipal() {
        setTitle("Sistema de Gestión de Adopciones - TSP Grupo 4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Panel Lateral (Sidebar)
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setBackground(new Color(30, 41, 59)); // Slate 800
        pnlSidebar.setPreferredSize(new Dimension(240, 0));
        pnlSidebar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Título de la Aplicación en el Sidebar
        JLabel lblAppTitle = new JLabel("PAE REFUGIOS");
        lblAppTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAppTitle.setForeground(new Color(16, 185, 129)); // Mint Green
        lblAppTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAppSub = new JLabel("Metodología TSPi - Grupo 4");
        lblAppSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblAppSub.setForeground(new Color(148, 163, 184)); // Slate 400
        lblAppSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlSidebar.add(lblAppTitle);
        pnlSidebar.add(lblAppSub);
        pnlSidebar.add(Box.createVerticalStrut(25));

        // Botones de Navegación del Sidebar
        agregarBotonSidebar(pnlSidebar, "Dashboard", "DASHBOARD");
        pnlSidebar.add(Box.createVerticalStrut(15)); // Agrupación visual

        JLabel lblCat = new JLabel("CATÁLOGOS (Ciclo 1)");
        lblCat.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblCat.setForeground(new Color(100, 116, 139));
        lblCat.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlSidebar.add(lblCat);
        pnlSidebar.add(Box.createVerticalStrut(5));

        agregarBotonSidebar(pnlSidebar, "Mascotas (F1)", "MASCOTA");
        agregarBotonSidebar(pnlSidebar, "Adoptantes (F2)", "ADOPTANTE");
        agregarBotonSidebar(pnlSidebar, "Veterinarios (F5)", "VETERINARIO");
        agregarBotonSidebar(pnlSidebar, "Personal (F7)", "PERSONAL");
        
        pnlSidebar.add(Box.createVerticalGlue());

        add(pnlSidebar, BorderLayout.WEST);

        // 2. Cabecera Superior (Header / Topbar)
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        pnlHeader.setPreferredSize(new Dimension(0, 65));
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            pnlHeader.getBorder(),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Título del Sistema
        JLabel lblSysTitle = new JLabel("Sistema de Gestión de Adopciones PAE");
        lblSysTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSysTitle.setForeground(new Color(15, 23, 42));
        pnlHeader.add(lblSysTitle, BorderLayout.WEST);

        // Control de Sesión (Simulador de Integrante / Rol)
        JPanel pnlSession = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSession.setBackground(Color.WHITE);

        pnlSession.add(new JLabel("Simular Integrante (Rol):"));
        
        cmbUsuarioSesion = new JComboBox<>();
        cmbUsuarioSesion.setPreferredSize(new Dimension(200, 30));
        pnlSession.add(cmbUsuarioSesion);

        lblRolBadge = new JLabel("LIDER");
        lblRolBadge.setOpaque(true);
        lblRolBadge.setBackground(new Color(209, 250, 229)); // Soft Green
        lblRolBadge.setForeground(new Color(5, 150, 105)); // Dark Green
        lblRolBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRolBadge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        pnlSession.add(lblRolBadge);

        pnlHeader.add(pnlSession, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // 3. Área de Contenido Central (CardLayout)
        Runnable actualizadorDashboard = () -> {
            if (pnlDashboard != null) pnlDashboard.actualizarDatos();
        };

        pnlDashboard = new VentanaDashboard();
        pnlMascotas = new VentanaMascota(actualizadorDashboard);
        pnlAdoptantes = new VentanaAdoptante(actualizadorDashboard);
        pnlVeterinarios = new VentanaVeterinario(actualizadorDashboard);
        pnlPersonal = new VentanaPersonal(actualizadorDashboard);

        pnlContent.add(pnlDashboard, "DASHBOARD");
        pnlContent.add(pnlMascotas, "MASCOTA");
        pnlContent.add(pnlAdoptantes, "ADOPTANTE");
        pnlContent.add(pnlVeterinarios, "VETERINARIO");
        pnlContent.add(pnlPersonal, "PERSONAL");

        add(pnlContent, BorderLayout.CENTER);

        // 4. Barra de Estado Inferior (Status Bar / Banner BD)
        JPanel pnlStatus = new JPanel(new BorderLayout());
        pnlStatus.setBackground(new Color(241, 245, 249));
        pnlStatus.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

        lblEstadoBD = new JLabel("Verificando conexión de base de datos...");
        lblEstadoBD.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pnlStatus.add(lblEstadoBD, BorderLayout.WEST);

        JLabel lblCopyright = new JLabel("Ingeniería de Software 2026");
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCopyright.setForeground(new Color(100, 116, 139));
        pnlStatus.add(lblCopyright, BorderLayout.EAST);

        add(pnlStatus, BorderLayout.SOUTH);

        // Cargar usuarios de personal e inicializar eventos
        cargarUsuariosSesion();
        verificarConexionBD();

        cmbUsuarioSesion.addActionListener(e -> cambiarUsuarioSesion());

        // Marcar botón activo inicial
        actualizarBotonActivo("DASHBOARD");
    }

    private void agregarBotonSidebar(JPanel container, String text, final String panelName) {
        final JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(new Color(203, 213, 225)); // Slate 300
        btn.setBackground(new Color(30, 41, 59));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            activePanelName = panelName;
            cardLayout.show(pnlContent, panelName);
            actualizarBotonActivo(panelName);
            
            if ("MASCOTA".equals(panelName)) pnlMascotas.recargarTabla();
            if ("ADOPTANTE".equals(panelName)) pnlAdoptantes.recargarTabla();
            if ("VETERINARIO".equals(panelName)) pnlVeterinarios.recargarTabla();
            if ("PERSONAL".equals(panelName)) pnlPersonal.recargarTabla();
            if ("DASHBOARD".equals(panelName)) pnlDashboard.actualizarDatos();
        });

        container.add(btn);
        sidebarButtons.add(btn);
    }

    private void actualizarBotonActivo(String panelName) {
        for (JButton btn : sidebarButtons) {
            btn.setBackground(new Color(30, 41, 59));
            btn.setForeground(new Color(203, 213, 225));
        }

        // Buscar botón correspondiente
        for (JButton btn : sidebarButtons) {
            String text = btn.getText();
            if ("DASHBOARD".equals(panelName) && text.equals("Dashboard")) {
                marcarActivo(btn);
            } else if ("MASCOTA".equals(panelName) && text.contains("Mascotas")) {
                marcarActivo(btn);
            } else if ("ADOPTANTE".equals(panelName) && text.contains("Adoptantes")) {
                marcarActivo(btn);
            } else if ("VETERINARIO".equals(panelName) && text.contains("Veterinarios")) {
                marcarActivo(btn);
            } else if ("PERSONAL".equals(panelName) && text.contains("Personal")) {
                marcarActivo(btn);
            }
        }
    }

    private void marcarActivo(JButton btn) {
        btn.setBackground(new Color(16, 185, 129)); // Mint Green
        btn.setForeground(Color.WHITE);
    }

    private void cargarUsuariosSesion() {
        try {
            IPersonalMD md = new PersonalMD();
            List<PersonalDP> personal = md.obtenerTodos();
            
            cmbUsuarioSesion.removeAllItems();
            for (PersonalDP p : personal) {
                cmbUsuarioSesion.addItem(p);
            }
            if (cmbUsuarioSesion.getItemCount() > 0) {
                cmbUsuarioSesion.setSelectedIndex(0);
                cambiarUsuarioSesion();
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar personal de BD para sesión: " + e.getMessage());
            cmbUsuarioSesion.removeAllItems();
            cmbUsuarioSesion.addItem(new PersonalDP(1, "Paúl Rosero", "1712345678", "LIDER", "ACTIVO"));
            cmbUsuarioSesion.addItem(new PersonalDP(2, "Gabriel Aguinaga", "1787654321", "PLANIFICACION_CALIDAD", "ACTIVO"));
            cmbUsuarioSesion.addItem(new PersonalDP(3, "Joselyn Cadena", "1755554444", "DESARROLLO", "ACTIVO"));
            cmbUsuarioSesion.setSelectedIndex(0);
            cambiarUsuarioSesion();
        }
    }

    private void cambiarUsuarioSesion() {
        PersonalDP seleccionado = cmbUsuarioSesion.getSelectedItem() instanceof PersonalDP ? (PersonalDP) cmbUsuarioSesion.getSelectedItem() : null;
        if (seleccionado != null) {
            SesionUsuario.setUsuarioActual(seleccionado);
            
            // Actualizar Badge
            lblRolBadge.setText(seleccionado.getRol());
            if ("LIDER".equals(seleccionado.getRol())) {
                lblRolBadge.setBackground(new Color(219, 234, 254)); // Soft Blue
                lblRolBadge.setForeground(new Color(37, 99, 235)); // Dark Blue
            } else if ("PLANIFICACION_CALIDAD".equals(seleccionado.getRol())) {
                lblRolBadge.setBackground(new Color(254, 243, 199)); // Soft Yellow
                lblRolBadge.setForeground(new Color(217, 119, 6)); // Dark Yellow
            } else if ("DESARROLLO".equals(seleccionado.getRol())) {
                lblRolBadge.setBackground(new Color(243, 232, 255)); // Soft Purple
                lblRolBadge.setForeground(new Color(147, 51, 234)); // Dark Purple
            } else {
                lblRolBadge.setBackground(new Color(241, 245, 249)); // Slate
                lblRolBadge.setForeground(new Color(71, 85, 105));
            }

            // Aplicar los permisos visuales en cascada para todos los paneles (STD-05)
            pnlMascotas.aplicarPermisosVisuales();
            pnlAdoptantes.aplicarPermisosVisuales();
            pnlVeterinarios.aplicarPermisosVisuales();
            pnlPersonal.aplicarPermisosVisuales();
        }
    }

    private void verificarConexionBD() {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            lblEstadoBD.setText("Conexión BD: Conectado a " + ConexionBD.getUrl() + " (Usuario: " + ConexionBD.getUser() + ")");
            lblEstadoBD.setForeground(new Color(5, 150, 105)); // Green
        } catch (SQLException e) {
            lblEstadoBD.setText("Conexión BD: DESCONECTADO (Modo Offline Simulador habilitado - STD-09)");
            lblEstadoBD.setForeground(new Color(220, 38, 38)); // Red
            
            JOptionPane.showMessageDialog(this, 
                "No se pudo conectar a la base de datos PostgreSQL.\n" +
                "El sistema entrará en modo de contingencia (STD-09).\n" +
                "Verifique que PostgreSQL esté corriendo en localhost:5432 y que exista la BD 'sistema_mascotas'.\n" +
                "Detalle: " + e.getMessage(), 
                "Conexión Fallida", JOptionPane.WARNING_MESSAGE);
        }
    }
}
