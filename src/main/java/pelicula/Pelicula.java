package pelicula;

import jakarta.persistence.*;
import lombok.*;
import copiaPelicula.CopiaPelicula;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="peliculas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "copias") // Excluir la colección para evitar problemas de recursión y lazy loading
public class Pelicula implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pelicula")
    private Integer id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "genero")
    private String genero;

    @Column(name = "año")
    private Integer año;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "director")
    private String director;

    @OneToMany(
            mappedBy = "pelicula", // Nombre del campo en la clase CopiaPelicula
            cascade = CascadeType.ALL, // Las operaciones de persistencia se propagan
            fetch = FetchType.LAZY // Carga perezosa (solo se carga si se accede a ella)
    )
    private Set<CopiaPelicula> copias; // Cambiado a una colección y nombre más descriptivo

    private String image_url;

    @Override
    public String toString() {
        // IMPORTANTE: Nunca incluir colecciones LAZY en toString() para evitar LazyInitializationException
        return "Pelicula{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", genero='" + genero + '\'' +
                ", año=" + año +
                ", descripcion='" + descripcion + '\'' +
                ", director='" + director + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }
}
