package pe.dquispe.myappfinal.models;

import java.util.ArrayList;
import java.util.List;

public class Mascota {
    private Long id;
    private  String nombre,raza,imagen;
    private Integer edad;

    private List<Usuario> usuario_id=new ArrayList<>();

    public Mascota() {
    }

    public List<Usuario> getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(List<Usuario> usuario_id) {
        this.usuario_id = usuario_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    @Override
    public String toString() {
        return "Mascota{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", raza='" + raza + '\'' +
                ", imagen='" + imagen + '\'' +
                ", edad=" + edad +
                ", usuario_id=" + usuario_id +
                '}';
    }
}
