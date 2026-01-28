package user;

import jakarta.persistence.*;
import lombok.*;
import copiaPelicula.CopiaPelicula;

import java.io.Serializable;

/**
 * Entidad User adaptada para ObjectDB.
 * En bases de datos de objetos, las anotaciones @Table y @Column son opcionales,
 * pero las mantenemos si quieres conservar compatibilidad o legibilidad.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "copiaAsignada")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ObjectDB indexará este campo automáticamente si se usa mucho en búsquedas
    private String nombreUsuario;

    private String contraseña;

    private boolean isAdmin;

    /**
     * Relación uno a uno con CopiaPelicula.
     * mappedBy indica que el dueño de la relación es el campo 'usuario' en CopiaPelicula.
     */
    @OneToOne(
            mappedBy = "usuario",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private CopiaPelicula copiaAsignada;

    @Override
    public String toString() {
        // Evitamos volcar 'copiaAsignada' para prevenir LazyInitializationException fuera del EntityManager
        return "User{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}