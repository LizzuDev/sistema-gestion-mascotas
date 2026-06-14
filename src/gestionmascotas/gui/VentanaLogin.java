package gestionmascotas.gui;
import gestionmascotas.dp.controladores.SesionUsuario;
import gestionmascotas.dp.modelos.PersonalDP;
import gestionmascotas.md.PersonalMD;
import javax.swing.*;
import java.awt.*;
public class VentanaLogin extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtClave;
    public VentanaLogin() {
        setTitle("PAE - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblTitle = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        pnlCenter.add(lblTitle, gbc);
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        pnlCenter.add(new JLabel("Identificación:"), gbc);
        txtUsuario = new JTextField(15);
        gbc.gridx = 1;
        pnlCenter.add(txtUsuario, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        pnlCenter.add(new JLabel("Clave:"), gbc);
        txtClave = new JPasswordField(15);
        gbc.gridx = 1;
        pnlCenter.add(txtClave, gbc);
        JButton btnLogin = new JButton("Ingresar");
        btnLogin.setBackground(new Color(16, 185, 129));
        btnLogin.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        pnlCenter.add(btnLogin, gbc);
        add(pnlCenter, BorderLayout.CENTER);
        btnLogin.addActionListener(e -> hacerLogin());
    }
    private void hacerLogin() {
        String usuario = txtUsuario.getText();
        String clave = new String(txtClave.getPassword());
        try {
            PersonalMD md = new PersonalMD();
            PersonalDP p = md.autenticar(usuario, clave);
            if (p != null) {
                SesionUsuario.setUsuarioActual(p);
                this.dispose();
                new VentanaPrincipal().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas o usuario inactivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}