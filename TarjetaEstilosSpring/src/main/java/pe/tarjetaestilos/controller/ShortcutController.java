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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.time.ZonedDateTime;


import pe.tarjetaestilos.models.Shortcut;
import pe.tarjetaestilos.models.Menu;
import pe.tarjetaestilos.services.ShortcutService;
import pe.tarjetaestilos.dto.shorcut.ShortcutSearchDto;
import pe.tarjetaestilos.dto.shorcut.ShortcutCreateDto;
import pe.tarjetaestilos.repositories.ShortcutRepository;
import pe.tarjetaestilos.repositories.MenuRepository;
import pe.tarjetaestilos.dto.ApiResponseDto;
/**
 *
 * @author julito
 */
@RestController
@RequestMapping("tarjeta-estilos") //http://localhost:8081/tarjeta-estilos
@CrossOrigin(value="http://localhost:3000") //puerto de vue
public class ShortcutController {
    private final ShortcutRepository shortcutRepository;
    private final MenuRepository menuRepository;
    
    public ShortcutController(
            ShortcutRepository shortcutRepository,
            MenuRepository menuRepository
    ) {
        this.shortcutRepository = shortcutRepository;
        this.menuRepository = menuRepository;
    }
    private static final Logger logger = LoggerFactory.getLogger(ShortcutController.class);
    
    @Autowired
    private ShortcutService shortcutService;
    
    
    @GetMapping("/web/shortcut/byMenu/{slug}")
    public ResponseEntity<List<Shortcut>> getPageContentByMenuSlug(@PathVariable String slug) {
        List<Shortcut> shortcut = shortcutService.getShortcutByMenuSlug(slug);

        if (shortcut.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(shortcut);
    }
    
    
    @GetMapping("/cms/shortcutAll") 
    public List<Shortcut> getDepartmentsAll(){
        List<Shortcut> shortcuts = this.shortcutService.getShortcutAll();
        logger.info("registos obtenidos:");
        shortcuts.forEach(shortcut-> logger.info(shortcut.toString()));
        return shortcuts;
    }
    
    @GetMapping("/cms/shortcuts/{page}/{size}") 
    public Page<Shortcut> getShortcuts(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return shortcutService.getShortcut(pageable);
    }
    
    @PostMapping("/cms/shortcuts/search")
    public Page<Shortcut> searchShortcuts(@RequestBody ShortcutSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return shortcutService.search(dto.getTitle(), pageable);
    }
    
    @PostMapping(value = "/cms/shortcuts/create")
    public ResponseEntity<ApiResponseDto<Shortcut>> createFrequentlyAskedQuestion(
            @Valid @RequestBody ShortcutCreateDto dto
    ) {
        try {
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Atajo no encontrado id " + dto.getMenu_id()));
            
            Shortcut shortcut = new Shortcut();
            shortcut.setIcon(dto.getIcon());
            shortcut.setTitle(dto.getTitle());
            shortcut.setMenu(menu);
            
            
            Shortcut savedShortcut = shortcutService.saveShortcut(shortcut);                        
            Shortcut newShortcut = shortcutService.getShortcutById(savedShortcut.getId());
            
            String successMessage = "Atajo registrada con ID " + savedShortcut.getId();
            ApiResponseDto<Shortcut> response = new ApiResponseDto<>(successMessage, newShortcut, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            String errorMessage = "Error al registrar noticia: " + e.getMessage();
            ApiResponseDto<Shortcut> response = new ApiResponseDto<>(errorMessage, null,HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/shortcuts/update/{id}")
    public ResponseEntity<ApiResponseDto<Shortcut>> updateFacility(
            @PathVariable Long id,
            @Valid @RequestBody ShortcutCreateDto dto
    ) {
        try {
            Shortcut existingShortcut = shortcutRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Atajo no encontrada con id " + id));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            
            existingShortcut.setIcon(dto.getIcon());
            existingShortcut.setTitle(dto.getTitle());
            existingShortcut.setMenu(menu);
                     
            Shortcut updatedShortcut = shortcutService.saveShortcut(existingShortcut);

            String successMessage = "Atajo actualizada con ID " + updatedShortcut.getId();
            ApiResponseDto<Shortcut> response = new ApiResponseDto<>(successMessage, updatedShortcut, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar atajo: " + e.getMessage();
            ApiResponseDto<Shortcut> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/shortcuts/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteShortcut(@PathVariable Long id) {
        try {
            Shortcut existingShortcut = shortcutRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Atajo no encontrada con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingShortcut.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("El atajo ya fue eliminada", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingShortcut.setDeletedAt(ZonedDateTime.now());
            shortcutRepository.save(existingShortcut);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Atajo eliminado con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar atajo: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}
