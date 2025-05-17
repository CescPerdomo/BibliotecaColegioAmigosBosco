package vista;

import modelo.MaterialDAO;
import modelo.PrestamoDAO;
import modelo.UsuarioDAO;
import modelo.Usuario;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Window;
import vista.VistaLoginCustom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.awt.CardLayout;


 // panel principal de bienvenida con un banner institucional
 
public class PanelInicio extends JPanel {
    private MaterialDAO materialDAO = new MaterialDAO();
    private PrestamoDAO prestamoDAO = new PrestamoDAO();
    private UsuarioDAO usuarioDAO   = new UsuarioDAO();
    private Usuario currentUser;
    // Usuario que inicio sesion
    private JPanel parentPanel;
    private CardLayout parentLayout;

    
     // Constructor principal se encarga de cargar los paneles 
     
    
    public PanelInicio(JPanel parentPanel, CardLayout parentLayout, Usuario currentUser) {
        this.parentPanel  = parentPanel;
        this.parentLayout = parentLayout;
        this.currentUser  = currentUser;
        initComponents();
    }

    
     // Metodo que construye y organiza los componentes visuales
     
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // encabezado con titulo y fecha
        
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy"));
        JLabel lblTitle = new JLabel(
            String.format(
                "<html><h1><u>Biblioteca Digital</u></h1>" +
                "<p>Hoy es %s</p></html>", fecha
            ),
            SwingConstants.CENTER
        );
        add(lblTitle, BorderLayout.NORTH);

        // parte central banner
        
        JLabel lblBanner = new JLabel();
        lblBanner.setIcon(new ImageIcon(getClass().getResource("/resources/images/bannerudb.jpg")));
        lblBanner.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblBanner, BorderLayout.CENTER);
        
        // Boton Cerrar sesion
        
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(btnCerrarSesion, BorderLayout.SOUTH);
        btnCerrarSesion.addActionListener(e -> {
            
        // se cierra la ventana actual
        
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.dispose();

        // se abre de nuevo login
        
        VistaLoginCustom login = new VistaLoginCustom();
        login.setLocationRelativeTo(null);
        login.setVisible(true);
        });
    }
}







