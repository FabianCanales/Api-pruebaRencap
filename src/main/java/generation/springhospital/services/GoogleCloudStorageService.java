package generation.springhospital.services;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import generation.springhospital.models.Agendamiento;
import generation.springhospital.models.Documento;
import generation.springhospital.models.Paciente;
import generation.springhospital.models.Usuario;
import generation.springhospital.repositories.AgendamientoRepository;
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
    // @Value("${google.cloud.storage.bucket-name}")
    private String bucketName = "example_bucket_renca";
    private final String SERVICE_ACCOUNT_JSON_PATH = "C:\\Users\\Lenovo\\Downloads\\careful-muse-438313-k1-10a632007159.json";
    private final Storage storage;
    // private final String GOOGLE_APPLICATION_CREDENTIALS = "C:\\Users\\p\\Desktop\\prueba\\analog-oven-438313-i2-b3b195ffa242.json";


    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private AgendamientoRepository agendamientoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    {
        try {
            // System.out.println("Aca empieza el codigo");
            storage = StorageOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(SERVICE_ACCOUNT_JSON_PATH)))
                    .build().getService();
            // System.out.println("Aca termina :) ");
            // System.out.println(bucketName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para subir un archivo asociado a un usuario
    public String uploadFileForUsuario(Long usuarioId, MultipartFile file) throws IOException {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

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
//Metodo para subir archivo asociado al paciente
    public String uploadFileForPaciente(Long pacienteId, MultipartFile file) throws IOException {

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        String filePath = "paciente/" + pacienteId + "/documento/" + file.getOriginalFilename();

        BlobId blobId = BlobId.of(bucketName, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());

        // Guardar información del archivo en la base de datos
        Documento documento = new Documento();
        documento.setNombreArchivo(file.getOriginalFilename());
        documento.setUrlArchivo(filePath);
        documento.setPaciente(paciente);
        documentoRepository.save(documento);


        return String.format("File %s uploaded to bucket %s as %s", file.getOriginalFilename(), bucketName, blobId.getName());
    }

    // Método para subir un archivo asociado a una agendamiento
    public String uploadFileForAgendamiento(Long agendamientoId, MultipartFile file) throws IOException {
        Agendamiento agendamiento = agendamientoRepository.findById(agendamientoId)
                .orElseThrow(() -> new RuntimeException("agendamiento no encontrada"));

        String filePath = "agendamientos/" + agendamientoId + "/documentos/" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());

        // Guardar información del archivo en la base de datos
        Documento documento = new Documento();
        documento.setNombreArchivo(file.getOriginalFilename());
        documento.setUrlArchivo(filePath);
        documento.setAgendamiento(agendamiento);
        documentoRepository.save(documento);

        System.out.println(blobId);

        return String.format("File %s uploaded to bucket %s as %s", file.getOriginalFilename(), bucketName, blobId.getName());
    }

    // Método para descargar un archivo
    public byte[] downloadFile(String fileName) {
        Blob blob = storage.get(BlobId.of(bucketName, fileName));
        System.out.println("Aqui llega el cod" + blob.exists());
        return blob.getContent();
    }

    // Método para listar archivos de un paciente
    public List<Documento> listFilesForPaciente(Long pacienteId) {
        return documentoRepository.findByPacienteId(pacienteId);
    }

    // Método para listar archivos de una agendamiento
    public List<Documento> listFilesForAgendamiento(Long agendamientoId) {
        return documentoRepository.findByAgendamientoId(agendamientoId);
    }

    // Método para eliminar un archivo
    public String deleteFile(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        boolean deleted = storage.delete(blobId);
        return deleted ? "File deleted successfully" : "File not found";
    }
}