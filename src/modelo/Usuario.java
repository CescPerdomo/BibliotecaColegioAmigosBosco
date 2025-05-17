

package modelo;


 // representa un usuario del sistema con credenciales y rol

public class Usuario {
    private int id;
    private String usuario;
    private String contrasena; // Contraseña del usuario almacenada como texto plano
    private Role rol; // Rol del usuario: puede ser ADMIN, ALUMNO o PROFESOR
    private int ejemplares_prestados;

    // Constructor vacio 
    public Usuario() { }

    //Constructor con parametros de usuario 
    public Usuario(int id, String usuario, String contrasena, Role rol, int ejemplares_prestados) {
        this.id = id;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.ejemplares_prestados = ejemplares_prestados;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Role getRol() {
        return rol;
    }

    public void setRol(Role rol) {
        this.rol = rol;
    }

    public int getEjemplares_prestados() {
        return ejemplares_prestados;
    }

    public void setEjemplares_prestados(int ejemplares_prestados) {
        this.ejemplares_prestados = ejemplares_prestados;
    }

    

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", usuario='" + usuario + '\'' +
               ", rol=" + rol + ", ejemplares_prestados=" + ejemplares_prestados + '}';
    }
}







