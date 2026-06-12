package gestionmascotas.gui;

import gestionmascotas.dp.controllers.SesionUsuario;
import gestionmascotas.dp.models.AdoptanteDP;
import gestionmascotas.md.AdoptanteMD;
import gestionmascotas.md.IAdoptanteMD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Módulo de Gestión de Adoptantes (F2).
 * Encargado: Joselyn Cadena (Desarrollo).
 * Cumple con STD-02, STD-05 (UI Segura) y STD-09 (Errores).
 */
public class VentanaAdoptante extends JPanel {
    private final IAdoptanteMD adoptanteMD = new AdoptanteMD();

    private JTable tblAdoptantes;
    private DefaultTableModel modelAdoptantes;

    private JTextField txtNombre;
    private JTextField txtIdentificacion;
    private JTextField txtCorreo;
    private JTextField txtTelefono;

    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private int selectedAdoptanteId = -1;
    private final Runnable onDataChanged;

    public VentanaAdoptante(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Cabecera
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("Gestión de Adoptantes (F2)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));
        JLabel lblDesc = new JLabel("Administre el registro y contacto de los adoptantes postulantes.");
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

        // Correo
        gbc.gridy = 4;
        pnlForm.add(new JLabel("Correo Electrónico:"), gbc);
        gbc.gridy = 5;
        txtCorreo = new JTextField();
        txtCorreo.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(txtCorreo, gbc);

        // Telefono
        gbc.gridy = 6;
        pnlForm.add(new JLabel("Teléfono de Contacto:"), gbc);
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

        // Tabla (Derecha)
        JPanel pnlTable = new JPanel(new BorderLayout(10, 10));
        pnlTable.setBackground(Color.WHITE);

        String[] columnas = {"ID", "Nombre", "Identificación", "Correo", "Teléfono"};
        modelAdoptantes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblAdoptantes = new JTable(modelAdoptantes);
        tblAdoptantes.setRowHeight(25);
        tblAdoptantes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblAdoptantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAdoptantes.setShowGrid(false);

        JTableHeader header = tblAdoptantes.getTableHeader();
        header.setBackground(new Color(241, 245, 249));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tblAdoptantes);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        pnlTable.add(scroll, BorderLayout.CENTER);

        add(pnlTable, BorderLayout.CENTER);

        // Listeners
        btnRegistrar.addActionListener(e -> registrarAdoptante());
        btnModificar.addActionListener(e -> modificarAdoptante());
        btnEliminar.addActionListener(e -> eliminarAdoptante());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tblAdoptantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblAdoptantes.getSelectedRow() != -1) {
                cargarAdoptanteSeleccionado();
            }
        });

        // Inicializar
        recargarTabla();
    }

    public void recargarTabla() {
        try {
            modelAdoptantes.setRowCount(0);
            List<AdoptanteDP> adoptantes = adoptanteMD.obtenerTodos();
            for (AdoptanteDP a : adoptantes) {
                modelAdoptantes.addRow(new Object[]{
                    a.getIdAdoptante(),
                    a.getNombre(),
                    a.getIdentificacion(),
                    a.getCorreo(),
                    a.getTelefono()
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar adoptantes: " + e.getMessage());
        }
    }

    private void registrarAdoptante() {
        if (!validarCampos()) return;

        AdoptanteDP a = new AdoptanteDP();
        a.setNombre(txtNombre.getText().trim());
        a.setIdentificacion(txtIdentificacion.getText().trim());
        a.setCorreo(txtCorreo.getText().trim());
        a.setTelefono(txtTelefono.getText().trim());

        try {
            int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
            adoptanteMD.insertar(a, ejecutor);
            
            JOptionPane.showMessageDialog(this, "Adoptante registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            recargarTabla();
            onDataChanged.run();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error de base de datos al registrar adoptante: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarAdoptante() {
        if (selectedAdoptanteId == -1) return;
        if (!validarCampos()) return;

        AdoptanteDP a = new AdoptanteDP();
        a.setIdAdoptante(selectedAdoptanteId);
        a.setNombre(txtNombre.getText().trim());
        a.setIdentificacion(txtIdentificacion.getText().trim());
        a.setCorreo(txtCorreo.getText().trim());
        a.setTelefono(txtTelefono.getText().trim());

        try {
            int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
            adoptanteMD.actualizar(a, ejecutor);
            
            JOptionPane.showMessageDialog(this, "Adoptante modificado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            recargarTabla();
            onDataChanged.run();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error de base de datos al modificar adoptante: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarAdoptante() {
        if (selectedAdoptanteId == -1) return;
        int result = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este adoptante?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
                adoptanteMD.eliminar(selectedAdoptanteId, ejecutor);
                
                JOptionPane.showMessageDialog(this, "Adoptante eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                recargarTabla();
                onDataChanged.run();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar adoptante (posiblemente vinculado a una adopción activa): " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarAdoptanteSeleccionado() {
        int row = tblAdoptantes.getSelectedRow();
        if (row != -1) {
            selectedAdoptanteId = (int) modelAdoptantes.getValueAt(row, 0);
            txtNombre.setText((String) modelAdoptantes.getValueAt(row, 1));
            txtIdentificacion.setText((String) modelAdoptantes.getValueAt(row, 2));
            txtCorreo.setText((String) modelAdoptantes.getValueAt(row, 3));
            txtTelefono.setText((String) modelAdoptantes.getValueAt(row, 4));

            aplicarPermisosVisuales();
        }
    }

    private void limpiarFormulario() {
        selectedAdoptanteId = -1;
        txtNombre.setText("");
        txtIdentificacion.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        
        tblAdoptantes.clearSelection();
        btnRegistrar.setEnabled(true);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);

        aplicarPermisosVisuales();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() || 
            txtIdentificacion.getText().trim().isEmpty() || 
            txtCorreo.getText().trim().isEmpty() || 
            txtTelefono.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Aplica restricciones visuales nativas usando setEnabled(false) (STD-05).
     * Joselyn Cadena (DESARROLLO) y el Líder tienen acceso de escritura.
     */
    public void aplicarPermisosVisuales() {
        boolean tienePermisoEscritura = SesionUsuario.esLider() || SesionUsuario.esDesarrollo();
        
        if (!tienePermisoEscritura) {
            btnRegistrar.setEnabled(false);
            btnModificar.setEnabled(false);
            btnEliminar.setEnabled(false);
            
            txtNombre.setEnabled(false);
            txtIdentificacion.setEnabled(false);
            txtCorreo.setEnabled(false);
            txtTelefono.setEnabled(false);
        } else {
            txtNombre.setEnabled(true);
            txtIdentificacion.setEnabled(true);
            txtCorreo.setEnabled(true);
            txtTelefono.setEnabled(true);

            if (selectedAdoptanteId != -1) {
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
