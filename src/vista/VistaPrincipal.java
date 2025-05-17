package vista;

import modelo.Usuario;
import modelo.Role;
import vista.PanelInicio;
import vista.PanelMateriales;
import vista.PanelPrestamos;
import vista.PanelDevoluciones;
import vista.PanelMora;
import vista.PanelUsuarios;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class VistaPrincipal extends JFrame {
    // Usuario actualmente autenticado en el sistema
    private final Usuario currentUser;
    // Paneles de la interfaz
    private final JPanel sidebar, panelCentral;
    // CardLayout permite cambiar dinámicamente entre paneles visibles
    private final CardLayout cardLayout;
    // Botones de navegación para cambiar entre paneles
    private final JButton btnInicio, btnMateriales, btnPrestamos,
                          btnDevoluciones, btnMora, btnGestionUsuarios;
    // Mapa para mantener una referencia a todos los paneles por nombre
    private final Map<String, JPanel> cards = new LinkedHashMap<>();
    
    // Constructor principal que recibe al usuario autenticado
    
    public VistaPrincipal(Usuario u) {
        super("Biblioteca – " + u.getRol());
        this.currentUser = u;
        
        // Constructor interfaz
        sidebar      = new JPanel(new GridLayout(0,1,5,5));
        panelCentral = new JPanel();
        cardLayout   = new CardLayout();
        panelCentral.setLayout(cardLayout);

        // Botones de navegación
        btnInicio          = new JButton("Inicio");
        btnMateriales      = new JButton("Materiales");
        btnPrestamos       = new JButton("Préstamos");
        btnDevoluciones    = new JButton("Devoluciones");
        btnMora            = new JButton("Mora");
        btnGestionUsuarios = new JButton("Usuarios");

        for (JButton b : Arrays.asList(
            btnInicio, btnMateriales, btnPrestamos,
            btnDevoluciones, btnMora, btnGestionUsuarios
        )) {
            sidebar.add(b);
        }

        // Crear e insertar cada panel en el card layout
        cards.put("Inicio",       new PanelInicio(panelCentral, cardLayout, currentUser));
        cards.put("Materiales",   new PanelMateriales(panelCentral, cardLayout, cards, currentUser));
        cards.put("Préstamos",    new PanelPrestamos(currentUser));
        cards.put("Devoluciones", new PanelDevoluciones(currentUser));
        cards.put("Mora",         new PanelMora());
        cards.put("Usuarios",     new PanelUsuarios(panelCentral, cardLayout, cards, currentUser));

        cards.forEach((name, panel) -> panelCentral.add(panel, name));

        // Layout principal
        getContentPane().setLayout(new BorderLayout(10,10));
        getContentPane().add(sidebar,      BorderLayout.WEST);
        getContentPane().add(panelCentral, BorderLayout.CENTER);

        // Listeners de navegación
        btnInicio.addActionListener(e -> cardLayout.show(panelCentral, "Inicio"));
        btnMateriales.addActionListener(e -> cardLayout.show(panelCentral, "Materiales"));
        btnPrestamos.addActionListener(e -> cardLayout.show(panelCentral, "Préstamos"));
        btnDevoluciones.addActionListener(e -> cardLayout.show(panelCentral, "Devoluciones"));
        btnMora.addActionListener(e -> cardLayout.show(panelCentral, "Mora"));
        btnGestionUsuarios.addActionListener(e -> cardLayout.show(panelCentral, "Usuarios"));

        configureByRole();

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // Oculta o muestra el boton de Usuarios segun el rol 
    private void configureByRole() {
        Role rol = currentUser.getRol();
        // Solo ADMIN puede ver y acceder a gestión de usuarios
        btnGestionUsuarios.setVisible(rol == Role.ADMIN);
        // Solo ADMIN puede ver el panel de moras
        btnMora.setVisible(currentUser.getRol() == Role.ADMIN);
        
    }
}

