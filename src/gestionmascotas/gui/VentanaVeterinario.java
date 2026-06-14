package gestionmascotas.gui;
import gestionmascotas.dp.controladores.SesionUsuario;
import gestionmascotas.dp.modelos.VeterinarioDP;
import gestionmascotas.util.GestorMensajes;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
public class VentanaVeterinario extends JPanel {
    private JTable tblVeterinarios;
    private DefaultTableModel modelVeterinarios;
    private JTextField txtCedula;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtEspecialidad;
    private JTextField txtLicencia;
    private JTextField txtTelefono;
    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private int selectedVeterinarioId = -1;
    private final Runnable onDataChanged;
    public VentanaVeterinario(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel pnlHeader = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Gestión de Veterinarios");
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
        
        pnlForm.add(new JLabel("Especialidad:"));
        txtEspecialidad = new JTextField(); 
        txtEspecialidad.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isLetter(e.getKeyChar()) && !Character.isWhitespace(e.getKeyChar()) || txtEspecialidad.getText().length() >= 100) e.consume(); } });
        pnlForm.add(txtEspecialidad);
        
        pnlForm.add(new JLabel("Licencia:"));
        txtLicencia = new JTextField(); 
        txtLicencia.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (txtLicencia.getText().length() >= 50) e.consume(); } });
        pnlForm.add(txtLicencia);
        
        pnlForm.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField(); 
        txtTelefono.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar()) || txtTelefono.getText().length() >= 10) e.consume(); } });
        pnlForm.add(txtTelefono);

        pnlForm.add(new JLabel("")); pnlForm.add(new JLabel("")); // fillers

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
        String[] columnas = {"ID", "Cédula", "Nombres", "Apellidos", "Especialidad", "Licencia", "Teléfono"};
        modelVeterinarios = new DefaultTableModel(columnas, 0) { public boolean isCellEditable(int row, int column) { return false; } };
        tblVeterinarios = new JTable(modelVeterinarios);
        tblVeterinarios.getColumnModel().getColumn(0).setMaxWidth(30);
        tblVeterinarios.getColumnModel().getColumn(0).setPreferredWidth(30);
        pnlTable.add(new JScrollPane(tblVeterinarios), BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);
        btnRegistrar.addActionListener(e -> registrar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        tblVeterinarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblVeterinarios.getSelectedRow() != -1) cargarSeleccionado();
        });
        recargarTabla();
    }
    public void recargarTabla() {
        try {
            modelVeterinarios.setRowCount(0);
            for (VeterinarioDP v : new VeterinarioDP().consultarDP()) {
                modelVeterinarios.addRow(new Object[]{v.getIdVeterinario(), v.getCedula(), v.getNombres(), v.getApellidos(), v.getEspecialidad(), v.getLicencia(), v.getTelefono()});
            }
        } catch (SQLException e) { manejarError(e); }
    }
    private boolean validarCampos() {
        if (txtCedula.getText().trim().isEmpty() || txtNombres.getText().trim().isEmpty() || txtApellidos.getText().trim().isEmpty() ||
            txtEspecialidad.getText().trim().isEmpty() || txtLicencia.getText().trim().isEmpty() || txtTelefono.getText().trim().isEmpty()) {
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
        VeterinarioDP v = new VeterinarioDP();
        v.setCedula(txtCedula.getText().trim()); v.setNombres(fmt(txtNombres.getText())); v.setApellidos(fmt(txtApellidos.getText())); v.setEspecialidad(fmt(txtEspecialidad.getText())); v.setLicencia(txtLicencia.getText().trim()); v.setTelefono(txtTelefono.getText().trim());
        try { v.grabarDP(SesionUsuario.getUsuarioActual().getIdPersonal()); limpiarFormulario(); recargarTabla(); JOptionPane.showMessageDialog(this, GestorMensajes.get("success.register", "Veterinario"), GestorMensajes.get("success.title"), JOptionPane.INFORMATION_MESSAGE); onDataChanged.run(); } catch (SQLException e) { manejarError(e); }
    }

    private void modificar() {
        if (selectedVeterinarioId == -1 || !validarCampos()) return;
        VeterinarioDP v = new VeterinarioDP();
        v.setIdVeterinario(selectedVeterinarioId); v.setCedula(txtCedula.getText().trim()); v.setNombres(fmt(txtNombres.getText())); v.setApellidos(fmt(txtApellidos.getText())); v.setEspecialidad(fmt(txtEspecialidad.getText())); v.setLicencia(txtLicencia.getText().trim()); v.setTelefono(txtTelefono.getText().trim());
        try { v.grabarDP(SesionUsuario.getUsuarioActual().getIdPersonal()); limpiarFormulario(); recargarTabla(); JOptionPane.showMessageDialog(this, GestorMensajes.get("success.update", "Veterinario"), GestorMensajes.get("success.title"), JOptionPane.INFORMATION_MESSAGE); onDataChanged.run(); } catch (SQLException e) { manejarError(e); }
    }

    private void eliminar() {
        if (selectedVeterinarioId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this, GestorMensajes.get("confirm.delete.msg"), GestorMensajes.get("confirm.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try { VeterinarioDP v = new VeterinarioDP(); v.setIdVeterinario(selectedVeterinarioId); v.eliminarDP(SesionUsuario.getUsuarioActual().getIdPersonal()); limpiarFormulario(); recargarTabla(); JOptionPane.showMessageDialog(this, GestorMensajes.get("success.delete", "Veterinario"), GestorMensajes.get("success.title"), JOptionPane.INFORMATION_MESSAGE); onDataChanged.run(); } catch (SQLException e) { manejarError(e); }
    }
    private void cargarSeleccionado() {
        int row = tblVeterinarios.getSelectedRow();
        if (row != -1) {
            selectedVeterinarioId = (int) modelVeterinarios.getValueAt(row, 0); txtCedula.setText((String) modelVeterinarios.getValueAt(row, 1)); txtNombres.setText((String) modelVeterinarios.getValueAt(row, 2)); txtApellidos.setText((String) modelVeterinarios.getValueAt(row, 3)); txtEspecialidad.setText((String) modelVeterinarios.getValueAt(row, 4)); txtLicencia.setText((String) modelVeterinarios.getValueAt(row, 5)); txtTelefono.setText((String) modelVeterinarios.getValueAt(row, 6));
            btnRegistrar.setEnabled(false); btnModificar.setEnabled(true); btnEliminar.setEnabled(true);
        }
    }
    private void limpiarFormulario() {
        selectedVeterinarioId = -1; txtCedula.setText(""); txtNombres.setText(""); txtApellidos.setText(""); txtEspecialidad.setText(""); txtLicencia.setText(""); txtTelefono.setText("");
        tblVeterinarios.clearSelection(); btnRegistrar.setEnabled(true); btnModificar.setEnabled(false); btnEliminar.setEnabled(false);
    }
}
