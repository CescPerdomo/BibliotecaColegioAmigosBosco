// Importación de clases necesarias para configurar apariencia visual y manejo de logs

import java.awt.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import modelo.Usuario;
import vista.VistaLoginCustom;
import vista.VistaPrincipal;
import javax.swing.*;

public class Main {
    // Logger utilizado para registrar eventos del sistema
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        
        try {
            // Establece la apariencia nativa del sistema operativo para Swing
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
            // Mejora la visualización de fuentes en interfaces gráficas Swing
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            
            //Mejora visual
            Color celeste = new Color(224, 242, 255); // tu celeste suave
        UIManager.put("Panel.background", new javax.swing.plaf.ColorUIResource(celeste));
        UIManager.put("Viewport.background", new javax.swing.plaf.ColorUIResource(celeste));
        UIManager.put("ScrollPane.background", new javax.swing.plaf.ColorUIResource(celeste));
       
            
        } catch (Exception e) {
            LOGGER.error("Error al iniciar sesion", e);
        }
        
        LOGGER.info("Iniciando aplicación de biblioteca");

        SwingUtilities.invokeLater(() -> {
            LOGGER.debug("Mostrando ventana de login");
            // Se instancia la ventana personalizada de login
            VistaLoginCustom login = new VistaLoginCustom();
            // Se agrega un listener para detectar un inicio de sesión exitoso
            login.addLoginSuccessListener(usuario -> {
                // Cierra la ventana de login una vez autenticado
                login.dispose();
                // Abre la vista principal (VistaPrincipal) y le pasa el usuario autenticado
                VistaPrincipal vp = new VistaPrincipal(usuario);
                vp.pack();
                vp.setLocationRelativeTo(null);
                vp.setVisible(true);
            });
            // Muestra la ventana de login
            login.setVisible(true);
        });
    }
}


