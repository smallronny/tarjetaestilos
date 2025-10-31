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
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.time.ZonedDateTime;
import jakarta.validation.Valid;

//import de proyecto
import pe.tarjetaestilos.dto.ApiResponseDto;
import pe.tarjetaestilos.services.SectionService;
import pe.tarjetaestilos.repositories.SectionRepository;
import pe.tarjetaestilos.repositories.SectionTypeRepository;
import pe.tarjetaestilos.dto.section.SectionCreateDto;
import pe.tarjetaestilos.dto.section.SectionSearchDto;
import pe.tarjetaestilos.models.Section;
import pe.tarjetaestilos.models.SectionType;
/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos")
public class SectionController {
    private final SectionRepository sectionRepository;
    private final SectionTypeRepository sectionTypeRepository;

    public SectionController(
            SectionRepository sectionRepository,
            SectionTypeRepository sectionTypeRepository
    ) {
        this.sectionRepository = sectionRepository;
        this.sectionTypeRepository = sectionTypeRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(SectionController.class);
    
    
    @Autowired
    private SectionService sectionService;
    
    @GetMapping("/cms/section-all") 
    public List<Section> getSectionAll(){
        List<Section> sections = this.sectionService.getSectionAll();
        logger.info("registos obtenidos:");
        sections.forEach(section-> logger.info(section.toString()));
        return sections;
    }
    
    @GetMapping("/web/section-all") 
    public List<Section> getWebSectionAll(){
        List<Section> sections = this.sectionService.getSectionAll();
        logger.info("registos obtenidos:");
        sections.forEach(section-> logger.info(section.toString()));
        return sections;
    }
    
    @GetMapping("/cms/section/{page}/{size}") 
    public Page<Section> getSection(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return sectionService.getSection(pageable);
    }
    
    @PostMapping("/cms/section/search")
    public Page<Section> searchSections(@RequestBody SectionSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return sectionService.search(dto.getTitle(),dto.getSection_type_id(), pageable);
    }
    
    @PostMapping(value = "/cms/section/create")
    public ResponseEntity<ApiResponseDto<Section>> createSection(
            @Valid @RequestBody SectionCreateDto dto
    ) {
        try {
            Section section = new Section();
            SectionType sectionType = sectionTypeRepository.findById(dto.getSection_type_id())
                    .orElseThrow(() -> new RuntimeException("Tipo de sección no encontrada id " + dto.getSection_type_id()));
            section.setTitle(dto.getTitle());
            section.setSectionType(sectionType);
            Section savedSection = sectionService.saveSection(section);
            

            Section newSection = sectionService.getSectionById(savedSection.getId());

            String successMessage = "Sección registrada con ID " + savedSection.getId();
            ApiResponseDto<Section> response = new ApiResponseDto<>(successMessage, newSection, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar sección: " + e.getMessage();
            ApiResponseDto<Section> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    
    @PutMapping(value = "/cms/section/update/{id}")
    public ResponseEntity<ApiResponseDto<Section>> updateSection(
            @PathVariable Long id,
            @Valid @RequestBody SectionCreateDto dto
    ) {
        try {
            Section section = sectionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Sección no encontrada con id " + id));

            
            SectionType sectionType = sectionTypeRepository.findById(dto.getSection_type_id())
                    .orElseThrow(() -> new RuntimeException("Tipo de sección no encontrada id " + dto.getSection_type_id()));
            
            section.setTitle(dto.getTitle());
            section.setSectionType(sectionType);
            

            Section updatedSection = sectionService.saveSection(section);

            String successMessage = "Promoción actualizada con ID " + updatedSection.getId();
            ApiResponseDto<Section> response = new ApiResponseDto<>(successMessage, updatedSection, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar promoción: " + e.getMessage();
            ApiResponseDto<Section> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/section/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteSection(@PathVariable Long id) {
        try {
           Section existingSection = sectionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Sección no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingSection.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("La sección ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingSection.setDeletedAt(ZonedDateTime.now());
            sectionRepository.save(existingSection);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Sección eliminada con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar la sección: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}
