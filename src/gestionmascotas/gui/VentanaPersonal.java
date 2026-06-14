package gestionmascotas.gui;
import gestionmascotas.dp.controladores.SesionUsuario;
import gestionmascotas.dp.modelos.PersonalDP;
import gestionmascotas.util.GestorMensajes;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
public class VentanaPersonal extends JPanel {
    private JTable tblPersonal;
    private DefaultTableModel modelPersonal;
    private JTextField txtCedula;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JComboBox<String> cmbCargo;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JPasswordField txtClave;
    private JComboBox<String> cmbEstado;
    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private int selectedPersonalId = -1;
    private final Runnable onDataChanged;
    public VentanaPersonal(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel pnlHeader = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Gestión de Personal");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        add(pnlHeader, BorderLayout.NORTH);
        JPanel pnlForm = new JPanel(new GridLayout(0, 4, 10, 10));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlForm.add(new JLabel("Cédula:"));
        txtCedula = new JTextField(); 
        txtCedula.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar()) || txtCedula.getText().length() >= 10) e.consume(); } });
        pnlForm.add(txtCedula);

        pnlForm.add(new JLabel("Nombres:"));
        txtNombres = new JTextField(); 
        txtNombres.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isLetter(e.getKeyChar()) && !Character.isWhitespace(e.getKeyChar()) || txtNombres.getText().length() >= 150) e.consume(); } });
        pnlForm.add(txtNombres);

        pnlForm.add(new JLabel("Apellidos:"));
        txtApellidos = new JTextField(); 
        txtApellidos.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isLetter(e.getKeyChar()) && !Character.isWhitespace(e.getKeyChar()) || txtApellidos.getText().length() >= 150) e.consume(); } });
        pnlForm.add(txtApellidos);

        pnlForm.add(new JLabel("Cargo:"));
        cmbCargo = new JComboBox<>(new String[]{"ADMINISTRADOR", "VOLUNTARIO", "VETERINARIO_JEFE"});
        pnlForm.add(cmbCargo);

        pnlForm.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField(); 
        txtTelefono.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar()) || txtTelefono.getText().length() >= 10) e.consume(); } });
        pnlForm.add(txtTelefono);

        pnlForm.add(new JLabel("Correo:"));
        txtCorreo = new JTextField(); 
        txtCorreo.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (txtCorreo.getText().length() >= 100) e.consume(); } });
        pnlForm.add(txtCorreo);

        pnlForm.add(new JLabel("Clave:"));
        txtClave = new JPasswordField();
        pnlForm.add(txtClave);

        pnlForm.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});
        pnlForm.add(cmbEstado);

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
        String[] columnas = {"ID", "Cédula", "Nombres", "Apellidos", "Cargo", "Teléfono", "Correo", "Estado"};
        modelPersonal = new DefaultTableModel(columnas, 0) { public boolean isCellEditable(int row, int column) { return false; } };
        tblPersonal = new JTable(modelPersonal);
        tblPersonal.getColumnModel().getColumn(0).setMaxWidth(30);
        tblPersonal.getColumnModel().getColumn(0).setPreferredWidth(30);
        pnlTable.add(new JScrollPane(tblPersonal), BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);
        btnRegistrar.addActionListener(e -> registrar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        tblPersonal.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblPersonal.getSelectedRow() != -1) cargarSeleccionado();
        });
        recargarTabla();
    }
    public void recargarTabla() {
        try {
            modelPersonal.setRowCount(0);
            for (PersonalDP p : new PersonalDP().consultarDP()) {
                modelPersonal.addRow(new Object[]{p.getIdPersonal(), p.getCedula(), p.getNombres(), p.getApellidos(), p.getCargo(), p.getTelefono(), p.getCorreo(), p.getEstado()});
            }
        } catch (SQLException e) { manejarError(e); }
    }
    private boolean validarCampos() {
        if (txtCedula.getText().trim().isEmpty() || txtNombres.getText().trim().isEmpty() || txtApellidos.getText().trim().isEmpty() ||
            txtTelefono.getText().trim().isEmpty() || txtCorreo.getText().trim().isEmpty() || new String(txtClave.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.incomplete.msg"), GestorMensajes.get("val.incomplete.title"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!txtCedula.getText().trim().matches("^\\d{10}$")) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.cedula"), GestorMensajes.get("val.error.title"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!txtNombres.getText().trim().matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ ]{3,100}$") || !txtApellidos.getText().trim().matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ ]{3,100}$")) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.nombres"), GestorMensajes.get("val.error.title"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!txtTelefono.getText().trim().matches("^\\d{7,10}$")) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.telefono"), GestorMensajes.get("val.error.title"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!txtCorreo.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.correo"), GestorMensajes.get("val.error.title"), JOptionPane.WARNING_MESSAGE);
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
        PersonalDP p = new PersonalDP();
        p.setCedula(txtCedula.getText().trim()); p.setNombres(fmt(txtNombres.getText())); p.setApellidos(fmt(txtApellidos.getText())); p.setCargo((String) cmbCargo.getSelectedItem()); p.setTelefono(txtTelefono.getText().trim()); p.setCorreo(txtCorreo.getText().trim().toLowerCase()); p.setClave(new String(txtClave.getPassword()).trim()); p.setEstado((String) cmbEstado.getSelectedItem());
        try { p.grabarDP(SesionUsuario.getUsuarioActual().getIdPersonal()); limpiarFormulario(); recargarTabla(); JOptionPane.showMessageDialog(this, GestorMensajes.get("success.register", "Personal"), GestorMensajes.get("success.title"), JOptionPane.INFORMATION_MESSAGE); onDataChanged.run(); } catch (SQLException e) { manejarError(e); }
    }

    private void modificar() {
        if (selectedPersonalId == -1 || !validarCampos()) return;
        PersonalDP p = new PersonalDP();
        p.setIdPersonal(selectedPersonalId); p.setCedula(txtCedula.getText().trim()); p.setNombres(fmt(txtNombres.getText())); p.setApellidos(fmt(txtApellidos.getText())); p.setCargo((String) cmbCargo.getSelectedItem()); p.setTelefono(txtTelefono.getText().trim()); p.setCorreo(txtCorreo.getText().trim().toLowerCase()); p.setClave(new String(txtClave.getPassword()).trim()); p.setEstado((String) cmbEstado.getSelectedItem());
        try { p.grabarDP(SesionUsuario.getUsuarioActual().getIdPersonal()); limpiarFormulario(); recargarTabla(); JOptionPane.showMessageDialog(this, GestorMensajes.get("success.update", "Personal"), GestorMensajes.get("success.title"), JOptionPane.INFORMATION_MESSAGE); onDataChanged.run(); } catch (SQLException e) { manejarError(e); }
    }

    private void eliminar() {
        if (selectedPersonalId == -1) return;
        if (selectedPersonalId == gestionmascotas.dp.controladores.SesionUsuario.getUsuarioActual().getIdPersonal()) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("block.self_delete"), GestorMensajes.get("block.title"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, GestorMensajes.get("confirm.delete.msg"), GestorMensajes.get("confirm.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try { PersonalDP p = new PersonalDP(); p.setIdPersonal(selectedPersonalId); p.eliminarDP(SesionUsuario.getUsuarioActual().getIdPersonal()); limpiarFormulario(); recargarTabla(); JOptionPane.showMessageDialog(this, GestorMensajes.get("success.delete", "Personal"), GestorMensajes.get("success.title"), JOptionPane.INFORMATION_MESSAGE); onDataChanged.run(); } catch (SQLException e) { manejarError(e); }
    }
    private void cargarSeleccionado() {
        int row = tblPersonal.getSelectedRow();
        if (row != -1) {
            selectedPersonalId = (int) modelPersonal.getValueAt(row, 0); txtCedula.setText((String) modelPersonal.getValueAt(row, 1)); txtNombres.setText((String) modelPersonal.getValueAt(row, 2)); txtApellidos.setText((String) modelPersonal.getValueAt(row, 3)); cmbCargo.setSelectedItem(modelPersonal.getValueAt(row, 4)); txtTelefono.setText((String) modelPersonal.getValueAt(row, 5)); txtCorreo.setText((String) modelPersonal.getValueAt(row, 6)); txtClave.setText(""); cmbEstado.setSelectedItem(modelPersonal.getValueAt(row, 7));
            btnRegistrar.setEnabled(false); btnModificar.setEnabled(true); btnEliminar.setEnabled(true);
        }
    }
    private void limpiarFormulario() {
        selectedPersonalId = -1; txtCedula.setText(""); txtNombres.setText(""); txtApellidos.setText(""); cmbCargo.setSelectedIndex(0); txtTelefono.setText(""); txtCorreo.setText(""); txtClave.setText(""); cmbEstado.setSelectedIndex(0);
        tblPersonal.clearSelection(); btnRegistrar.setEnabled(true); btnModificar.setEnabled(false); btnEliminar.setEnabled(false);
    }
}
