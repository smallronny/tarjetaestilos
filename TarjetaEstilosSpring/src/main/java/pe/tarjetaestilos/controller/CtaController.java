/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.controller;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.UUID;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.time.ZonedDateTime;
import jakarta.validation.Valid;


//imports tarjetas
import pe.tarjetaestilos.models.Cta;
import pe.tarjetaestilos.models.Menu;
import pe.tarjetaestilos.services.CtaService;
import pe.tarjetaestilos.dto.cta.CtaSearchDto;
import pe.tarjetaestilos.dto.cta.CtaCreateDto;
import pe.tarjetaestilos.repositories.CtaRepository;
import pe.tarjetaestilos.repositories.MenuRepository;
import pe.tarjetaestilos.dto.ApiResponseDto;

/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos") 
public class CtaController {
    private final CtaRepository ctaRepository;
    private final MenuRepository menuRepository;

    public CtaController(
            CtaRepository ctaRepository,
            MenuRepository menuRepository
    ) {
        this.ctaRepository = ctaRepository;
        this.menuRepository = menuRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(CtaController.class);
    
    @Autowired
    private CtaService ctaService;
    
    
    @GetMapping("/web/cta/byMenu/{slug}")
    public ResponseEntity<List<Cta>> getCtaByMenuSlug(@PathVariable String slug) {
        List<Cta> cta = ctaService.getCtaByMenuSlug(slug);

        if (cta.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(cta);
    }
    
    
    @GetMapping("/cms/ctaAll") 
    public List<Cta> getCtaAll(){
        List<Cta> ctas = this.ctaService.getCtaAll();
        logger.info("registos obtenidos:");
        ctas.forEach(cta-> logger.info(cta.toString()));
        return ctas;
    }
    
    @GetMapping("/cms/cta/{page}/{size}") 
    public Page<Cta> getCta(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return ctaService.getCta(pageable);
    }
    
    @PostMapping("/cms/cta/search")
    public Page<Cta> searchCta(@RequestBody CtaSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return ctaService.search(dto.getUrl(),dto.getMenu_id(), pageable);
    }
    
    @PostMapping(value = "/cms/cta/create")
    public ResponseEntity<ApiResponseDto<Cta>> createCta(
            @Valid @RequestBody CtaCreateDto dto
    ) {
        try {
            Cta ctas = new Cta();
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            ctas.setUrl(dto.getUrl());
            ctas.setImage(dto.getImage());
            ctas.setMenu(menu);
            
            Cta savedCta = ctaService.saveCta(ctas);
            

            Cta newCta = ctaService.getCtaById(savedCta.getId());

            String successMessage = "Cta registrado con ID " + savedCta.getId();
            ApiResponseDto<Cta> response = new ApiResponseDto<>(successMessage, newCta, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar banner: " + e.getMessage();
            ApiResponseDto<Cta> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/cta/update/{id}")
    public ResponseEntity<ApiResponseDto<Cta>> updateCta(
            @PathVariable Long id,
            @Valid @RequestBody CtaCreateDto dto
    ) {
        try {
            Cta cta = ctaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cta no encontrada con id " + id));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu  no encontrada id " + dto.getMenu_id()));
            cta.setUrl(dto.getUrl());
            cta.setMenu(menu);
            
            if (dto.getImage() != null && !dto.getImage().trim().isEmpty()) {
                cta.setImage(dto.getImage());
            }
            
            Cta updatedCta = ctaService.saveCta(cta);

            String successMessage = "Cta actualizado con ID " + updatedCta.getId();
            ApiResponseDto<Cta> response = new ApiResponseDto<>(successMessage, updatedCta, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar banner: " + e.getMessage();
            ApiResponseDto<Cta> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/cta/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteCta(@PathVariable Long id) {
        try {
           Cta existingCta = ctaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cta no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingCta.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("Banner ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingCta.setDeletedAt(ZonedDateTime.now());
            ctaRepository.save(existingCta);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Cta eliminado con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar el cta: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping(value = "/cms/cta/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<String>> createImage(
        @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            Path uploadPath = Paths.get("uploads/cta/image/");
            
            Path filePath = null;
            // proceso de cardImage
            if (image != null && !image.isEmpty()) {
                String originalFilenameCard = image.getOriginalFilename();
                String extensionCard = "";
                if (originalFilenameCard != null && originalFilenameCard.contains(".")) {
                    extensionCard = originalFilenameCard.substring(originalFilenameCard.lastIndexOf("."));
                }
                String uniqueFilenameCard = System.currentTimeMillis() + "_" + UUID.randomUUID() + extensionCard;
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePathCard = uploadPath.resolve(uniqueFilenameCard);
                Files.copy(image.getInputStream(), filePathCard, StandardCopyOption.REPLACE_EXISTING);
                filePath = filePathCard;
            }
            ApiResponseDto<String> response = new ApiResponseDto<>(
                    "Imagen creada ",
                    filePath != null ? filePath.toString() : null,
                    HttpStatus.CREATED.value()
            );            
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            String errorMessage = "Error al registrar imagen: " + e.getMessage();
            ApiResponseDto<String> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}
