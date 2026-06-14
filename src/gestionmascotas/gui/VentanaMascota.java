package gestionmascotas.gui;

import gestionmascotas.dp.controladores.SesionUsuario;
import gestionmascotas.dp.modelos.MascotaDP;
import gestionmascotas.util.GestorMensajes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import com.toedter.calendar.JDateChooser;

public class VentanaMascota extends JPanel {

    private JTable tblMascotas;
    private DefaultTableModel modelMascotas;

    private JTextField txtNombre;
    private JComboBox<String> cmbEspecie;
    private JDateChooser dcFechaNacimiento;
    private JTextField txtRaza;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbCentroId;

    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private int selectedMascotaId = -1;
    private final Runnable onDataChanged;

    public VentanaMascota(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel pnlHeader = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Gestión de Mascotas");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new GridLayout(0, 4, 10, 10));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(); 
        txtNombre.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isLetter(e.getKeyChar()) && !Character.isWhitespace(e.getKeyChar()) || txtNombre.getText().length() >= 50) e.consume(); } });
        pnlForm.add(txtNombre);

        pnlForm.add(new JLabel("Especie:"));
        cmbEspecie = new JComboBox<>(new String[]{"PERRO", "GATO", "OTRO"});
        pnlForm.add(cmbEspecie);

        pnlForm.add(new JLabel("Fecha Nacimiento:"));
        dcFechaNacimiento = new JDateChooser();
        dcFechaNacimiento.setDateFormatString("yyyy-MM-dd");
        dcFechaNacimiento.setMaxSelectableDate(new java.util.Date()); // No puede nacer en el futuro
        pnlForm.add(dcFechaNacimiento);

        pnlForm.add(new JLabel("Raza:"));
        txtRaza = new JTextField(); 
        txtRaza.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isLetter(e.getKeyChar()) && !Character.isWhitespace(e.getKeyChar()) || txtRaza.getText().length() >= 50) e.consume(); } });
        pnlForm.add(txtRaza);

        pnlForm.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(new String[]{"DISPONIBLE", "ADOPTADA", "EN_TRATAMIENTO"});
        pnlForm.add(cmbEstado);

        pnlForm.add(new JLabel("Centro:"));
        cmbCentroId = new JComboBox<>(new String[]{"1 - Sede Quito Norte", "2 - Sede Cumbayá", "3 - Sede Quito Sur", "4 - Sede Lleno Total"});
        pnlForm.add(cmbCentroId);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnRegistrar = new JButton("Registrar"); btnModificar = new JButton("Modificar"); btnEliminar = new JButton("Eliminar"); btnLimpiar = new JButton("Limpiar");
        btnModificar.setEnabled(false); btnEliminar.setEnabled(false);
        if ("VOLUNTARIO".equals(gestionmascotas.dp.controladores.SesionUsuario.getUsuarioActual().getCargo())) btnEliminar.setVisible(false);
        pnlButtons.add(btnRegistrar); pnlButtons.add(btnModificar); pnlButtons.add(btnEliminar); pnlButtons.add(btnLimpiar);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlForm, BorderLayout.CENTER);
        pnlTop.add(pnlButtons, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        JPanel pnlTable = new JPanel(new BorderLayout());
        String[] columnas = {"ID", "Nombre", "Especie", "F. Nacimiento", "Edad", "Raza", "Estado", "Centro"};
        modelMascotas = new DefaultTableModel(columnas, 0) { public boolean isCellEditable(int row, int column) { return false; } };
        tblMascotas = new JTable(modelMascotas);
        tblMascotas.getColumnModel().getColumn(0).setMaxWidth(30);
        tblMascotas.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblMascotas.getColumnModel().getColumn(4).setMaxWidth(60);
        pnlTable.add(new JScrollPane(tblMascotas), BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);

        btnRegistrar.addActionListener(e -> registrar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tblMascotas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblMascotas.getSelectedRow() != -1) cargarSeleccionado();
        });

        recargarTabla();
    }

    public void recargarTabla() {
        try {
            modelMascotas.setRowCount(0);
            for (MascotaDP m : new MascotaDP().consultarDP()) {
                String centroName = "Desconocido";
                for (int i = 0; i < cmbCentroId.getItemCount(); i++) {
                    if (cmbCentroId.getItemAt(i).startsWith(m.getIdCentro() + " - ")) {
                        centroName = cmbCentroId.getItemAt(i).split(" - ")[1];
                        break;
                    }
                }
                String edadStr = "";
                if (m.getFechaNacimiento() != null) {
                    java.time.LocalDate fn = m.getFechaNacimiento().toLocalDate();
                    java.time.Period period = java.time.Period.between(fn, java.time.LocalDate.now());
                    if (period.getYears() > 0) {
                        edadStr = period.getYears() + (period.getYears() == 1 ? " año" : " años");
                    } else if (period.getMonths() > 0) {
                        edadStr = period.getMonths() + (period.getMonths() == 1 ? " mes" : " meses");
                    } else {
                        edadStr = period.getDays() + (period.getDays() == 1 ? " día" : " días");
                    }
                }
                modelMascotas.addRow(new Object[]{m.getIdMascota(), m.getNombre(), m.getEspecie(), m.getFechaNacimiento().toString(), edadStr, m.getRaza(), m.getEstadoAdopcion(), centroName});
            }
        } catch (SQLException e) { manejarError(e); }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() || txtRaza.getText().trim().isEmpty() || dcFechaNacimiento.getDate() == null) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.incomplete.msg"), GestorMensajes.get("val.incomplete.title"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!txtNombre.getText().trim().matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ ]{2,50}$")) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.nombre.mascota"), GestorMensajes.get("val.error.title"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!txtRaza.getText().trim().matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ ]{2,50}$")) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.raza"), GestorMensajes.get("val.error.title"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private String fmt(String t) {
        String s = java.text.Normalizer.normalize(t.trim(), java.text.Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
        StringBuilder r = new StringBuilder();
        for (String p : s.split(" ")) if (!p.isEmpty()) r.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(" ");
        return r.toString().trim();
    }

    private void manejarError(SQLException e) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.err.printf("[%s] %s: %s%n", timestamp, e.getClass().getSimpleName(), e.getMessage());
        String msg = GestorMensajes.get("error.db.default");
        if (e.getMessage() != null) {
            String lower = e.getMessage().toLowerCase();
            if ("23505".equals(e.getSQLState()) || lower.contains("uq_") || lower.contains("duplicate key") || lower.contains("unique constraint") || lower.contains("llave duplicada") || lower.contains("unicidad")) msg = GestorMensajes.get("error.db.duplicate");
            else if (lower.contains("foreign key")) msg = GestorMensajes.get("error.db.foreignkey");
            else if (lower.contains("cédula") || lower.contains("correo") || lower.contains("licencia") || lower.contains("dependencias")) msg = e.getMessage();
        }
        JOptionPane.showMessageDialog(this, msg, GestorMensajes.get("error.db.title"), JOptionPane.ERROR_MESSAGE);
    }

    private void registrar() {
        if (!validarCampos()) return;
        MascotaDP m = new MascotaDP();
        m.setNombre(fmt(txtNombre.getText())); m.setEspecie((String) cmbEspecie.getSelectedItem()); m.setRaza(fmt(txtRaza.getText())); m.setEstadoAdopcion((String) cmbEstado.getSelectedItem());
        java.util.Date d = dcFechaNacimiento.getDate();
        if (d != null) m.setFechaNacimiento(new java.sql.Date(d.getTime()));
        String selCentro = (String) cmbCentroId.getSelectedItem(); m.setIdCentro(Integer.parseInt(selCentro.split(" - ")[0]));
        try { m.grabarDP(SesionUsuario.getUsuarioActual().getIdPersonal()); limpiarFormulario(); recargarTabla(); JOptionPane.showMessageDialog(this, GestorMensajes.get("success.register", "Mascota"), GestorMensajes.get("success.title"), JOptionPane.INFORMATION_MESSAGE); onDataChanged.run(); } catch (SQLException e) { manejarError(e); }
    }

    private void modificar() {
        if (selectedMascotaId == -1 || !validarCampos()) return;
        MascotaDP m = new MascotaDP();
        m.setIdMascota(selectedMascotaId);
        m.setNombre(fmt(txtNombre.getText())); m.setEspecie((String) cmbEspecie.getSelectedItem()); m.setRaza(fmt(txtRaza.getText())); m.setEstadoAdopcion((String) cmbEstado.getSelectedItem());
        java.util.Date d = dcFechaNacimiento.getDate();
        if (d != null) m.setFechaNacimiento(new java.sql.Date(d.getTime()));
        String selCentro = (String) cmbCentroId.getSelectedItem(); m.setIdCentro(Integer.parseInt(selCentro.split(" - ")[0]));
        try { m.grabarDP(SesionUsuario.getUsuarioActual().getIdPersonal()); limpiarFormulario(); recargarTabla(); JOptionPane.showMessageDialog(this, GestorMensajes.get("success.update", "Mascota"), GestorMensajes.get("success.title"), JOptionPane.INFORMATION_MESSAGE); onDataChanged.run(); } catch (SQLException e) { manejarError(e); }
    }

    private void eliminar() {
        if (selectedMascotaId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this, GestorMensajes.get("confirm.delete.msg"), GestorMensajes.get("confirm.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try { MascotaDP m = new MascotaDP(); m.setIdMascota(selectedMascotaId); 
              String selCentro = (String) cmbCentroId.getSelectedItem(); m.setIdCentro(Integer.parseInt(selCentro.split(" - ")[0]));
              m.eliminarDP(SesionUsuario.getUsuarioActual().getIdPersonal()); limpiarFormulario(); recargarTabla(); JOptionPane.showMessageDialog(this, GestorMensajes.get("success.delete", "Mascota"), GestorMensajes.get("success.title"), JOptionPane.INFORMATION_MESSAGE); onDataChanged.run(); 
        } catch (SQLException e) { manejarError(e); }
    }

    private void cargarSeleccionado() {
        int row = tblMascotas.getSelectedRow();
        if (row != -1) {
            selectedMascotaId = (int) modelMascotas.getValueAt(row, 0); txtNombre.setText((String) modelMascotas.getValueAt(row, 1)); cmbEspecie.setSelectedItem(modelMascotas.getValueAt(row, 2));
            try { dcFechaNacimiento.setDate(java.sql.Date.valueOf((String) modelMascotas.getValueAt(row, 3))); } catch (Exception ignored) {}
            txtRaza.setText((String) modelMascotas.getValueAt(row, 5)); cmbEstado.setSelectedItem(modelMascotas.getValueAt(row, 6));
            String centroName = (String) modelMascotas.getValueAt(row, 7);
            for (int i = 0; i < cmbCentroId.getItemCount(); i++) {
                if (cmbCentroId.getItemAt(i).endsWith(centroName)) { cmbCentroId.setSelectedIndex(i); break; }
            }
            btnRegistrar.setEnabled(false); btnModificar.setEnabled(true); btnEliminar.setEnabled(true);
        }
    }

    private void limpiarFormulario() {
        selectedMascotaId = -1; txtNombre.setText(""); cmbEspecie.setSelectedIndex(0); dcFechaNacimiento.setDate(new java.util.Date()); txtRaza.setText(""); cmbEstado.setSelectedIndex(0); cmbCentroId.setSelectedIndex(0);
        tblMascotas.clearSelection(); btnRegistrar.setEnabled(true); btnModificar.setEnabled(false); btnEliminar.setEnabled(false);
    }
}
