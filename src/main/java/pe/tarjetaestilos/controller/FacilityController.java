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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.tarjetaestilos.models.Facility;
import pe.tarjetaestilos.models.Menu;
import pe.tarjetaestilos.services.FacilityService;
import pe.tarjetaestilos.dto.facility.FacilitySearchDto;
import pe.tarjetaestilos.dto.facility.FacilityCreateDto;
import org.springframework.data.domain.Sort;
import pe.tarjetaestilos.dto.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import org.springframework.web.bind.annotation.PutMapping;
import pe.tarjetaestilos.repositories.FacilityRepository;
import pe.tarjetaestilos.repositories.MenuRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import jakarta.validation.Valid;
/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos") //http://localhost:8081/tarjeta-estilos
@CrossOrigin(value="http://localhost:3000") //puerto de vue
public class FacilityController {
    private final FacilityRepository facilityRepository;
    private final MenuRepository menuRepository;
    
    public FacilityController(
            FacilityRepository facilityRepository,
            MenuRepository menuRepository
    ) {
        this.facilityRepository = facilityRepository;
        this.menuRepository = menuRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(FacilityController.class);
    
    @Autowired
    private FacilityService facilityService;
    
    
    @GetMapping("/web/facility/byMenu/{slug}")
    public ResponseEntity<List<Facility>> getFacilityByMenuSlug(@PathVariable String slug) {
        List<Facility> facility = facilityService.getFacilityByMenuSlug(slug);

        if (facility.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(facility);
    }
    
    
    @GetMapping("/cms/facility/{page}/{size}") //http://localhost:8081/tarjeta-estilos/facility
    public Page<Facility> getFacility(
        @PathVariable int page,
        @PathVariable int size
    ){
        Pageable pageable = PageRequest.of((page-1), size);
        return facilityService.getFacility(pageable);
    }
    
    @GetMapping("/cms/facilityAll") 
    public List<Facility> getFacilityAll(){
        List<Facility> facilities = this.facilityService.getFacilityAll();
        logger.info("registos obtenidos:");
        facilities.forEach(promotion-> logger.info(facilities.toString()));
        return facilities;
    }
    
    @PostMapping("/cms/facility/search")
    public Page<Facility> searchFacilities(@RequestBody FacilitySearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );
        return facilityService.search(dto.getTitle(), pageable);
    }
    
    @PostMapping(value = "/cms/facility/create")
    public ResponseEntity<ApiResponseDto<Facility>> createFacility(
            @Valid @RequestBody FacilityCreateDto dto
    ) {
        try {
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            
            Facility facility = new Facility();
            facility.setTitle(dto.getTitle());
            facility.setLabel(dto.getLabel());
            facility.setButtonLabel(dto.getButton_label());
            facility.setImage(dto.getImage());
            facility.setMenu(menu);
            Facility savedFacility = facilityService.saveFacility(facility);                        
            Facility newFacility = facilityService.getFacilityById(savedFacility.getId());
            
            String successMessage = "Noticia registrada con ID " + savedFacility.getId();
            ApiResponseDto<Facility> response = new ApiResponseDto<>(successMessage, newFacility, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            String errorMessage = "Error al registrar noticia: " + e.getMessage();
            ApiResponseDto<Facility> response = new ApiResponseDto<>(errorMessage, null,HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/facility/update/{id}")
    public ResponseEntity<ApiResponseDto<Facility>> updateFacility(
            @PathVariable Long id,
            @Valid @RequestBody FacilityCreateDto dto
    ) {
        try {
            Facility existingFacility = facilityRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Facilidad no encontrada con id " + id));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            
            existingFacility.setTitle(dto.getTitle());
            existingFacility.setLabel(dto.getLabel());
            existingFacility.setButtonLabel(dto.getButton_label());
            existingFacility.setImage(dto.getImage());
            existingFacility.setMenu(menu);
                     
            Facility updatedFacility = facilityService.saveFacility(existingFacility);

            String successMessage = "Facilidad actualizada con ID " + updatedFacility.getId();
            ApiResponseDto<Facility> response = new ApiResponseDto<>(successMessage, updatedFacility, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar facilidad: " + e.getMessage();
            ApiResponseDto<Facility> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/facility/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteFacility(@PathVariable Long id) {
        try {
            Facility existingFacility = facilityRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Facilidad no encontrada con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingFacility.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("La facilidad ya fue eliminada", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingFacility.setDeletedAt(ZonedDateTime.now());
            facilityRepository.save(existingFacility);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Facilidad eliminada con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar facilidad: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping(value = "/cms/facility/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<String>> createImage(
        @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            Path uploadPath = Paths.get("uploads/facility/image/");
            
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
