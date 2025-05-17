

package vista;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import modelo.AuthDAO;
import modelo.Usuario;
import modelo.Role;      // Se Importa el enum de roles

public class VistaLoginCustom extends JFrame {
    private AuthDAO authDAO;
    private java.util.List<LoginSuccessListener> loginSuccessListeners;

    // Componentes de la interfaz
    private JPanel mainPanel;
    private JLabel lblLogo;
    private JLabel lblTitulo, lblColegio, lblBosco;
    private JPanel loginPanel;
    private JLabel lblUsuario, lblContrasena;
    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JButton btnIngresar;

    // Listener para login exitoso //
    public interface LoginSuccessListener {
        void onLoginSuccess(Usuario usuario);
    }

    public VistaLoginCustom() {
        super("Sistema De Biblioteca");
        authDAO = new AuthDAO();
        loginSuccessListeners = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        // Configuración del jframe
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Panel principal con el fondo personalizado
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(230, 240, 255)); // azul claro
            }
        };
        mainPanel.setLayout(null);

        // Logo de la Universidad o Colegio
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("logologo.png"));
        lblLogo = new JLabel(logoIcon);
        lblLogo.setBounds(50, 100, 150, 150);
        mainPanel.add(lblLogo);

        // Títulos
        lblTitulo  = new JLabel("Sistema De Biblioteca", SwingConstants.CENTER);
        lblTitulo .setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo .setForeground(new Color(63, 81, 181));
        lblTitulo .setBounds(0, 30, 500, 40);
        mainPanel.add(lblTitulo );

        lblColegio = new JLabel("Colegio Amigos", SwingConstants.CENTER);
        lblColegio.setFont(new Font("Arial", Font.BOLD, 20));
        lblColegio.setBounds(0, 70, 500, 25);
        mainPanel.add(lblColegio);

        lblBosco   = new JLabel("de Don Bosco", SwingConstants.CENTER);
        lblBosco  .setFont(new Font("Arial", Font.BOLD, 20));
        lblBosco  .setBounds(0, 95, 500, 25);
        mainPanel.add(lblBosco);

        // Panel de login
        loginPanel = new JPanel(null);
        loginPanel.setOpaque(false);
        loginPanel.setBounds(250, 140, 200, 150);

        lblUsuario    = new JLabel("Usuario:");
        lblUsuario   .setFont(new Font("Arial", Font.PLAIN, 14));
        lblUsuario   .setBounds(0, 0, 200, 25);
        loginPanel.add(lblUsuario);

        txtUsuario    = new JTextField();
        txtUsuario   .setBounds(0, 25, 200, 25);
        loginPanel.add(txtUsuario);

        lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(new Font("Arial", Font.PLAIN, 14));
        lblContrasena.setBounds(0, 60, 200, 25);
        loginPanel.add(lblContrasena);

        txtClave      = new JPasswordField();
        txtClave     .setBounds(0, 85, 200, 25);
        loginPanel.add(txtClave);

        btnIngresar   = new JButton("Ingresar");
        btnIngresar  .setBounds(50, 120, 100, 30);
        btnIngresar  .setBackground(new Color(51, 103, 214));
        btnIngresar  .setForeground(Color.BLACK);
        btnIngresar  .setFocusPainted(false);
        btnIngresar  .setBorderPainted(false);
        btnIngresar  .setFont(new Font("Arial", Font.BOLD, 14));
        btnIngresar  .addActionListener(e -> validarLogin());
        loginPanel.add(btnIngresar);

        mainPanel.add(loginPanel);
        setContentPane(mainPanel);
    }

    private void validarLogin() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtClave.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Por favor ingresa usuario y contraseña",
                "Error de validación",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (authDAO.autenticarUsuario(user, pass)) {
            // Aqui Login construye Usuario con Rol
            Usuario u = new Usuario();
            u.setUsuario(user);
            u.setRol(authDAO.obtenerRol(user));  // aquí usamos el enum Role
            JOptionPane.showMessageDialog(
                this,
                "Bienvenido al Sistema",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );
            notifyLoginSuccess(u);
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Usuario o contraseña incorrectos",
                "Error de autenticación",
                JOptionPane.ERROR_MESSAGE
            );
            txtClave.setText("");
            txtClave.requestFocus();
        }
    }

    // Se Registra un listener para cuando el login sea exitoso //
    public void addLoginSuccessListener(LoginSuccessListener l) {
        loginSuccessListeners.add(l);
    }

    // Validacion y Notificacion a los listeners el usuario autenticado //
    private void notifyLoginSuccess(Usuario u) {
        for (LoginSuccessListener l : loginSuccessListeners) {
            l.onLoginSuccess(u);
        }
    }
}

