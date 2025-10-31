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
import pe.tarjetaestilos.services.SectionsMenusService;
import pe.tarjetaestilos.repositories.SectionsMenusRepository;
import pe.tarjetaestilos.repositories.SectionRepository;
import pe.tarjetaestilos.repositories.MenuRepository;
import pe.tarjetaestilos.dto.sectionsMenus.SectionsMenusCreateDto;
import pe.tarjetaestilos.dto.sectionsMenus.SectionsMenusSearchDto;
import pe.tarjetaestilos.models.SectionsMenus;
import pe.tarjetaestilos.models.Section;
import pe.tarjetaestilos.models.Menu;
/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos")
public class SectionsMenusController {
    
    private final SectionsMenusRepository sectionsMenusRepository;
    private final SectionRepository sectionRepository;
    private final MenuRepository menuRepository;

    public SectionsMenusController(
            SectionsMenusRepository sectionsMenusRepository,
            SectionRepository sectionRepository,
            MenuRepository menuRepository
    ) {
        this.sectionRepository = sectionRepository;
        this.menuRepository = menuRepository;
        this.sectionsMenusRepository = sectionsMenusRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(SectionController.class);
    
    
    @Autowired
    private SectionsMenusService sectionsMenusService;
    
    @GetMapping("/cms/sections-menus-all") 
    public List<SectionsMenus> getSectionsMenusAll(){
        List<SectionsMenus> sectionsMenus = this.sectionsMenusService.getSectionsMenusAll();
        logger.info("registos obtenidos:");
        sectionsMenus.forEach(section-> logger.info(section.toString()));
        return sectionsMenus;
    }
    
    @GetMapping("/cms/sections-menus/{page}/{size}") 
    public Page<SectionsMenus> getSectionsMenus(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return sectionsMenusService.getSectionsMenus(pageable);
    }
    
    
    @PostMapping(value = "/cms/sections-menus/create")
    public ResponseEntity<ApiResponseDto<SectionsMenus>> createSectionsMenus(
            @Valid @RequestBody SectionsMenusCreateDto dto
    ) {
        try {
            SectionsMenus sectionsMenus = new SectionsMenus();
            Section section = sectionRepository.findById(dto.getSection_id())
                    .orElseThrow(() -> new RuntimeException("Sección no encontrada id " + dto.getSection_id()));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrada id " + dto.getMenu_id()));
            
            sectionsMenus.setSection(section);
            sectionsMenus.setMenu(menu);
            
            SectionsMenus savedSectionsMenus = sectionsMenusService.saveSectionsMenus(sectionsMenus);
            

            SectionsMenus newSectionsMenus = sectionsMenusService.getSectionsMenusById(savedSectionsMenus.getId());

            String successMessage = "Sección registrada con ID " + savedSectionsMenus.getId();
            ApiResponseDto<SectionsMenus> response = new ApiResponseDto<>(successMessage, newSectionsMenus, HttpStatus.CREATED.value());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar sección: " + e.getMessage();
            ApiResponseDto<SectionsMenus> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    
    @PutMapping(value = "/cms/sections-menus/update/{id}")
    public ResponseEntity<ApiResponseDto<SectionsMenus>> updateSection(
            @PathVariable Long id,
            @Valid @RequestBody SectionsMenusCreateDto dto
    ) {
        try {
            SectionsMenus sectionsMenus = sectionsMenusRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Sección menu no encontrada con id " + id));

            
            Section section = sectionRepository.findById(dto.getSection_id())
                    .orElseThrow(() -> new RuntimeException("Sección no encontrada id " + dto.getSection_id()));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrada id " + dto.getMenu_id()));
            
            sectionsMenus.setSection(section);
            sectionsMenus.setMenu(menu);
            

            SectionsMenus updatedSectionsMenus = sectionsMenusService.saveSectionsMenus(sectionsMenus);

            String successMessage = "Sección menu actualizada con ID " + updatedSectionsMenus.getId();
            ApiResponseDto<SectionsMenus> response = new ApiResponseDto<>(successMessage, updatedSectionsMenus, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar promoción: " + e.getMessage();
            ApiResponseDto<SectionsMenus> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/sections-menus/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteSectionsMenus(@PathVariable Long id) {
        try {
           SectionsMenus existingSectionsMenus = sectionsMenusRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Sección menu no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingSectionsMenus.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("La sección menu ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingSectionsMenus.setDeletedAt(ZonedDateTime.now());
            sectionsMenusRepository.save(existingSectionsMenus);

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
