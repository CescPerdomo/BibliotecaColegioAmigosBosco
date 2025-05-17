package vista;

import modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

// Formulario para registrar (segun rol de usuario), buscar, y prestar materiales

public class PanelMateriales extends JPanel {

    private final JPanel parentPanel;
    private final CardLayout parentCardLayout;
    private final Map<String, JPanel> cards;
    // Usuario autenticado
    private final Usuario currentUser;
    
    // DAOs para acceso a base de datos
    
    private final MaterialDAO materialDAO;
    private final PrestamoDAO prestamoDAO;
    private final UsuarioDAO usuarioDAO;
    
    // Componentes interfaz
    
    private JTable table;
    private JScrollPane scrollPane;
    private JLabel mensaje;
    private DefaultTableModel tableModel;

    private JComboBox<String> comboTipo;
    
    // Campos del formulario
    
    private JTextField txtTitulo, txtAutor, txtCategoria, txtEstado, txtISBN, txtEdicion, txtISSN, txtPaginas, txtDuracion, txtAnio;
    private JFormattedTextField txtFecha;
    private JTextField txtBuscar;
    
    // Constructor que inicializa los DAOs, el diseño y carga datos
    
    public PanelMateriales(JPanel parentPanel, CardLayout parentCardLayout, Map<String, JPanel> cards, Usuario currentUser) {
        this.parentPanel = parentPanel;
        this.parentCardLayout = parentCardLayout;
        this.cards = cards;
        this.currentUser = currentUser;
        this.materialDAO = new MaterialDAO();
        this.prestamoDAO = new PrestamoDAO();
        this.usuarioDAO = new UsuarioDAO();

        setLayout(new BorderLayout());
        initComponents(); // Crea los elementos graficos
        loadData(); // Carga los materiales (solo para admin)
    }
        
        // Componentes visuales del panel
        
    private void initComponents() {
        JPanel panelForm = new JPanel(new GridLayout(0, 2, 10, 10));
        
        // Campos del formulario
        
        comboTipo = new JComboBox<>(new String[]{"Libro", "Revista", "AV"});
        txtTitulo = new JTextField();
        txtAutor = new JTextField();
        txtCategoria = new JTextField();
        txtEstado = new JTextField();
        txtISBN = new JTextField();
        txtEdicion = new JTextField();
        txtFecha = new JFormattedTextField();
        txtISSN = new JTextField();
        txtPaginas = new JTextField();
        txtDuracion = new JTextField();
        txtAnio = new JTextField();
        
        // se agregan campos
        
        panelForm.add(new JLabel("Tipo de Material:")); panelForm.add(comboTipo);
        panelForm.add(new JLabel("Título:")); panelForm.add(txtTitulo);
        panelForm.add(new JLabel("Autor:")); panelForm.add(txtAutor);
        panelForm.add(new JLabel("Categoría:")); panelForm.add(txtCategoria);
        panelForm.add(new JLabel("Estado:")); panelForm.add(txtEstado);
        panelForm.add(new JLabel("ISBN:")); panelForm.add(txtISBN);
        panelForm.add(new JLabel("Edición:")); panelForm.add(txtEdicion);
        panelForm.add(new JLabel("Fecha Publicación (YYYY-MM-DD):")); panelForm.add(txtFecha);
        panelForm.add(new JLabel("ISSN:")); panelForm.add(txtISSN);
        panelForm.add(new JLabel("Número de Páginas:")); panelForm.add(txtPaginas);
        panelForm.add(new JLabel("Duración (minutos):")); panelForm.add(txtDuracion);
        panelForm.add(new JLabel("Año:")); panelForm.add(txtAnio);

        JButton btnGuardar = new JButton("Guardar");
        
        // Condicion solo para el ADMIN para guardar materiales nuevos
        
        if (currentUser.getRol() != Role.ADMIN) {
            btnGuardar.setEnabled(false);
        }
        
        // Botones del formulario
        
        JButton btnLimpiar = new JButton("Limpiar Campos");
        JButton btnPrestar = new JButton("Prestar Material Seleccionado");
        JButton btnBuscar = new JButton("Consultar Materiales");
        JButton btnLimpiarBusqueda = new JButton("Limpiar Búsqueda");
        txtBuscar = new JTextField(20);

        panelForm.add(btnGuardar);
        panelForm.add(btnLimpiar);

        btnGuardar.addActionListener(e -> guardarMaterial());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        comboTipo.addActionListener(e -> actualizarCamposVisibles());
        actualizarCamposVisibles(); // Esto hace que se desactiven los campos que no corresponden al tipo de material a guardar

        add(panelForm, BorderLayout.NORTH);
        
        // tabla de resultados
        tableModel = new DefaultTableModel(new String[]{"ID", "Título", "Tipo", "Autor", "Estado"}, 0);
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        // se incluyo mensaje pero el ocultar la tabla por rol no queda visible
        mensaje = new JLabel("Materiales visibles solo para usuarios administradores. Realiza una búsqueda para filtrar.", JLabel.CENTER);
        mensaje.setFont(new Font("SansSerif", Font.ITALIC, 14));

        add(scrollPane, BorderLayout.CENTER);
        add(mensaje, BorderLayout.SOUTH);

        // Condicion para bloquear tabla de materiales si el rol no es ADMIN
        
        if (currentUser.getRol() != Role.ADMIN) {
        scrollPane.setVisible(false);
        mensaje.setVisible(true);
        } else {
        mensaje.setVisible(false);
        }
        
        // Panel inferior busqueda y boton de prestamo
        
        JPanel panelSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSouth.add(new JLabel("Buscar:"));
        panelSouth.add(txtBuscar);
        panelSouth.add(btnBuscar);
        panelSouth.add(btnLimpiarBusqueda);
        panelSouth.add(btnPrestar);
        add(panelSouth, BorderLayout.SOUTH);

        btnPrestar.addActionListener(e -> onPrestar());
        btnBuscar.addActionListener(e -> onBuscar());
        btnLimpiarBusqueda.addActionListener(e -> {
            txtBuscar.setText("");
            if (mensaje != null && mensaje.getParent() != null)
                mensaje.setVisible(currentUser.getRol() != Role.ADMIN);
            if (scrollPane != null && scrollPane.getParent() != null)
                scrollPane.setVisible(currentUser.getRol() == Role.ADMIN);
            loadData();
        });
    }
        // busqueda de materiales
        private void onBuscar() {
        String query = txtBuscar.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        boolean hasResults = false;

        if (!query.isEmpty()) {
        List<Material> lista = materialDAO.listar();
        for (Material m : lista) {
            String titulo = m.getTitulo() != null ? m.getTitulo().toLowerCase() : "";
            String autor = m.getAutor() != null ? m.getAutor().toLowerCase() : "";
            String categoria = m.getCategoria() != null ? m.getCategoria().toLowerCase() : "";

            if (titulo.contains(query) || autor.contains(query) || categoria.contains(query)) {
                tableModel.addRow(new Object[]{
                    m.getId(), m.getTitulo(), m.getTipoMaterial(), m.getAutor(), m.getEstado()
                });
                hasResults = true;
            }
        }
    } else {
        loadData();
        if (currentUser.getRol() != Role.ADMIN) {
        if (mensaje != null && mensaje.getParent() != null)
            mensaje.setVisible(true);
        if (scrollPane != null && scrollPane.getParent() != null)
            scrollPane.setVisible(false);
    }
        return;
    }

    if (currentUser.getRol() != Role.ADMIN) {
        if (scrollPane != null && scrollPane.getParent() != null)
            scrollPane.setVisible(hasResults);
        if (mensaje != null && mensaje.getParent() != null)
            mensaje.setVisible(!hasResults);
    }
}
         // Accion de prestar y registrar el prestamo
    private void onPrestar() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un material.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

            // Restricción por rol: 5 préstamos maximo para ALUMNO, 10 para PROFESOR
        int activos = prestamoDAO.countActiveByUser(currentUser.getUsuario());
        if ((currentUser.getRol() == Role.ALUMNO && activos >= 5) ||
            (currentUser.getRol() == Role.PROFESOR && activos >= 10)) {
            JOptionPane.showMessageDialog(this, "Has alcanzado tu límite de préstamos.", "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
            return;
        }
            // registro del prestamo
        int idMat = (int) tableModel.getValueAt(sel, 0);
        String tipo = (String) comboTipo.getSelectedItem();

        Prestamo p = new Prestamo();
        p.setIdMaterial(idMat);
        p.setTipoMaterial(tipo);
        p.setUsuarioPresta(currentUser.getUsuario());
        p.setFechaPrestamo(new Timestamp(System.currentTimeMillis()));
        int dias = (currentUser.getRol() == Role.ALUMNO) ? 3 : 15;
        p.setFechaDevolucion(Timestamp.from(Instant.now().plus(dias, ChronoUnit.DAYS)));
        p.setEstado("ACTIVO");

        if (!prestamoDAO.agregar(p)) {
            JOptionPane.showMessageDialog(this, "Error al registrar el préstamo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
            // Esto actualiza el contador de ejemplares del usuario
        currentUser.setEjemplares_prestados(currentUser.getEjemplares_prestados() + 1);
        usuarioDAO.actualizar(currentUser);
        
        // Esto hace que al dar click en prestar agrega el prestamo al Panel de Prestamos directamente
        
        PanelPrestamos pp = (PanelPrestamos) cards.get("Préstamos");
        pp.addPrestamoDirecto(p);
        parentCardLayout.show(parentPanel, "Préstamos");
    }
        // Campos habilitados por rol
    private void actualizarCamposVisibles() {
        String tipo = (String) comboTipo.getSelectedItem();
        txtISBN.setEnabled("Libro".equals(tipo));
        txtEdicion.setEnabled("Libro".equals(tipo));
        txtFecha.setEnabled("Libro".equals(tipo));

        txtISSN.setEnabled("Revista".equals(tipo));
        txtPaginas.setEnabled("Revista".equals(tipo));

        txtDuracion.setEnabled("AV".equals(tipo));
        txtAnio.setEnabled("AV".equals(tipo));
    }
    
        //Crea un objeto Material con los datos del formulario y lo guarda en la BD
    
    private void guardarMaterial() {
        Material m = new Material();
        m.setTipoMaterial((String) comboTipo.getSelectedItem());
        m.setTitulo(txtTitulo.getText());
        m.setAutor(txtAutor.getText());
        m.setCategoria(txtCategoria.getText());
        m.setEstado(txtEstado.getText());
        m.setISBN(txtISBN.getText());
        m.setEdicion(txtEdicion.getText());

        try {
            m.setFechaPublicacion(LocalDate.parse(txtFecha.getText()));
        } catch (Exception e) {
            m.setFechaPublicacion(null);
        }

        m.setIssn(txtISSN.getText());
        try {
            m.setNumeroPaginas(Integer.parseInt(txtPaginas.getText()));
        } catch (Exception e) {
            m.setNumeroPaginas(null);
        }

        try {
            m.setDuracionMinutos(Integer.parseInt(txtDuracion.getText()));
        } catch (Exception e) {
            m.setDuracionMinutos(null);
        }

        try {
            m.setAnio(Integer.parseInt(txtAnio.getText()));
        } catch (Exception e) {
            m.setAnio(null);
        }

        boolean exito = materialDAO.agregar(m);
        if (exito) {
            JOptionPane.showMessageDialog(this, "Material guardado correctamente");
            loadData();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el material");
        }
    }

    private void limpiarCampos() {
        txtTitulo.setText("");
        txtAutor.setText("");
        txtCategoria.setText("");
        txtEstado.setText("");
        txtISBN.setText("");
        txtEdicion.setText("");
        txtFecha.setText("");
        txtISSN.setText("");
        txtPaginas.setText("");
        txtDuracion.setText("");
        txtAnio.setText("");
        comboTipo.setSelectedIndex(0);
        actualizarCamposVisibles();
    }

        // Esto hace que se cargan todos los materiales desde la BD y los muestra en la tabla segun rol ADMIN
    
    private void loadData() {
        if (currentUser.getRol() != Role.ADMIN) return;
        tableModel.setRowCount(0);
        List<Material> lista = materialDAO.listar();
        for (Material m : lista) {
            tableModel.addRow(new Object[]{
                m.getId(), m.getTitulo(), m.getTipoMaterial(), m.getAutor(), m.getEstado()
            });
        }
    }
}









