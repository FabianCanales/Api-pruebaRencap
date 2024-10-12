package generation.springhospital.services;

import com.google.api.client.util.Value;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import com.google.storage.v2.BucketName;
import generation.springhospital.models.Cita;
import generation.springhospital.models.Documento;
import generation.springhospital.models.Paciente;
import generation.springhospital.models.Usuario;
import generation.springhospital.repositories.CitaRepository;
import generation.springhospital.repositories.DocumentoRepository;
import generation.springhospital.repositories.PacienteRepository;
import generation.springhospital.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class GoogleCloudStorageService {
    @Value("${google.cloud.storage.bucket-name}")
    private String bucketName;
    private final String SERVICE_ACCOUNT_JSON_PATH = "C:\\Users\\fabia\\Documents\\Proyectos Bootcamp\\Proyectos Java\\Spring boot\\SpringBootC14";
    private final Storage storage;


    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    {
        try {
            storage = StorageOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(SERVICE_ACCOUNT_JSON_PATH)))
                    .build().getService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para subir un archivo asociado a un paciente
    public String uploadFileForUsuario(Long usuarioId, MultipartFile file) throws IOException {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        String filePath = "usuarios/" + usuarioId + "/documentos/" + file.getOriginalFilename();

        BlobId blobId = BlobId.of(bucketName, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());

        // Guardar información del archivo en la base de datos
        Documento documento = new Documento();
        documento.setNombreArchivo(file.getOriginalFilename());
        documento.setUrlArchivo(filePath);
        documento.setUsuario(usuario);
        documentoRepository.save(documento);


        return String.format("File %s uploaded to bucket %s as %s", file.getOriginalFilename(), bucketName, blobId.getName());
    }

    // Método para subir un archivo asociado a una cita
    public String uploadFileForCita(Long citaId, MultipartFile file) throws IOException {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        String filePath = "citas/" + citaId + "/documentos/" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());

        // Guardar información del archivo en la base de datos
        Documento documento = new Documento();
        documento.setNombreArchivo(file.getOriginalFilename());
        documento.setUrl(filePath);
        documento.setCita(cita);
        documentoRepository.save(documento);

        return String.format("File %s uploaded to bucket %s as %s", file.getOriginalFilename(), bucketName, blobId.getName());
    }

    // Método para descargar un archivo
    public byte[] downloadFile(String fileName) {
        Blob blob = storage.get(BlobId.of(bucketName, fileName));
        return blob.getContent();
    }

    // Método para listar archivos de un paciente
    public List<Documento> listFilesForPaciente(Long pacienteId) {
        return documentoRepository.findByPacienteId(pacienteId);
    }

    // Método para listar archivos de una cita
    public List<Documento> listFilesForCita(Long citaId) {
        return documentoRepository.findByCitaId(citaId);
    }

    // Método para eliminar un archivo
    public String deleteFile(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        boolean deleted = storage.delete(blobId);
        return deleted ? "File deleted successfully" : "File not found";
    }
}