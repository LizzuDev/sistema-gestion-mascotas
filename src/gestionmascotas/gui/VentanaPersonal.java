package gestionmascotas.gui;

import gestionmascotas.dp.controllers.SesionUsuario;
import gestionmascotas.dp.models.PersonalDP;
import gestionmascotas.md.IPersonalMD;
import gestionmascotas.md.PersonalMD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Módulo de Gestión de Personal (F7).
 * Encargado: Gabriel Aguinaga (Planificación/Calidad).
 * Cumple con STD-02, STD-05 (UI Segura) y STD-09 (Errores).
 */
public class VentanaPersonal extends JPanel {
    private final IPersonalMD personalMD = new PersonalMD();

    private JTable tblPersonal;
    private DefaultTableModel modelPersonal;

    private JTextField txtNombre;
    private JTextField txtIdentificacion;
    private JComboBox<String> cmbRol;
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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Cabecera
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("Gestión de Personal (F7)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));
        JLabel lblDesc = new JLabel("Administre las cuentas de los integrantes del equipo y personal del refugio.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(100, 116, 139));
        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        pnlHeader.add(lblDesc, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // Formulario (Izquierda)
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(new Color(248, 250, 252));
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        pnlForm.setPreferredSize(new Dimension(320, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridy = 1;
        txtNombre = new JTextField();
        txtNombre.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(txtNombre, gbc);

        // Cédula
        gbc.gridy = 2;
        pnlForm.add(new JLabel("Identificación (Cédula):"), gbc);
        gbc.gridy = 3;
        txtIdentificacion = new JTextField();
        txtIdentificacion.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(txtIdentificacion, gbc);

        // Rol
        gbc.gridy = 4;
        pnlForm.add(new JLabel("Rol Metodológico:"), gbc);
        gbc.gridy = 5;
        cmbRol = new JComboBox<>(new String[]{"LIDER", "PLANIFICACION_CALIDAD", "DESARROLLO", "PERSONAL_APOYO"});
        cmbRol.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(cmbRol, gbc);

        // Estado
        gbc.gridy = 6;
        pnlForm.add(new JLabel("Estado:"), gbc);
        gbc.gridy = 7;
        cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});
        cmbEstado.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(cmbEstado, gbc);

        // Botones
        gbc.gridy = 8;
        gbc.insets = new java.awt.Insets(15, 5, 5, 5);
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 8, 8));
        pnlButtons.setBackground(new Color(248, 250, 252));

        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new Color(16, 185, 129));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegistrar.setFocusPainted(false);

        btnModificar = new JButton("Modificar");
        btnModificar.setBackground(new Color(59, 130, 246));
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnModificar.setFocusPainted(false);
        btnModificar.setEnabled(false);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(239, 68, 68));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEliminar.setFocusPainted(false);
        btnEliminar.setEnabled(false);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(new Color(100, 116, 139));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLimpiar.setFocusPainted(false);

        pnlButtons.add(btnRegistrar);
        pnlButtons.add(btnModificar);
        pnlButtons.add(btnEliminar);
        pnlButtons.add(btnLimpiar);

        pnlForm.add(pnlButtons, gbc);

        add(pnlForm, BorderLayout.WEST);

        // Tabla
        JPanel pnlTable = new JPanel(new BorderLayout(10, 10));
        pnlTable.setBackground(Color.WHITE);

        String[] columnas = {"ID", "Nombre", "Identificación", "Rol", "Estado"};
        modelPersonal = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPersonal = new JTable(modelPersonal);
        tblPersonal.setRowHeight(25);
        tblPersonal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblPersonal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPersonal.setShowGrid(false);

        JTableHeader header = tblPersonal.getTableHeader();
        header.setBackground(new Color(241, 245, 249));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tblPersonal);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        pnlTable.add(scroll, BorderLayout.CENTER);

        add(pnlTable, BorderLayout.CENTER);

        // Listeners
        btnRegistrar.addActionListener(e -> registrarPersonal());
        btnModificar.addActionListener(e -> modificarPersonal());
        btnEliminar.addActionListener(e -> eliminarPersonal());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tblPersonal.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblPersonal.getSelectedRow() != -1) {
                cargarPersonalSeleccionado();
            }
        });

        // Inicializar
        recargarTabla();
    }

    public void recargarTabla() {
        try {
            modelPersonal.setRowCount(0);
            List<PersonalDP> lista = personalMD.obtenerTodos();
            for (PersonalDP p : lista) {
                modelPersonal.addRow(new Object[]{
                    p.getIdPersonal(),
                    p.getNombre(),
                    p.getIdentificacion(),
                    p.getRol(),
                    p.getEstado()
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar personal: " + e.getMessage());
        }
    }

    private void registrarPersonal() {
        if (!validarCampos()) return;

        PersonalDP p = new PersonalDP();
        p.setNombre(txtNombre.getText().trim());
        p.setIdentificacion(txtIdentificacion.getText().trim());
        p.setRol((String) cmbRol.getSelectedItem());
        p.setEstado((String) cmbEstado.getSelectedItem());

        try {
            int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
            personalMD.insertar(p, ejecutor);
            
            JOptionPane.showMessageDialog(this, "Miembro del personal registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            recargarTabla();
            onDataChanged.run();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error de base de datos al registrar personal: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarPersonal() {
        if (selectedPersonalId == -1) return;
        if (!validarCampos()) return;

        PersonalDP p = new PersonalDP();
        p.setIdPersonal(selectedPersonalId);
        p.setNombre(txtNombre.getText().trim());
        p.setIdentificacion(txtIdentificacion.getText().trim());
        p.setRol((String) cmbRol.getSelectedItem());
        p.setEstado((String) cmbEstado.getSelectedItem());

        try {
            int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
            personalMD.actualizar(p, ejecutor);
            
            JOptionPane.showMessageDialog(this, "Personal modificado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            recargarTabla();
            onDataChanged.run();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error de base de datos al modificar personal: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPersonal() {
        if (selectedPersonalId == -1) return;
        if (selectedPersonalId == SesionUsuario.getUsuarioActual().getIdPersonal()) {
            JOptionPane.showMessageDialog(this, "No puede eliminarse a sí mismo de la sesión activa.", "Acción Bloqueada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este miembro de personal?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
                personalMD.eliminar(selectedPersonalId, ejecutor);
                
                JOptionPane.showMessageDialog(this, "Personal eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                recargarTabla();
                onDataChanged.run();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar (puede que esté vinculado a auditorías u adopciones registradas): " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarPersonalSeleccionado() {
        int row = tblPersonal.getSelectedRow();
        if (row != -1) {
            selectedPersonalId = (int) modelPersonal.getValueAt(row, 0);
            txtNombre.setText((String) modelPersonal.getValueAt(row, 1));
            txtIdentificacion.setText((String) modelPersonal.getValueAt(row, 2));
            cmbRol.setSelectedItem(modelPersonal.getValueAt(row, 3));
            cmbEstado.setSelectedItem(modelPersonal.getValueAt(row, 4));

            aplicarPermisosVisuales();
        }
    }

    private void limpiarFormulario() {
        selectedPersonalId = -1;
        txtNombre.setText("");
        txtIdentificacion.setText("");
        cmbRol.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        
        tblPersonal.clearSelection();
        btnRegistrar.setEnabled(true);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);

        aplicarPermisosVisuales();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() || 
            txtIdentificacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre e Identificación son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Aplica la seguridad visual nativa con setEnabled(false) (STD-05).
     * Gabriel Aguinaga (CALIDAD) y el Líder tienen acceso de escritura.
     * Joselyn Cadena (DESARROLLO) tiene acceso de solo lectura.
     */
    public void aplicarPermisosVisuales() {
        boolean tienePermisoEscritura = SesionUsuario.esLider() || SesionUsuario.esCalidad();
        
        if (!tienePermisoEscritura) {
            btnRegistrar.setEnabled(false);
            btnModificar.setEnabled(false);
            btnEliminar.setEnabled(false);
            
            txtNombre.setEnabled(false);
            txtIdentificacion.setEnabled(false);
            cmbRol.setEnabled(false);
            cmbEstado.setEnabled(false);
        } else {
            txtNombre.setEnabled(true);
            txtIdentificacion.setEnabled(true);
            cmbRol.setEnabled(true);
            cmbEstado.setEnabled(true);

            if (selectedPersonalId != -1) {
                btnRegistrar.setEnabled(false);
                btnModificar.setEnabled(true);
                btnEliminar.setEnabled(true);
            } else {
                btnRegistrar.setEnabled(true);
                btnModificar.setEnabled(false);
                btnEliminar.setEnabled(false);
            }
        }
    }
}
