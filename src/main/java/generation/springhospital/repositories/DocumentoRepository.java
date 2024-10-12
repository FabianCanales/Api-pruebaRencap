package generation.springhospital.repositories;

import generation.springhospital.models.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    // Declara metodo para buscar Documentos Por Id De paciente
    List<Documento> findByPacienteId(Long pacienteId);

    //Declara metodo para buscar documentos por id de la cita
    List<Documento> findByCitaId(Long doctorId);




}
