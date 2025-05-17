

package vista;

import modelo.Usuario;
import modelo.UsuarioDAO;
import modelo.Role;
import javax.swing.JPasswordField;
import modelo.UsuarioDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;


 // Panel de administración de usuarios solo accesible para rol ADMIN
 // Permite crear editar eliminar usuarios y restablecer contraseñas

public class PanelUsuarios extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private UsuarioDAO dao;
    private final JPanel parentPanel;
    private final CardLayout parentCardLayout;
    private final Map<String, JPanel> cards;
    private final Usuario currentUser;

    // Constructor con acceso al panel principal
    
    public PanelUsuarios(JPanel parentPanel,
                         CardLayout parentCardLayout,
                         Map<String,JPanel> cards,
                         Usuario currentUser) {
        this.parentPanel      = parentPanel;
        this.parentCardLayout = parentCardLayout;
        this.cards            = cards;
        this.currentUser      = currentUser;
        this.dao              = new UsuarioDAO();
        initComponents();
        loadData();
        
    }
            // inicializa componentes visuales
    private void initComponents() {
        setLayout(new BorderLayout(10,10));

        // Barra de Herramientas
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        JButton btnAgregar   = new JButton("Agregar");
        JButton btnEditar    = new JButton("Editar");
        JButton btnEliminar  = new JButton("Eliminar");
        JButton btnResetPass = new JButton("Restablecer contraseña");
        JButton btnRefrescar = new JButton("Refrescar");
        toolBar.add(btnAgregar);
        toolBar.add(btnEditar);
        toolBar.add(btnEliminar);
        toolBar.add(btnResetPass);
        toolBar.addSeparator();
        toolBar.add(btnRefrescar);
        add(toolBar, BorderLayout.NORTH);

        // Tabla usuarios
        tableModel = new DefaultTableModel(
            new Object[]{"ID","Usuario","Rol","Ejemplares Prestados"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Acciones
        btnRefrescar.addActionListener(this::onRefrescar);
        btnEliminar .addActionListener(this::onEliminar);
        btnAgregar  .addActionListener(this::onAgregar);
        btnEditar   .addActionListener(this::onEditar);
        btnResetPass.addActionListener(e -> onResetPassword());

    }
        // Carga todos los usuarios en la tabla
    private void loadData() {
        tableModel.setRowCount(0);
        List<Usuario> lista = dao.listar();
        for (Usuario u : lista) {
            tableModel.addRow(new Object[]{
                u.getId(),
                u.getUsuario(),
                u.getRol(),
                u.getEjemplares_prestados()
            });
        }
    }

    private void onRefrescar(ActionEvent e) {
        loadData();
    }
        // Elimina un usuario seleccionado de la tabla
    private void onEliminar(ActionEvent e) {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(sel, 0);
        if (JOptionPane.showConfirmDialog(this,
            "¿Eliminar el usuario seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (dao.eliminar(id)) loadData();
            else JOptionPane.showMessageDialog(this, "Error al eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        // Agrega un nuevo usuario al sistema
    private void onAgregar(ActionEvent e) {
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JComboBox<Role> cbRole = new JComboBox<>(Role.values());
        Object[] fields = {
            "Usuario:", txtUser,
            "Contraseña:", txtPass,
            "Rol:", cbRole
        };
        int opcion = JOptionPane.showConfirmDialog(this, fields, "Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            Usuario u = new Usuario();
            u.setUsuario(txtUser.getText().trim());
            u.setContrasena(new String(txtPass.getPassword()));
            u.setRol((Role) cbRole.getSelectedItem());
            if (dao.agregar(u)) loadData();
            else JOptionPane.showMessageDialog(this, "Error al agregar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        // Edita un usuario seleccionado de la tabla
    private void onEditar(ActionEvent e) {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(sel, 0);
        String user = (String) tableModel.getValueAt(sel, 1);
        Role rol = (Role) tableModel.getValueAt(sel, 2);

        JTextField txtUser = new JTextField(user);
        JPasswordField txtPass = new JPasswordField();
        JComboBox<Role> cbRole = new JComboBox<>(Role.values());
        cbRole.setSelectedItem(rol);

        Object[] fields = {
            "Usuario:", txtUser,
            "Contraseña (si cambia):", txtPass,
            "Rol:", cbRole
        };
        int opcion = JOptionPane.showConfirmDialog(this, fields, "Editar Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            Usuario u = new Usuario();
            u.setId(id);
            u.setUsuario(txtUser.getText().trim());
            String newPass = new String(txtPass.getPassword());
            u.setContrasena(newPass.isEmpty() ? dao.listar().stream()
                .filter(x->x.getId()==id).findFirst().get().getContrasena()
                : newPass);
            u.setRol((Role) cbRole.getSelectedItem());
            if (dao.actualizar(u)) loadData();
            else JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        // Restablece la contraseña de un usuario seleccionado
    private void onResetPassword() {
    int sel = table.getSelectedRow();
    if (sel < 0) {
        JOptionPane.showMessageDialog(this, "Selecciona un usuario.", "Atención", JOptionPane.WARNING_MESSAGE);
        return;
    }
    int id = (int) tableModel.getValueAt(sel, 0);
    // Se ingresa la nueva contraseña
    JPasswordField pf1 = new JPasswordField();
    JPasswordField pf2 = new JPasswordField();
    Object[] fields = {
        "Nueva contraseña:", pf1,
        "Confirmar contraseña:", pf2
    };
    int ok = JOptionPane.showConfirmDialog(this, fields, "Restablecer contraseña", JOptionPane.OK_CANCEL_OPTION);
    if (ok != JOptionPane.OK_OPTION) return;
    String pass1 = new String(pf1.getPassword()), pass2 = new String(pf2.getPassword());
    if (pass1.isEmpty() || !pass1.equals(pass2)) {
        JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden o están vacías.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    // Se invoca al UsuarioDAO
    UsuarioDAO dao = new UsuarioDAO();
    if (dao.resetearContrasena(id, pass1)) {
        JOptionPane.showMessageDialog(this, "Contraseña restablecida.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        loadData();
    } else {
        JOptionPane.showMessageDialog(this, "Error al restablecer.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

}