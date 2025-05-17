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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Permite listar, buscar, agregar, devolver y eliminar prestamos
// Aplica restricciones por rol (ALUMNO y PROFESOR) y calcula la mora

public class PanelPrestamos extends JPanel {
    private static final Logger LOGGER = LogManager.getLogger(PanelPrestamos.class);

    private final Usuario currentUser; // Usuario autenticado
    private final PrestamoDAO dao; // Acceso a datos de prestamos


    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;

    public PanelPrestamos(Usuario currentUser) {
        this.currentUser = currentUser;
        this.dao = new PrestamoDAO();
        initComponents(); // Inicializa la interfaz
        loadData(); // Carga datos de prestamos
    }
            // componentes visuales del panel
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlNorth = new JPanel(new BorderLayout(5, 5));
        JPanel pnlBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        txtBuscar = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        pnlBuscar.add(new JLabel("Buscar (Usuario o ID material):"));
        pnlBuscar.add(txtBuscar);
        pnlBuscar.add(btnBuscar);
        pnlNorth.add(pnlBuscar, BorderLayout.NORTH);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnAgregar = new JButton("Agregar");
        JButton btnDevolver = new JButton("Devolver");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");

        toolBar.add(btnAgregar);
        toolBar.add(btnDevolver);
        toolBar.add(btnEliminar);
        toolBar.addSeparator();
        toolBar.add(btnRefrescar);
        pnlNorth.add(toolBar, BorderLayout.SOUTH);
        
        //Bloqueo por Privilegios de ADMIN

        // Deshabilita botones si no es ADMIN
        if (currentUser.getRol() != Role.ADMIN) {
            btnAgregar.setEnabled(false);
            btnEliminar.setEnabled(false);
            btnDevolver.setEnabled(false);
        }

        add(pnlNorth, BorderLayout.NORTH);
            // Tabla que muestra todos los prestamos
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Material", "Usuario", "Fecha Préstamo", "Fecha Devolución", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> onBuscar());
        btnRefrescar.addActionListener(e -> loadData());
        btnEliminar.addActionListener(e -> onEliminar());
        btnAgregar.addActionListener(e -> onAgregar());
        btnDevolver.addActionListener(e -> onDevolver(e));
    }
        // Carga todos los prestamos registrados desde la base de datos
    void loadData() {
        tableModel.setRowCount(0);
        try {
            List<Prestamo> lista = dao.listar();
            MaterialDAO mdao = new MaterialDAO();
            for (Prestamo p : lista) {
                String materialDesc;
                Material m = mdao.findById(p.getIdMaterial());
                materialDesc = (m != null) ? p.getIdMaterial() + " - " + m.getTitulo() : String.valueOf(p.getIdMaterial());
                tableModel.addRow(new Object[]{
                    p.getId(), materialDesc, p.getUsuarioPresta(),
                    p.getFechaPrestamo(), p.getFechaDevolucion(), p.getEstado()
                });
            }
        } catch (Exception e) {
            LOGGER.error("Error cargando datos de préstamos", e);
            JOptionPane.showMessageDialog(this,
                "Error al cargar préstamos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
            // Busca prestamos filtrando por usuario o ID de material
    private void onBuscar() {
        String term = txtBuscar.getText().trim();
        if (term.isEmpty()) {
            loadData();
            return;
        }
        tableModel.setRowCount(0);
        try {
            List<Prestamo> lista = dao.buscar(term);
            MaterialDAO mdao = new MaterialDAO();
            for (Prestamo p : lista) {
                String materialDesc;
                Material m = mdao.findById(p.getIdMaterial());
                materialDesc = (m != null) ? p.getIdMaterial() + " - " + m.getTitulo() : String.valueOf(p.getIdMaterial());
                tableModel.addRow(new Object[]{
                    p.getId(), materialDesc, p.getUsuarioPresta(),
                    p.getFechaPrestamo(), p.getFechaDevolucion(), p.getEstado()
                });
            }
        } catch (Exception ex) {
            LOGGER.error("Error al buscar préstamos", ex);
            JOptionPane.showMessageDialog(this,
                "Error en búsqueda: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        // Agrega un nuevo prestamo manualmente (solo para rol ADMIN)
    private void onAgregar() {
        int activos = dao.countActiveByUser(currentUser.getUsuario());
        // Validación por rol
        if (currentUser.getRol() == Role.ALUMNO && activos >= 5) {
            JOptionPane.showMessageDialog(this,
                "Máximo de 5 préstamos para ALUMNO.", "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (currentUser.getRol() == Role.PROFESOR && activos >= 10) {
            JOptionPane.showMessageDialog(this,
                "Máximo de 10 préstamos para PROFESOR.", "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String sIdMat = JOptionPane.showInputDialog(this, "ID del material:");
            if (sIdMat == null) return;
            int idMat = Integer.parseInt(sIdMat.trim());

            Prestamo p = new Prestamo();
            p.setIdMaterial(idMat);
            p.setUsuarioPresta(currentUser.getUsuario());
            p.setFechaPrestamo(new Timestamp(System.currentTimeMillis()));
            p.setEstado("ACTIVO");

            if (dao.agregar(p)) {
                currentUser.setEjemplares_prestados(currentUser.getEjemplares_prestados() + 1);
                new UsuarioDAO().actualizar(currentUser);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al agregar préstamo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
            // Devuelve un prestamo seleccionado y calcula si hay mora
    private void onDevolver(ActionEvent e) {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un préstamo.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(sel, 0);
        Prestamo p = dao.listar().stream()
            .filter(pr -> pr.getId() == id)
            .findFirst().orElse(null);
        if (p == null) {
            JOptionPane.showMessageDialog(this,
                "Préstamo no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Proceso de Calculo de mora
        
        Instant ahora = Instant.now();
        long diasTotal = ChronoUnit.DAYS.between(p.getFechaPrestamo().toInstant(), ahora);
        Role rol = new AuthDAO().obtenerRol(p.getUsuarioPresta());
        long diasGracia = (rol == Role.ALUMNO) ? 3 : (rol == Role.PROFESOR) ? 15 : Long.MAX_VALUE;
        long diasMora = diasTotal - diasGracia;
        double recargo = (diasMora > 0) ? diasMora * 0.25 : 0;

        p.setFechaDevolucion(new Timestamp(System.currentTimeMillis()));
        p.setEstado("DEVUELTO");
        if (dao.actualizar(p)) {
            currentUser.setEjemplares_prestados(currentUser.getEjemplares_prestados() - 1);
            new UsuarioDAO().actualizar(currentUser);
            
            // Si hay mora la se registra
            
            if (diasMora > 0) {
                Mora m = new Mora();
                m.setPrestamoId(p.getId());
                m.setUsuario(currentUser.getUsuario());
                m.setIdMaterial(p.getIdMaterial());
                m.setDiasMora((int) diasMora);
                m.setMontoMora(recargo);
                new MoraDAO().insertar(m);
            }
            loadData();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al devolver préstamo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
            // Elimina el prestamo seleccionado (solo rol ADMIN)
    private void onEliminar() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un préstamo.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(sel, 0);
        int opt = JOptionPane.showConfirmDialog(this,
            "¿Eliminar préstamo?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                if (dao.eliminar(id)) {
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error al eliminar préstamo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                LOGGER.error("Error eliminando préstamo", ex);
            }
        }
    }
                // para agregar una fila a la tabla de forma directa (desde PanelMateriales)
    public void addPrestamoDirecto(Prestamo p) {
        Material m = new MaterialDAO().findById(p.getIdMaterial());
        String materialDesc = (m != null)
            ? p.getIdMaterial() + " - " + m.getTitulo()
            : String.valueOf(p.getIdMaterial());
        tableModel.addRow(new Object[]{
            p.getId(), materialDesc, p.getUsuarioPresta(),
            p.getFechaPrestamo(), p.getFechaDevolucion(), p.getEstado()
        });
    }
}






