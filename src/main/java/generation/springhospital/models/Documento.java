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
    private String UrlArchivo;
    private String Url;
//hacer conexión entre bdd de cita y dr.
    @ManyToOne
    private  Usuario usuario;

    @ManyToOne
    private Cita cita;

    @ManyToOne
    private Doctor doctor;

    @ManyToOne
    private Paciente paciente;
}
