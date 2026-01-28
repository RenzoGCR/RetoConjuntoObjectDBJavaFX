package copiaPelicula;
import jakarta.persistence.*;
import lombok.*;
import pelicula.Pelicula;
import user.User;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"pelicula", "usuario"})
@Entity
// @Table es opcional en ObjectDB, pero puedes dejarlo por compatibilidad
public class CopiaPelicula implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ObjectDB gestiona las relaciones de forma nativa como punteros a objetos
    @ManyToOne(fetch = FetchType.EAGER)
    private Pelicula pelicula;

    @OneToOne(fetch = FetchType.LAZY)
    private User usuario;

    private String estado;
    private String soporte;

    @Override
    public String toString() {
        return "CopiaPelicula{" +
                "id=" + id +
                ", estado='" + estado + '\'' +
                ", soporte='" + soporte + '\'' +
                '}';
    }
}