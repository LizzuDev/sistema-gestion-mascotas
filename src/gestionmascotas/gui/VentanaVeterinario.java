package gestionmascotas.gui;

import gestionmascotas.dp.controllers.SesionUsuario;
import gestionmascotas.dp.models.VeterinarioDP;
import gestionmascotas.md.IVeterinarioMD;
import gestionmascotas.md.VeterinarioMD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Módulo de Gestión de Veterinarios (F5).
 * Encargado: Gabriel Aguinaga (Calidad).
 * Cumple con STD-02, STD-05 (UI Segura) y STD-09 (Errores).
 */
public class VentanaVeterinario extends JPanel {
    private final IVeterinarioMD veterinarioMD = new VeterinarioMD();

    private JTable tblVeterinarios;
    private DefaultTableModel modelVeterinarios;

    private JTextField txtNombre;
    private JTextField txtIdentificacion;
    private JTextField txtEspecialidad;
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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Cabecera
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("Gestión de Veterinarios (F5)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));
        JLabel lblDesc = new JLabel("Administre el equipo de profesionales veterinarios asociados al refugio.");
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

        // Identificacion (Cédula)
        gbc.gridy = 2;
        pnlForm.add(new JLabel("Identificación (Cédula):"), gbc);
        gbc.gridy = 3;
        txtIdentificacion = new JTextField();
        txtIdentificacion.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(txtIdentificacion, gbc);

        // Especialidad
        gbc.gridy = 4;
        pnlForm.add(new JLabel("Especialidad médica:"), gbc);
        gbc.gridy = 5;
        txtEspecialidad = new JTextField();
        txtEspecialidad.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(txtEspecialidad, gbc);

        // Telefono
        gbc.gridy = 6;
        pnlForm.add(new JLabel("Teléfono Contacto:"), gbc);
        gbc.gridy = 7;
        txtTelefono = new JTextField();
        txtTelefono.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(txtTelefono, gbc);

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

        String[] columnas = {"ID", "Nombre", "Identificación", "Especialidad", "Teléfono"};
        modelVeterinarios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblVeterinarios = new JTable(modelVeterinarios);
        tblVeterinarios.setRowHeight(25);
        tblVeterinarios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblVeterinarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblVeterinarios.setShowGrid(false);

        JTableHeader header = tblVeterinarios.getTableHeader();
        header.setBackground(new Color(241, 245, 249));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tblVeterinarios);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        pnlTable.add(scroll, BorderLayout.CENTER);

        add(pnlTable, BorderLayout.CENTER);

        // Listeners
        btnRegistrar.addActionListener(e -> registrarVeterinario());
        btnModificar.addActionListener(e -> modificarVeterinario());
        btnEliminar.addActionListener(e -> eliminarVeterinario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tblVeterinarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblVeterinarios.getSelectedRow() != -1) {
                cargarVeterinarioSeleccionado();
            }
        });

        // Inicializar
        recargarTabla();
    }

    public void recargarTabla() {
        try {
            modelVeterinarios.setRowCount(0);
            List<VeterinarioDP> lista = veterinarioMD.obtenerTodos();
            for (VeterinarioDP v : lista) {
                modelVeterinarios.addRow(new Object[]{
                    v.getIdVeterinario(),
                    v.getNombre(),
                    v.getIdentificacion(),
                    v.getEspecialidad(),
                    v.getTelefono()
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar veterinarios: " + e.getMessage());
        }
    }

    private void registrarVeterinario() {
        if (!validarCampos()) return;

        VeterinarioDP v = new VeterinarioDP();
        v.setNombre(txtNombre.getText().trim());
        v.setIdentificacion(txtIdentificacion.getText().trim());
        v.setEspecialidad(txtEspecialidad.getText().trim());
        v.setTelefono(txtTelefono.getText().trim());

        try {
            int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
            veterinarioMD.insertar(v, ejecutor);
            
            JOptionPane.showMessageDialog(this, "Veterinario registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            recargarTabla();
            onDataChanged.run();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error de base de datos al registrar veterinario: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarVeterinario() {
        if (selectedVeterinarioId == -1) return;
        if (!validarCampos()) return;

        VeterinarioDP v = new VeterinarioDP();
        v.setIdVeterinario(selectedVeterinarioId);
        v.setNombre(txtNombre.getText().trim());
        v.setIdentificacion(txtIdentificacion.getText().trim());
        v.setEspecialidad(txtEspecialidad.getText().trim());
        v.setTelefono(txtTelefono.getText().trim());

        try {
            int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
            veterinarioMD.actualizar(v, ejecutor);
            
            JOptionPane.showMessageDialog(this, "Veterinario modificado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            recargarTabla();
            onDataChanged.run();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error de base de datos al modificar veterinario: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarVeterinario() {
        if (selectedVeterinarioId == -1) return;
        int result = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este veterinario?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
                veterinarioMD.eliminar(selectedVeterinarioId, ejecutor);
                
                JOptionPane.showMessageDialog(this, "Veterinario eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                recargarTabla();
                onDataChanged.run();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar veterinario (posiblemente esté agendado en citas activas): " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarVeterinarioSeleccionado() {
        int row = tblVeterinarios.getSelectedRow();
        if (row != -1) {
            selectedVeterinarioId = (int) modelVeterinarios.getValueAt(row, 0);
            txtNombre.setText((String) modelVeterinarios.getValueAt(row, 1));
            txtIdentificacion.setText((String) modelVeterinarios.getValueAt(row, 2));
            txtEspecialidad.setText((String) modelVeterinarios.getValueAt(row, 3));
            txtTelefono.setText((String) modelVeterinarios.getValueAt(row, 4));

            aplicarPermisosVisuales();
        }
    }

    private void limpiarFormulario() {
        selectedVeterinarioId = -1;
        txtNombre.setText("");
        txtIdentificacion.setText("");
        txtEspecialidad.setText("");
        txtTelefono.setText("");
        
        tblVeterinarios.clearSelection();
        btnRegistrar.setEnabled(true);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);

        aplicarPermisosVisuales();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() || 
            txtIdentificacion.getText().trim().isEmpty() || 
            txtEspecialidad.getText().trim().isEmpty() || 
            txtTelefono.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public void aplicarPermisosVisuales() {
        boolean tienePermisoEscritura = SesionUsuario.esLider() || SesionUsuario.esCalidad();
        
        if (!tienePermisoEscritura) {
            btnRegistrar.setEnabled(false);
            btnModificar.setEnabled(false);
            btnEliminar.setEnabled(false);
            
            txtNombre.setEnabled(false);
            txtIdentificacion.setEnabled(false);
            txtEspecialidad.setEnabled(false);
            txtTelefono.setEnabled(false);
        } else {
            txtNombre.setEnabled(true);
            txtIdentificacion.setEnabled(true);
            txtEspecialidad.setEnabled(true);
            txtTelefono.setEnabled(true);

            if (selectedVeterinarioId != -1) {
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
