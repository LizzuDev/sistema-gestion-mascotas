package gestionmascotas.gui;

import gestionmascotas.dp.models.MascotaDP;
import gestionmascotas.md.MascotaMD;
import gestionmascotas.md.IMascotaMD;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel de Control Principal (VentanaDashboard).
 * Ofrece métricas y cumple con los requerimientos visuales del Ciclo 1.
 */
public class VentanaDashboard extends JPanel {
    private final IMascotaMD mascotaMD = new MascotaMD();

    private JLabel lblTotalMascotasVal;
    private JLabel lblTotalAdoptadasVal;
    private JLabel lblDisponiblesVal;
    private JTable tblRecientes;
    private DefaultTableModel modelRecientes;

    public VentanaDashboard() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(248, 250, 252)); // Slate 50
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título del Dashboard
        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setBackground(new Color(248, 250, 252));
        JLabel lblTitle = new JLabel("Panel de Control Metodológico (TSP)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42)); // Slate 900
        JLabel lblSubtitle = new JLabel("Resumen de indicadores clave y registro de mascotas en tiempo real.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 116, 139)); // Slate 500
        pnlTitle.add(lblTitle, BorderLayout.NORTH);
        pnlTitle.add(lblSubtitle, BorderLayout.SOUTH);

        add(pnlTitle, BorderLayout.NORTH);

        // Grid de Tarjetas de Estadísticas (3 Columnas)
        JPanel pnlStats = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlStats.setBackground(new Color(248, 250, 252));

        pnlStats.add(crearTarjetaStat("Mascotas Registradas", "0", new Color(16, 185, 129), 1)); // Emerald
        pnlStats.add(crearTarjetaStat("Adoptadas Exitosamente", "0", new Color(59, 130, 246), 2)); // Blue
        pnlStats.add(crearTarjetaStat("Disponibles para Adopción", "0", new Color(147, 51, 234), 3)); // Purple

        // Contenedor Central para la Tabla de Mascotas Recientes
        JPanel pnlReporte = new JPanel(new BorderLayout(10, 10));
        pnlReporte.setBackground(Color.WHITE);
        pnlReporte.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTblTitle = new JLabel("Mascotas Registradas Recientemente");
        lblTblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTblTitle.setForeground(new Color(15, 23, 42));
        pnlReporte.add(lblTblTitle, BorderLayout.NORTH);

        // Modelo de Tabla
        String[] columnas = {"ID Mascota", "Nombre", "Especie", "Edad (Años)", "Estado", "ID Centro"};
        modelRecientes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblRecientes = new JTable(modelRecientes);
        tblRecientes.setRowHeight(35);
        tblRecientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblRecientes.getTableHeader().setReorderingAllowed(false);
        tblRecientes.setShowGrid(false);
        tblRecientes.setIntercellSpacing(new Dimension(0, 0));

        // Estilizar cabecera
        JTableHeader header = tblRecientes.getTableHeader();
        header.setBackground(new Color(241, 245, 249)); // Slate 100
        header.setForeground(new Color(71, 85, 105)); // Slate 600
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(100, 30));

        // Renderizado personalizado para el estado
        tblRecientes.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = new JLabel((String) value);
                lbl.setOpaque(true);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

                String valStr = (String) value;
                if ("ADOPTADA".equals(valStr)) {
                    lbl.setBackground(new Color(209, 250, 229)); // Soft Green
                    lbl.setForeground(new Color(5, 150, 105)); // Dark Green
                } else if ("DISPONIBLE".equals(valStr)) {
                    lbl.setBackground(new Color(219, 234, 254)); // Soft Blue
                    lbl.setForeground(new Color(37, 99, 235)); // Dark Blue
                } else {
                    lbl.setBackground(new Color(254, 243, 199)); // Soft Yellow
                    lbl.setForeground(new Color(217, 119, 6)); // Dark Yellow
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tblRecientes);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        pnlReporte.add(scroll, BorderLayout.CENTER);

        // Añadir al panel central
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 20));
        pnlCenter.setBackground(new Color(248, 250, 252));
        pnlCenter.add(pnlStats, BorderLayout.NORTH);
        pnlCenter.add(pnlReporte, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // Cargar datos
        actualizarDatos();
    }

    private JPanel crearTarjetaStat(String titulo, String valorDefecto, Color colorBorde, int tipoCard) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorBorde);
                g2.setStroke(new BasicStroke(4));
                g2.drawLine(0, 0, 0, getHeight());
                g2.dispose();
            }
        };
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(100, 116, 139));

        JLabel lblVal = new JLabel(valorDefecto);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblVal.setForeground(new Color(15, 23, 42));

        if (tipoCard == 1) lblTotalMascotasVal = lblVal;
        else if (tipoCard == 2) lblTotalAdoptadasVal = lblVal;
        else if (tipoCard == 3) lblDisponiblesVal = lblVal;

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);

        return card;
    }

    public void actualizarDatos() {
        try {
            // 1. Obtener datos
            List<MascotaDP> mascotas = mascotaMD.obtenerTodas();
            int totalMascotas = mascotas.size();
            long totalAdoptadas = mascotas.stream().filter(m -> "ADOPTADA".equals(m.getEstado())).count();
            long disponibles = mascotas.stream().filter(m -> "DISPONIBLE".equals(m.getEstado())).count();

            // 2. Poblar tabla con últimas registradas
            modelRecientes.setRowCount(0);
            int count = 0;
            for (MascotaDP m : mascotas) {
                if (count >= 10) break;
                modelRecientes.addRow(new Object[]{
                    m.getIdMascota(),
                    m.getNombre(),
                    m.getEspecie(),
                    m.getEdad(),
                    m.getEstado(),
                    m.getIdCentro()
                });
                count++;
            }

            // 3. Setear labels
            lblTotalMascotasVal.setText(String.valueOf(totalMascotas));
            lblTotalAdoptadasVal.setText(String.valueOf(totalAdoptadas));
            lblDisponiblesVal.setText(String.valueOf(disponibles));

        } catch (SQLException e) {
            System.err.println("Error al cargar datos del Dashboard: " + e.getMessage());
        }
    }
}
