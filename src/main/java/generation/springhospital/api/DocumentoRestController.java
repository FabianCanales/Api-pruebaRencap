package generation.springhospital.api;

import generation.springhospital.models.Documento;
import generation.springhospital.services.GoogleCloudStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin("*")
public class DocumentoRestController {

    @Autowired
    private GoogleCloudStorageService storageService;

    // Subir archivo para un usuario
    @PostMapping("/upload/usuario/{usuarioId}")
    public String uploadFileForUsuario(@PathVariable Long usuarioId, @RequestParam("file") MultipartFile file) throws IOException {
        return storageService.uploadFileForUsuario(usuarioId, file);
    }

    // Subir archivo para una agendamiento
    @PostMapping("/upload/agendamiento/{agendamientoId}")
    public String uploadFileForAgendamiento(@PathVariable Long agendamientoId, @RequestParam("file") MultipartFile file) throws IOException {
        return storageService.uploadFileForAgendamiento(agendamientoId, file);
    }

    // Listar archivos para un usuario
    @GetMapping("/list/usuario/{usuarioId}")
    public List<Documento> listFilesForUsuario(@PathVariable Long usuarioId) {
        return storageService.listFilesForUsuario(usuarioId);
    }

    // Listar archivos para una agendamiento
    @GetMapping("/list/agendamiento/{agendamientoId}")
    public List<Documento> listFilesForAgendamiento(@PathVariable Long agendamientoId) {
        return storageService.listFilesForAgendamiento(agendamientoId);
    }

    /*
    // Descargar archivo
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        byte[] data = storageService.downloadFile(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }*/
  //Descarga un archivo  por un usuario
    @GetMapping("/download/{usuarioId}/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long usuarioId, @PathVariable String fileName) {
        String fullPath = "usuario/" + usuarioId + "/documento/" + fileName;
        System.out.println(fullPath);
        byte[] data = storageService.downloadFile(fullPath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    // Eliminar archivo
    @DeleteMapping("/delete/{usuarioId}/{fileName}")
    public String deleteFile(@PathVariable String fileName, @PathVariable Long usuarioId) {

        String fullPath = "usuario/" + usuarioId + "/documento/" + fileName;
        return storageService.deleteFile(fullPath);
    }
}

