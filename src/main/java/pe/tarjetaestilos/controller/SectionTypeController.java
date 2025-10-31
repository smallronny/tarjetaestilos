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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import pe.tarjetaestilos.services.SectionTypeService;
import org.springframework.data.domain.Sort;
import pe.tarjetaestilos.repositories.SectionTypeRepository;

import org.springframework.http.MediaType;
import pe.tarjetaestilos.dto.ApiResponseDto;
import pe.tarjetaestilos.dto.sectionType.SectionTypeCreateDto;
import pe.tarjetaestilos.dto.sectionType.SectionTypeSearchDto;
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
import pe.tarjetaestilos.models.SectionType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.time.ZonedDateTime;
import jakarta.validation.Valid;
/**
 *
 * @author julito
 */
@RestController
@RequestMapping("tarjeta-estilos")
public class SectionTypeController {
    private final SectionTypeRepository sectionTypeRepository;

    public SectionTypeController(
            SectionTypeRepository sectionTypeRepository
    ) {
        this.sectionTypeRepository = sectionTypeRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(SectionTypeController.class);
    
    @Autowired
    private SectionTypeService sectionTypeService;
    
    @GetMapping("/cms/section-type-all") 
    public List<SectionType> getSectionTypeAll(){
        List<SectionType> sectionTypes = this.sectionTypeService.getSectionTypeAll();
        logger.info("registos obtenidos:");
        sectionTypes.forEach(sectionType-> logger.info(sectionType.toString()));
        return sectionTypes;
    }
    
    
    @GetMapping("/web/section-type-all") 
    public List<SectionType> getWebSectionTypeAll(){
        List<SectionType> sectionTypes = this.sectionTypeService.getSectionTypeAll();
        logger.info("registos obtenidos:");
        sectionTypes.forEach(sectionType-> logger.info(sectionType.toString()));
        return sectionTypes;
    }
    
    
    @GetMapping("/cms/section-type/{page}/{size}") 
    public Page<SectionType> getSectionTypes(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return sectionTypeService.getSectionType(pageable);
    }
    
    @PostMapping("/cms/section-type/search")
    public Page<SectionType> searchPromotions(@RequestBody SectionTypeSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return sectionTypeService.search(dto.getTitle(), pageable);
    }
    
    @PostMapping(value = "/cms/section-type/create")
    public ResponseEntity<ApiResponseDto<SectionType>> createSectionType(
            @Valid @RequestBody SectionTypeCreateDto dto
    ) {
        try {
            SectionType sectionType = new SectionType();            
            sectionType.setTitle(dto.getTitle());
            sectionType.setLogo(dto.getLogo());
            
            SectionType savedSectionType = sectionTypeService.saveSectionType(sectionType);
            

            SectionType newSectionType = sectionTypeService.getSectionTypeById(savedSectionType.getId());

            String successMessage = "Promoción registrada con ID " + savedSectionType.getId();
            ApiResponseDto<SectionType> response = new ApiResponseDto<>(successMessage, newSectionType, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar noticia: " + e.getMessage();
            ApiResponseDto<SectionType> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/section-type/update/{id}")
    public ResponseEntity<ApiResponseDto<SectionType>> updateSectionType(
            @PathVariable Long id,
            @Valid @RequestBody SectionTypeCreateDto dto
    ) {
        try {
            SectionType sectionType = sectionTypeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tipo de sección no encontrada con id " + id));

            sectionType.setTitle(dto.getTitle());

            if (dto.getLogo() != null && !dto.getLogo().trim().isEmpty()) {
                sectionType.setLogo(dto.getLogo());
            }
            

            SectionType updatedSectionType = sectionTypeService.saveSectionType(sectionType);

            String successMessage = "Tipo de sección actualizada con ID " + updatedSectionType.getId();
            ApiResponseDto<SectionType> response = new ApiResponseDto<>(successMessage, updatedSectionType, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar el tipo de sección: " + e.getMessage();
            ApiResponseDto<SectionType> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/section-type/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteSectionType(@PathVariable Long id) {
        try {
           SectionType existingSectionType = sectionTypeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tipo de sección no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingSectionType.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("Tipo de sección ya fue eliminada", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingSectionType.setDeletedAt(ZonedDateTime.now());
            sectionTypeRepository.save(existingSectionType);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Tipo de sección eliminada con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar tipo de sección: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping(value = "/cms/section-type/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<String>> createImage(
        @RequestParam(value = "logo", required = false) MultipartFile logo
    ) {
        try {
            Path uploadPath = Paths.get("uploads/section-type/logo/");
            
            Path filePath = null;
            // proceso de cardImage
            if (logo != null && !logo.isEmpty()) {
                String originalFilenameCard = logo.getOriginalFilename();
                String extensionCard = "";
                if (originalFilenameCard != null && originalFilenameCard.contains(".")) {
                    extensionCard = originalFilenameCard.substring(originalFilenameCard.lastIndexOf("."));
                }
                String uniqueFilenameCard = System.currentTimeMillis() + "_" + UUID.randomUUID() + extensionCard;
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePathCard = uploadPath.resolve(uniqueFilenameCard);
                Files.copy(logo.getInputStream(), filePathCard, StandardCopyOption.REPLACE_EXISTING);
                filePath = filePathCard;
            }
            ApiResponseDto<String> response = new ApiResponseDto<>(
                    "Logo creado ",
                    filePath != null ? filePath.toString() : null,
                    HttpStatus.CREATED.value()
            );            
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            String errorMessage = "Error al registrar la imagen: " + e.getMessage();
            ApiResponseDto<String> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
