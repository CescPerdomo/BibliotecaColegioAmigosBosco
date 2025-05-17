
package modelo;

import java.sql.Timestamp;
import java.time.temporal.Temporal;


 //Entidad Prestamo muestra la tabla 'prestamo' en BD
    // Almacena informacion sobre que usuario presto que material
        // fecha su estado y cuando debe devolverlo
 
public class Prestamo {
    private int id; // Identificador unico del prestamo (PK)
    private int idMaterial; // ID del material que se esta prestando
    private String usuarioPresta;
    private Timestamp fechaPrestamo;
    private Timestamp fechaDevolucion;
    private String estado;// “ACTIVO” o “DEVUELTO” y otro ESTADO
    private String usuario;
    private String tipoMaterial;

    // Se dejo Constructor vacio para poder usar new Prestamo()
    public Prestamo() {
    }

    // Constructor parametrizado con todos los campos (excepto tipoMaterial)
    public Prestamo(int id, int idMaterial, String usuarioPresta,
                    Timestamp fechaPrestamo, Timestamp fechaDevolucion,
                    String estado) {
        this.id = id;
        this.idMaterial = idMaterial;
        this.usuarioPresta = usuarioPresta;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdMaterial() { return idMaterial; }
    public void setIdMaterial(int idMaterial) { this.idMaterial = idMaterial; }
    
   

    public void setUsuario(String usuario) {
    this.usuario = usuario;
    }

    public String getUsuario() {
    return usuario;
    }
    
    public String getTipoMaterial() {
        return tipoMaterial;
    }
    
    public void setTipoMaterial(String tipoMAterial) {
        this.tipoMaterial = tipoMaterial;
    }
    

    public String getUsuarioPresta() { return usuarioPresta; }
    public void setUsuarioPresta(String usuarioPresta) { this.usuarioPresta = usuarioPresta; }

    public Timestamp getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(Timestamp fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public Timestamp getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(Timestamp fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "["+id+"] mat="+idMaterial+" user="+usuarioPresta+
               " pre="+fechaPrestamo+" dev="+fechaDevolucion+
               " est="+estado;
    }
        // Metodo no implementado se dejo ya que presentaba errores si se eliminaba 
    public Temporal now() {
        throw new UnsupportedOperationException(""); 
    }

    
}
