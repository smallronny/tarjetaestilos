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
import pe.tarjetaestilos.services.MenuTypesService;
import pe.tarjetaestilos.repositories.MenuTypesRepository;
import pe.tarjetaestilos.repositories.SectionTypeRepository;
import pe.tarjetaestilos.repositories.MenuRepository;
import pe.tarjetaestilos.dto.menuType.MenuTypeCreateDto;
import pe.tarjetaestilos.dto.menuType.MenuTypeSearchDto;
import pe.tarjetaestilos.models.MenuType;
import pe.tarjetaestilos.models.SectionType;
import pe.tarjetaestilos.models.Menu;
/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos")
public class MenuTypeController {
    
    private final MenuTypesRepository menuTypesRepository;
    private final SectionTypeRepository sectionTypeRepository;
    private final MenuRepository menuRepository;

    public MenuTypeController(
            MenuTypesRepository menuTypesRepository,
            SectionTypeRepository sectionTypeRepository,
            MenuRepository menuRepository
    ) {
        this.menuTypesRepository = menuTypesRepository;
        this.menuRepository = menuRepository;
        this.sectionTypeRepository = sectionTypeRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(MenuTypeController.class);
    
    @Autowired
    private MenuTypesService menuTypesService;
    
    @GetMapping("/cms/menu-type-all") 
    public List<MenuType> getSectionsMenusAll(){
        List<MenuType> menuType = this.menuTypesService.getMenuTypeAll();
        logger.info("registos obtenidos:");
        menuType.forEach(menuTyp-> logger.info(menuTyp.toString()));
        return menuType;
    }
    
    @GetMapping("/cms/menu-type/{page}/{size}") 
    public Page<MenuType> getMenuType(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return menuTypesService.getMenuType(pageable);
    }
    
    
    @PostMapping(value = "/cms/menu-type/create")
    public ResponseEntity<ApiResponseDto<MenuType>> createMenuType(
            @Valid @RequestBody MenuTypeCreateDto dto
    ) {
        try {
            MenuType menuType = new MenuType();
            SectionType sectionType = sectionTypeRepository.findById(dto.getSection_type_id())
                    .orElseThrow(() -> new RuntimeException("Tipo Sección no encontrada id " + dto.getSection_type_id()));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrada id " + dto.getMenu_id()));
            
            menuType.setSectionType(sectionType);
            menuType.setMenu(menu);
            
            MenuType savedMenuType = menuTypesService.saveMenuType(menuType);
            

            MenuType newMenuType = menuTypesService.getMenuTypeById(savedMenuType.getId());

            String successMessage = "Tipo Sección registrada con ID " + savedMenuType.getId();
            ApiResponseDto<MenuType> response = new ApiResponseDto<>(successMessage, newMenuType, HttpStatus.CREATED.value());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar tipo sección: " + e.getMessage();
            ApiResponseDto<MenuType> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    
    @PutMapping(value = "/cms/menu-type/update/{id}")
    public ResponseEntity<ApiResponseDto<MenuType>> updateMenuType(
            @PathVariable Long id,
            @Valid @RequestBody MenuTypeCreateDto dto
    ) {
        try {
            MenuType menuType = menuTypesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("tipo Sección menu no encontrada con id " + id));

            
            SectionType sectionType = sectionTypeRepository.findById(dto.getSection_type_id())
                    .orElseThrow(() -> new RuntimeException("tipo Sección no encontrada id " + dto.getSection_type_id()));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrada id " + dto.getMenu_id()));
            
            menuType.setSectionType(sectionType);
            menuType.setMenu(menu);
            

            MenuType updatedMenuType = menuTypesService.saveMenuType(menuType);

            String successMessage = "Sección menu actualizada con ID " + updatedMenuType.getId();
            ApiResponseDto<MenuType> response = new ApiResponseDto<>(successMessage, updatedMenuType, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar promoción: " + e.getMessage();
            ApiResponseDto<MenuType> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/menu-type/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteMenuType(@PathVariable Long id) {
        try {
           MenuType existingMenuType = menuTypesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tipo de menu no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingMenuType.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("El tipo de menu ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingMenuType.setDeletedAt(ZonedDateTime.now());
            menuTypesRepository.save(existingMenuType);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Tipo de menu eliminado con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar la tipo de menu: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}
