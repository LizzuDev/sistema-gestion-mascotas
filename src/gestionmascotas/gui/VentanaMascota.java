package gestionmascotas.gui;

import gestionmascotas.dp.controllers.SesionUsuario;
import gestionmascotas.dp.models.MascotaDP;
import gestionmascotas.md.IMascotaMD;
import gestionmascotas.md.MascotaMD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Módulo de Gestión de Mascotas (F1).
 * Implementado por Joselyn Cadena (Administradora de Desarrollo).
 * Cumple con STD-02, STD-05 (UI Segura) y STD-09 (Manejo de Errores).
 */
public class VentanaMascota extends JPanel {
    private final IMascotaMD mascotaMD = new MascotaMD();

    private JTable tblMascotas;
    private DefaultTableModel modelMascotas;

    private JTextField txtNombre;
    private JComboBox<String> cmbEspecie;
    private JSpinner spnEdad;
    private JComboBox<String> cmbEstado;
    private JTextField txtCentroId;

    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private int selectedMascotaId = -1;
    private final Runnable onDataChanged;

    public VentanaMascota(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Cabecera del Panel
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("Gestión de Mascotas (F1)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));
        JLabel lblDesc = new JLabel("Administre el registro y asignación de animales rescatados en los diferentes centros.");
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
        pnlForm.add(new JLabel("Nombre Mascota:"), gbc);
        gbc.gridy = 1;
        txtNombre = new JTextField();
        txtNombre.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(txtNombre, gbc);

        // Especie
        gbc.gridy = 2;
        pnlForm.add(new JLabel("Especie:"), gbc);
        gbc.gridy = 3;
        cmbEspecie = new JComboBox<>(new String[]{"PERRO", "GATO", "OTRO"});
        cmbEspecie.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(cmbEspecie, gbc);

        // Edad
        gbc.gridy = 4;
        pnlForm.add(new JLabel("Edad (Años):"), gbc);
        gbc.gridy = 5;
        spnEdad = new JSpinner(new SpinnerNumberModel(0, 0, 30, 1));
        spnEdad.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(spnEdad, gbc);

        // Estado
        gbc.gridy = 6;
        pnlForm.add(new JLabel("Estado:"), gbc);
        gbc.gridy = 7;
        cmbEstado = new JComboBox<>(new String[]{"DISPONIBLE", "ADOPTADA", "EN_TRATAMIENTO"});
        cmbEstado.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(cmbEstado, gbc);

        // Centro (ID Sede)
        gbc.gridy = 8;
        pnlForm.add(new JLabel("ID Centro / Refugio:"), gbc);
        gbc.gridy = 9;
        txtCentroId = new JTextField("1");
        txtCentroId.setPreferredSize(new Dimension(0, 30));
        pnlForm.add(txtCentroId, gbc);

        // Contenedor de Botones
        gbc.gridy = 10;
        gbc.insets = new java.awt.Insets(15, 5, 5, 5);
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 8, 8));
        pnlButtons.setBackground(new Color(248, 250, 252));

        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new Color(16, 185, 129)); // Mint
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegistrar.setFocusPainted(false);

        btnModificar = new JButton("Modificar");
        btnModificar.setBackground(new Color(59, 130, 246)); // Blue
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnModificar.setFocusPainted(false);
        btnModificar.setEnabled(false);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(239, 68, 68)); // Red
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

        String[] columnas = {"ID", "Nombre", "Especie", "Edad", "Estado", "ID Centro"};
        modelMascotas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMascotas = new JTable(modelMascotas);
        tblMascotas.setRowHeight(25);
        tblMascotas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblMascotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMascotas.setShowGrid(false);

        JTableHeader header = tblMascotas.getTableHeader();
        header.setBackground(new Color(241, 245, 249));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tblMascotas);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        pnlTable.add(scroll, BorderLayout.CENTER);

        add(pnlTable, BorderLayout.CENTER);

        // Listeners
        btnRegistrar.addActionListener(e -> registrarMascota());
        btnModificar.addActionListener(e -> modificarMascota());
        btnEliminar.addActionListener(e -> eliminarMascota());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tblMascotas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblMascotas.getSelectedRow() != -1) {
                cargarMascotaSeleccionada();
            }
        });

        // Inicializar
        recargarTabla();
    }

    public void recargarTabla() {
        try {
            modelMascotas.setRowCount(0);
            List<MascotaDP> mascotas = mascotaMD.obtenerTodas();
            for (MascotaDP m : mascotas) {
                modelMascotas.addRow(new Object[]{
                    m.getIdMascota(),
                    m.getNombre(),
                    m.getEspecie(),
                    m.getEdad(),
                    m.getEstado(),
                    m.getIdCentro()
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar tabla mascotas: " + e.getMessage());
        }
    }

    private void registrarMascota() {
        if (!validarCampos()) return;

        MascotaDP m = new MascotaDP();
        m.setNombre(txtNombre.getText().trim());
        m.setEspecie((String) cmbEspecie.getSelectedItem());
        m.setEdad((Integer) spnEdad.getValue());
        m.setEstado((String) cmbEstado.getSelectedItem());
        m.setIdCentro(Integer.parseInt(txtCentroId.getText().trim()));

        try {
            int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
            mascotaMD.insertar(m, ejecutor);
            
            JOptionPane.showMessageDialog(this, "Mascota registrada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            recargarTabla();
            onDataChanged.run();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error de base de datos al registrar mascota: " + e.getMessage(), 
                "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarMascota() {
        if (selectedMascotaId == -1) return;
        if (!validarCampos()) return;

        MascotaDP m = new MascotaDP();
        m.setIdMascota(selectedMascotaId);
        m.setNombre(txtNombre.getText().trim());
        m.setEspecie((String) cmbEspecie.getSelectedItem());
        m.setEdad((Integer) spnEdad.getValue());
        m.setEstado((String) cmbEstado.getSelectedItem());
        m.setIdCentro(Integer.parseInt(txtCentroId.getText().trim()));

        try {
            int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
            mascotaMD.actualizar(m, ejecutor);
            
            JOptionPane.showMessageDialog(this, "Mascota modificada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            recargarTabla();
            onDataChanged.run();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error de base de datos al modificar mascota: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarMascota() {
        if (selectedMascotaId == -1) return;
        int result = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar esta mascota del sistema?", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                int ejecutor = SesionUsuario.getUsuarioActual().getIdPersonal();
                int idCentro = Integer.parseInt(txtCentroId.getText().trim());
                
                mascotaMD.eliminar(selectedMascotaId, idCentro, ejecutor);
                
                JOptionPane.showMessageDialog(this, "Mascota eliminada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                recargarTabla();
                onDataChanged.run();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error de base de datos al eliminar mascota: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarMascotaSeleccionada() {
        int row = tblMascotas.getSelectedRow();
        if (row != -1) {
            selectedMascotaId = (int) modelMascotas.getValueAt(row, 0);
            txtNombre.setText((String) modelMascotas.getValueAt(row, 1));
            cmbEspecie.setSelectedItem(modelMascotas.getValueAt(row, 2));
            spnEdad.setValue(modelMascotas.getValueAt(row, 3));
            cmbEstado.setSelectedItem(modelMascotas.getValueAt(row, 4));
            txtCentroId.setText(String.valueOf(modelMascotas.getValueAt(row, 5)));

            // Cambiar disponibilidad de botones según rol de seguridad (STD-05)
            aplicarPermisosVisuales();
        }
    }

    private void limpiarFormulario() {
        selectedMascotaId = -1;
        txtNombre.setText("");
        cmbEspecie.setSelectedIndex(0);
        spnEdad.setValue(0);
        cmbEstado.setSelectedIndex(0);
        txtCentroId.setText("1");
        
        tblMascotas.clearSelection();
        btnRegistrar.setEnabled(true);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);

        aplicarPermisosVisuales();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la mascota no puede estar vacío.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(txtCentroId.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID del centro debe ser un número entero.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Aplica la seguridad visual nativa desactivando controles visuales usando setEnabled(false) (STD-05).
     * Joselyn Cadena (DESARROLLO) y el Líder del Equipo tienen acceso completo a este panel.
     * Gabriel Aguinaga (CALIDAD) tiene acceso de solo lectura (deshabilitado registrar/modificar/eliminar).
     */
    public void aplicarPermisosVisuales() {
        boolean tienePermisoEscritura = SesionUsuario.esLider() || SesionUsuario.esDesarrollo();
        
        if (!tienePermisoEscritura) {
            // Deshabilitar explícitamente controles de escritura según STD-05
            btnRegistrar.setEnabled(false);
            btnModificar.setEnabled(false);
            btnEliminar.setEnabled(false);
            
            txtNombre.setEnabled(false);
            cmbEspecie.setEnabled(false);
            spnEdad.setEnabled(false);
            cmbEstado.setEnabled(false);
            txtCentroId.setEnabled(false);
        } else {
            // Habilitar controles si tiene permiso
            txtNombre.setEnabled(true);
            cmbEspecie.setEnabled(true);
            spnEdad.setEnabled(true);
            cmbEstado.setEnabled(true);
            txtCentroId.setEnabled(true);

            // Mantener lógica de selección para modificar/eliminar
            if (selectedMascotaId != -1) {
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
