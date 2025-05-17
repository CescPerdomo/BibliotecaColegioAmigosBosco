package vista;

import modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;


// Muestra y gestiona moras generadas por devoluciones atrasadas
// Tambien permite buscar por usuario, simular moras (para efectos de pruebas en vivo sin restriccion d fecha), y mostrar totales por ejemplares
// Solo debe estar visible para rol ADMIN (controlado desde VistaPrincipal)

public class PanelMora extends JPanel {
    private final MoraDAO moraDAO = new MoraDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final MoraResumenDAO resumenDAO = new MoraResumenDAO();
    private final JTable tabla;
    private final DefaultTableModel modelo;
    private final JTextField txtBuscar;

    public PanelMora() {
        setLayout(new BorderLayout());

        modelo = new DefaultTableModel(new Object[]{
            "Usuario", "Días Mora", "Ejemplares Prestados", "Monto Mora Total"
        }, 0);

        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtBuscar = new JTextField(15);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnSimular = new JButton("Simular Mora");

        panelNorte.add(new JLabel("Usuario:"));
        panelNorte.add(txtBuscar);
        panelNorte.add(btnBuscar);
        panelNorte.add(btnRefrescar);
        panelNorte.add(btnSimular);

        add(panelNorte, BorderLayout.NORTH);

        btnBuscar.addActionListener(e -> buscar(txtBuscar.getText().trim()));
        btnRefrescar.addActionListener(e -> cargarDatos());
        btnSimular.addActionListener(e -> simularMora());

        cargarDatos();
    }
            // Carga todas las moras y muestra los datos acumulados en la tabla
    
    private void cargarDatos() {
        modelo.setRowCount(0);
        List<Mora> moras = moraDAO.listarTodas();
        double total = 0.0;
        for (Mora m : moras) {
            double monto = m.getMontoMora();
            total += monto;
            modelo.addRow(new Object[]{
                m.getUsuario(),
                m.getDiasMora(),
                prestamoDAO.contarEjemplaresPrestadosPorUsuario(m.getUsuario()),
                String.format("$%.2f", monto)
            });
        }
        
        // Muestra fila total al final de la tabla
        
        modelo.addRow(new Object[]{"", "", "TOTAL:", String.format("$%.2f", total)});
    }
        // Filtra las moras por nombre de usuario
    private void buscar(String usuario) {
        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre de usuario.");
            return;
        }
        modelo.setRowCount(0);
        List<Mora> moras = moraDAO.listarPorUsuario(usuario);
        double total = 0.0;
        for (Mora m : moras) {
            double monto = m.getMontoMora();
            total += monto;
            modelo.addRow(new Object[]{
                m.getUsuario(),
                m.getDiasMora(),
                prestamoDAO.contarEjemplaresPrestadosPorUsuario(m.getUsuario()),
                String.format("$%.2f", monto)
            });
        }
        modelo.addRow(new Object[]{"", "", "TOTAL:", String.format("$%.2f", total)});
    }
        // Simula una mora para un usuario y material, se dejo activo para efectos de pruebas y demostraciones en vivo
        // Registra un prestamo simulado
        // Calcula mora con $0.25 extra por ejemplar
        // Inserta o actualiza la mora en la tabla resumen
    
    private void simularMora() {
        try {
            String usuario = JOptionPane.showInputDialog(this, "Nombre de usuario:");
            if (usuario == null || usuario.trim().isEmpty()) return;

            String sIdMat = JOptionPane.showInputDialog(this, "ID del material:");
            if (sIdMat == null || sIdMat.trim().isEmpty()) return;
            int idMat = Integer.parseInt(sIdMat);

            String sDias = JOptionPane.showInputDialog(this, "¿Hace cuántos días se prestó?");
            if (sDias == null || sDias.trim().isEmpty()) return;
            int dias = Integer.parseInt(sDias);

            // Primero verifica la existencia del usuario
            
            Usuario u = usuarioDAO.buscarPorNombre(usuario);
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Usuario no encontrado.");
                return;
            }
                // Valida limites de prestamo segun rol
                
            int prestamosActuales = prestamoDAO.countActiveByUser(usuario);
            int limite = (u.getRol() == Role.ALUMNO) ? 5 : 10;
            if (prestamosActuales >= limite) {
                JOptionPane.showMessageDialog(this, "Has alcanzado el máximo permitido de préstamos.");
                return;
            }

            int maxDias = (u.getRol() == Role.ALUMNO) ? 3 : 15;
            int moraDias = dias - maxDias;
            if (moraDias <= 0) {
                JOptionPane.showMessageDialog(this, "Este préstamo aún está dentro del período permitido.");
                return;
            }

            int ejemplares = 1; // un prestamo simulado representa un solo ejemplar

            // Simula prestamo
            Prestamo p = new Prestamo();
            p.setIdMaterial(idMat);
            p.setUsuarioPresta(usuario);
            p.setFechaPrestamo(Timestamp.valueOf(LocalDate.now().minusDays(dias).atStartOfDay()));
            p.setFechaDevolucion(Timestamp.valueOf(LocalDate.now().minusDays(dias - maxDias).atStartOfDay()));
            p.setEstado("SIMULADO");

            if (!prestamoDAO.agregar(p)) {
                JOptionPane.showMessageDialog(this, "No se pudo crear préstamo simulado.");
                return;
            }
                // Registra la mora simulada
            Mora m = new Mora();
            m.setPrestamoId(p.getId());
            m.setUsuario(usuario);
            m.setIdMaterial(idMat);
            m.setDiasMora(moraDias);
            m.setEjemplaresPrestados(ejemplares);
            m.setMontoMora(moraDias * 0.25 + ejemplares * 0.25); // $0.25 extra por ejemplar
            moraDAO.insertar(m);

            // Actualiza resumen de mora por usuario
            
            double total = moraDAO.obtenerTotalPorUsuario(usuario);
            resumenDAO.insertarOActualizar(usuario, total);

            cargarDatos();
            JOptionPane.showMessageDialog(this, "Mora simulada registrada correctamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en simulación: " + e.getMessage());
        }
    }
}


