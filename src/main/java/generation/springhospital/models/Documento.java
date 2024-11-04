package generation.springhospital.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty ("_id")
    private Long id;
    // Google Cloud Storage necesita el nombre del archivo para la descarga correcta de este
    private String nombreArchivo;
    private String UrlArchivo;

//hacer conexión entre bdd de agendamiento y dr.
    @ManyToOne
    private  Usuario usuario;

    @ManyToOne
    private Agendamiento agendamiento;

    @ManyToOne
    private Doctor doctor;

    @ManyToOne
    private Paciente paciente;
}
