package gestionmascotas.gui;

import gestionmascotas.dp.controladores.SesionUsuario;
import gestionmascotas.dp.modelos.AdoptanteDP;
import gestionmascotas.util.GestorMensajes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;
import com.toedter.calendar.JDateChooser;

public class VentanaAdoptante extends JPanel {

    private JTable tblAdoptantes;
    private DefaultTableModel modelAdoptantes;

    private JTextField txtCedula;
    private JTextField txtNombre;
    private JDateChooser dcFechaNacimiento;
    private JTextField txtDireccion;
    private JTextField txtOcupacion;

    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private int selectedAdoptanteId = -1;
    private final Runnable onDataChanged;

    public VentanaAdoptante(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel pnlHeader = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Gestión de Adoptantes (F2)");
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
        txtNombre = new JTextField();
        txtNombre.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isLetter(e.getKeyChar()) && !Character.isWhitespace(e.getKeyChar()) || txtNombre.getText().length() >= 100) e.consume(); } });
        pnlForm.add(txtNombre);

        pnlForm.add(new JLabel("Fecha de Nacimiento:"));
        dcFechaNacimiento = new JDateChooser();
        dcFechaNacimiento.setDateFormatString("yyyy-MM-dd");
        java.util.Calendar cal = java.util.Calendar.getInstance(); cal.add(java.util.Calendar.YEAR, -18); dcFechaNacimiento.setMaxSelectableDate(cal.getTime());
        pnlForm.add(dcFechaNacimiento);

        pnlForm.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        txtDireccion.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (txtDireccion.getText().length() >= 200) e.consume(); } });
        pnlForm.add(txtDireccion);

        pnlForm.add(new JLabel("Ocupación:"));
        txtOcupacion = new JTextField();
        txtOcupacion.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isLetter(e.getKeyChar()) && !Character.isWhitespace(e.getKeyChar()) || txtOcupacion.getText().length() >= 150) e.consume(); } });
        pnlForm.add(txtOcupacion);

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
        String[] columnas = {"ID", "Cédula", "Nombre", "F. Nacimiento", "Edad", "Dirección", "Ocupación"};
        modelAdoptantes = new DefaultTableModel(columnas, 0) { public boolean isCellEditable(int row, int column) { return false; } };
        tblAdoptantes = new JTable(modelAdoptantes);
        tblAdoptantes.getColumnModel().getColumn(0).setMaxWidth(30); tblAdoptantes.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblAdoptantes.getColumnModel().getColumn(4).setMaxWidth(60);
        pnlTable.add(new JScrollPane(tblAdoptantes), BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);

        btnRegistrar.addActionListener(e -> registrar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tblAdoptantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblAdoptantes.getSelectedRow() != -1) {
                cargarSeleccionado();
            }
        });

        recargarTabla();
    }

    public void recargarTabla() {
        try {
            modelAdoptantes.setRowCount(0);
            List<AdoptanteDP> lista = new AdoptanteDP().consultarDP();
            for (AdoptanteDP a : lista) {
                String edadStr = "";
                if(a.getFechaNacimiento() != null) {
                    java.time.LocalDate fn = a.getFechaNacimiento().toLocalDate();
                    edadStr = java.time.Period.between(fn, java.time.LocalDate.now()).getYears() + " años";
                }
                modelAdoptantes.addRow(new Object[]{
                    a.getIdAdoptante(), a.getCedula(), a.getNombre(),
                    a.getFechaNacimiento().toString(), edadStr, a.getDireccion(), a.getOcupacion()
                });
            }
        } catch (SQLException e) {
            manejarError(e);
        }
    }

    private boolean validarCampos() {
        if (txtCedula.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty() || 
            txtDireccion.getText().trim().isEmpty() || txtOcupacion.getText().trim().isEmpty() || dcFechaNacimiento.getDate() == null) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.incomplete.msg"), GestorMensajes.get("val.incomplete.title"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!txtCedula.getText().trim().matches("^\\d{10}$")) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.cedula"), GestorMensajes.get("val.error.title"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!txtNombre.getText().trim().matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ ]{3,100}$")) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.nombres"), GestorMensajes.get("val.error.title"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtDireccion.getText().trim().length() < 5 || txtDireccion.getText().trim().length() > 200) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.direccion"), GestorMensajes.get("val.error.title"), JOptionPane.WARNING_MESSAGE);
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

    private boolean validarEdad(java.util.Date fechaNac) {
        if (fechaNac == null) return false;
        LocalDate fn = fechaNac.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int edad = Period.between(fn, LocalDate.now()).getYears();
        if (edad < 18) {
            JOptionPane.showMessageDialog(this, GestorMensajes.get("val.edad.adoptante"), GestorMensajes.get("val.error.title"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void registrar() {
        if (!validarCampos()) return;
        java.util.Date d = dcFechaNacimiento.getDate();
        if (!validarEdad(d)) return;

        AdoptanteDP a = new AdoptanteDP();
        a.setCedula(txtCedula.getText().trim());
        a.setNombre(fmt(txtNombre.getText()));
        a.setFechaNacimiento(new java.sql.Date(d.getTime()));
        a.setDireccion(fmt(txtDireccion.getText()));
        a.setOcupacion(fmt(txtOcupacion.getText()));

        try {
            int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
            a.grabarDP(ejecutor);
            limpiarFormulario();
            recargarTabla();
            JOptionPane.showMessageDialog(this, GestorMensajes.get("success.register", "Adoptante"), GestorMensajes.get("success.title"), JOptionPane.INFORMATION_MESSAGE);
            onDataChanged.run();
        } catch (SQLException e) {
            manejarError(e);
        }
    }

    private void modificar() {
        if (selectedAdoptanteId == -1 || !validarCampos()) return;
        java.util.Date d = dcFechaNacimiento.getDate();
        if (!validarEdad(d)) return;

        AdoptanteDP a = new AdoptanteDP();
        a.setIdAdoptante(selectedAdoptanteId);
        a.setCedula(txtCedula.getText().trim());
        a.setNombre(fmt(txtNombre.getText()));
        a.setFechaNacimiento(new java.sql.Date(d.getTime()));
        a.setDireccion(fmt(txtDireccion.getText()));
        a.setOcupacion(fmt(txtOcupacion.getText()));

        try {
            int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
            a.grabarDP(ejecutor);
            limpiarFormulario();
            recargarTabla();
            JOptionPane.showMessageDialog(this, GestorMensajes.get("success.update", "Adoptante"), GestorMensajes.get("success.title"), JOptionPane.INFORMATION_MESSAGE);
            onDataChanged.run();
        } catch (SQLException e) {
            manejarError(e);
        }
    }

    private void eliminar() {
        if (selectedAdoptanteId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this, GestorMensajes.get("confirm.delete.msg"), GestorMensajes.get("confirm.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try {
            AdoptanteDP a = new AdoptanteDP();
            a.setIdAdoptante(selectedAdoptanteId);
            a.eliminarDP(SesionUsuario.getUsuarioActual().getIdPersonal());
            limpiarFormulario();
            recargarTabla();
            JOptionPane.showMessageDialog(this, GestorMensajes.get("success.delete", "Adoptante"), GestorMensajes.get("success.title"), JOptionPane.INFORMATION_MESSAGE);
            onDataChanged.run();
        } catch (SQLException e) {
            manejarError(e);
        }
    }

    private void cargarSeleccionado() {
        int row = tblAdoptantes.getSelectedRow();
        if (row != -1) {
            selectedAdoptanteId = (int) modelAdoptantes.getValueAt(row, 0);
            txtCedula.setText((String) modelAdoptantes.getValueAt(row, 1));
            txtNombre.setText((String) modelAdoptantes.getValueAt(row, 2));
            try {
                dcFechaNacimiento.setDate(java.sql.Date.valueOf((String) modelAdoptantes.getValueAt(row, 3)));
            } catch (Exception ignored) {}
            txtDireccion.setText((String) modelAdoptantes.getValueAt(row, 5));
            txtOcupacion.setText((String) modelAdoptantes.getValueAt(row, 6));

            btnRegistrar.setEnabled(false);
            btnModificar.setEnabled(true);
            btnEliminar.setEnabled(true);
        }
    }

    private void limpiarFormulario() {
        selectedAdoptanteId = -1;
        txtCedula.setText("");
        txtNombre.setText("");
        dcFechaNacimiento.setDate(new java.util.Date());
        txtDireccion.setText("");
        txtOcupacion.setText("");

        tblAdoptantes.clearSelection();
        btnRegistrar.setEnabled(true);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
}
