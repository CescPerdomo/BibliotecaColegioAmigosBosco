package vista;

import modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Permite al usuario (principalmente rol ADMIN) gestionar devoluciones, eliminaciones
// y renovaciones de prestamos activos tambien calcula mora en caso aplique

public class PanelDevoluciones extends JPanel {
    // DAOs necesarios
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    // Tabla y modelo para mostrar los prestamos
    private final JTable tabla;
    private final DefaultTableModel modelo;
    // Usuario autenticado actual
    private final Usuario currentUser;

    // Constructor que controla la vista y los botones del panel
    
    public PanelDevoluciones(Usuario currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout());

        modelo = new DefaultTableModel(new Object[]{
            "ID", "Usuario", "ID Material", "Fecha Préstamo", "Fecha Devolución", "Estado"
        }, 0);
        tabla = new JTable(modelo);

        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnDevolver = new JButton("Devolver");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRenovar = new JButton("Renovar Préstamo");
        JButton btnRefrescar = new JButton("Refrescar");
        toolBar.add(btnDevolver);
        toolBar.add(btnEliminar);
        toolBar.add(btnRenovar);
        toolBar.add(btnRefrescar);
        add(toolBar, BorderLayout.NORTH);

        btnDevolver.addActionListener(this::devolver);
        btnEliminar.addActionListener(this::eliminar);
        btnRenovar.addActionListener(this::renovar);
        btnRefrescar.addActionListener(e -> cargarDatos());

        // El boton Renovar solo es visible para rol ADMIN
        
        if (currentUser.getRol() != Role.ADMIN) {
            btnRenovar.setVisible(false);
        }

        cargarDatos(); //carga de prestamos al iniciar
    }
            // Carga todos los prestamos en la tabla
    private void cargarDatos() {
        modelo.setRowCount(0);
        for (Prestamo p : prestamoDAO.listar()) {
            modelo.addRow(new Object[]{
                p.getId(),
                p.getUsuarioPresta(),
                p.getIdMaterial(),
                p.getFechaPrestamo(),
                p.getFechaDevolucion(),
                p.getEstado()
            });
        }
    }
    
        // Accion para devolver un prestamo y registrar mora si aplica
    
    private void devolver(ActionEvent e) {
        int row = tabla.getSelectedRow();
        if (row == -1) return;
        int id = (int) modelo.getValueAt(row, 0);
        Prestamo p = prestamoDAO.buscarPorId(id);
        if (p == null || !"ACTIVO".equals(p.getEstado())) return;

        // Calculo de dias desde la fecha de prestamo
            
        Instant ahora = Instant.now();
        long dias = ChronoUnit.DAYS.between(p.getFechaPrestamo().toInstant(), ahora);
        Role rol = usuarioDAO.obtenerRol(p.getUsuarioPresta());
        long limite = rol == Role.ALUMNO ? 3 : 15;
        long mora = Math.max(0, dias - limite);
        double multa = mora > 0 ? mora * 0.25 : 0;

        // Actualiza el prestamo como devuelto
        
        p.setFechaDevolucion(Timestamp.from(ahora));
        p.setEstado("DEVUELTO");
        prestamoDAO.actualizar(p);

        // Si hay mora la registra
        
        if (mora > 0) {
            Mora m = new Mora();
            m.setPrestamoId(p.getId());
            m.setUsuario(p.getUsuarioPresta());
            m.setIdMaterial(p.getIdMaterial());
            m.setDiasMora((int) mora);
            m.setMontoMora(multa + 0.25); // // Se suma $0.25 extra por la regla del cobro del ejemplar mas el dia
            new MoraDAO().insertar(m);
        }

        cargarDatos();
    }
        // Elimina un prestamo de la BD
    private void eliminar(ActionEvent e) {
        int row = tabla.getSelectedRow();
        if (row == -1) return;
        int id = (int) modelo.getValueAt(row, 0);
        prestamoDAO.eliminar(id);
        cargarDatos();
    }
        // Renueva un prestamo activo si no esta en mora
    private void renovar(ActionEvent e) {
        int row = tabla.getSelectedRow();
        if (row == -1) return;
        int id = (int) modelo.getValueAt(row, 0);
        Prestamo p = prestamoDAO.buscarPorId(id);
        if (p == null || !"ACTIVO".equals(p.getEstado())) {
            JOptionPane.showMessageDialog(this, "Solo se pueden renovar préstamos activos.");
            return;
        }

        Instant ahora = Instant.now();
        long dias = ChronoUnit.DAYS.between(p.getFechaPrestamo().toInstant(), ahora);
        Role rol = usuarioDAO.obtenerRol(p.getUsuarioPresta());
        long limite = rol == Role.ALUMNO ? 3 : 15;
        if (dias > limite) {
            JOptionPane.showMessageDialog(this, "No se puede renovar: el préstamo ya está en mora.");
            return;
        }

        // para renovacion sumar el mismo limite de dias desde la fecha actual
        p.setFechaPrestamo(Timestamp.from(ahora));
        p.setFechaDevolucion(Timestamp.from(ahora.plus(limite, ChronoUnit.DAYS)));
        p.setEstado("RENOVADO");
        prestamoDAO.actualizar(p);
        cargarDatos();
        JOptionPane.showMessageDialog(this, "Préstamo renovado exitosamente.");
    }
}
  





