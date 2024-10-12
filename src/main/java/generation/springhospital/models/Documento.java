package generation.springhospital.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Documentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Google Cloud Storage necesita el nombre del archivo para la descarga correcta de este
    private String nombreArchivo;

    private String urlArchivo;

    @ManyToOne
    private  Usuario usuario;
}
