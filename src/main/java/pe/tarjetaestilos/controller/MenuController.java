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
import java.util.Optional;
/**
 *
 * @author julito
 */

//import de proyecto
import pe.tarjetaestilos.dto.ApiResponseDto;
import pe.tarjetaestilos.services.MenuService;
import pe.tarjetaestilos.repositories.SectionsMenusRepository;
import pe.tarjetaestilos.repositories.SectionRepository;
import pe.tarjetaestilos.repositories.SectionTypeRepository;
import pe.tarjetaestilos.repositories.MenuTypesRepository;

import pe.tarjetaestilos.repositories.MenuRepository;
import pe.tarjetaestilos.dto.menu.MenuCreateDto;
import pe.tarjetaestilos.dto.menu.MenuSearchDto;
import pe.tarjetaestilos.models.Menu;
import pe.tarjetaestilos.models.SectionsMenus;
import pe.tarjetaestilos.models.MenuType;
import pe.tarjetaestilos.models.Section;
import pe.tarjetaestilos.models.SectionType;


@RestController
@RequestMapping("tarjeta-estilos")
public class MenuController {
    private final MenuRepository menuRepository;
    private final SectionRepository sectionRepository;
    private final SectionsMenusRepository sectionsMenusRepository;
    private final SectionTypeRepository sectionTypeRepository;
    private final MenuTypesRepository menuTypesRepository;

    public MenuController(
            MenuRepository menuRepository,
            SectionRepository sectionRepository,
            SectionsMenusRepository sectionsMenusRepository,
            SectionTypeRepository sectionTypeRepository,
            MenuTypesRepository menuTypesRepository
    ) {
        this.menuRepository = menuRepository;
        this.sectionRepository = sectionRepository;
        this.sectionsMenusRepository = sectionsMenusRepository;
        this.sectionTypeRepository = sectionTypeRepository;
        this.menuTypesRepository = menuTypesRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(PromotionController.class);
    
    @Autowired
    private MenuService menuService;
    
    @GetMapping("/cms/menuAll") 
    public List<Menu> getMenuAll(){
        List<Menu> menus = this.menuService.getMenuAll();
        logger.info("registos obtenidos:");
        menus.forEach(menu-> logger.info(menu.toString()));
        return menus;
    }
    
    @GetMapping("/cms/menu/{page}/{size}") 
    public Page<Menu> getMenus(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return menuService.getMenu(pageable);
    }
    
    @PostMapping("/cms/menu/search")
    public Page<Menu> searchMenus(@RequestBody MenuSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return menuService.search(dto.getTitle(),dto.getSlug(),dto.getSections(), pageable);
    }
    
    @PostMapping(value = "/cms/menu/create")
    public ResponseEntity<ApiResponseDto<Menu>> createMenu(
            @Valid @RequestBody MenuCreateDto dto
    ) {
        try {
            Menu menu = new Menu();
            
            menu.setTitle(dto.getTitle());
            menu.setSlug(dto.getSlug());
            menu.setIcon(dto.getIcon());
            menu.setPage(dto.getPage());
            menu.setProduct(dto.getProduct());
            menu.setInsurance(dto.getInsurance());
            menu.setPosition(dto.getPosition());
            
            Menu savedMenu = menuService.saveMenu(menu);
            
            List<Long> sections = dto.getSections();
            if (sections != null && !sections.isEmpty()) {
                List<SectionsMenus> sectionsMenus = sections.stream()
                    .map(sectId -> {
                        Section section = sectionRepository.findById(sectId)
                                .orElseThrow(() -> new RuntimeException("Category not found with id " + sectId));

                        SectionsMenus sm = new SectionsMenus();
                        sm.setMenu(menu);
                        sm.setSection(section);
                        return sm;
                    })
                    .toList();
                sectionsMenusRepository.saveAll(sectionsMenus);
                savedMenu.getSectionsMenus().addAll(sectionsMenus);
            }
            
            List<Long> menutypes = dto.getTypes();
            if (menutypes != null && !menutypes.isEmpty()) {
                List<MenuType> menuType = menutypes.stream()
                    .map(sectId -> {
                        SectionType sectionType = sectionTypeRepository.findById(sectId)
                                .orElseThrow(() -> new RuntimeException("Category not found with id " + sectId));

                        MenuType mt = new MenuType();
                        mt.setMenu(menu);
                        mt.setSectionType(sectionType);
                        return mt;
                    })
                    .toList();
                menuTypesRepository.saveAll(menuType);
                savedMenu.getMenuType().addAll(menuType);
            }
            

            Menu newMenu = menuService.getMenuById(savedMenu.getId());

            String successMessage = "Menu registrada con ID " + savedMenu.getId();
            ApiResponseDto<Menu> response = new ApiResponseDto<>(successMessage, newMenu, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar menu: " + e.getMessage();
            ApiResponseDto<Menu> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/menu/update/{id}")
    public ResponseEntity<ApiResponseDto<Menu>> updateMenu(
            @PathVariable Long id,
            @Valid @RequestBody MenuCreateDto dto
    ) {
        try {
            Menu menu = menuRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Menu no encontrada con id " + id));

            menu.setTitle(dto.getTitle());
            menu.setSlug(dto.getSlug());
            menu.setIcon(dto.getIcon());
            menu.setPage(dto.getPage());
            menu.setProduct(dto.getProduct());
            menu.setInsurance(dto.getInsurance());
            menu.setPosition(dto.getPosition());
            
            List<Long> sections = dto.getSections();            
            if (sections != null && !sections.isEmpty()) {
                // Borrar categorías antiguas
                sectionsMenusRepository.deleteAll(menu.getSectionsMenus());

                // Insertar las nuevas
                List<SectionsMenus> sectionsMenus = sections.stream()
                    .map(catId -> {
                        Section section = sectionRepository.findById(catId)
                                .orElseThrow(() -> new RuntimeException("Category not found with id " + catId));
                        SectionsMenus bc = new SectionsMenus();
                        bc.setMenu(menu);
                        bc.setSection(section);
                        return bc;
                    })
                    .toList();

                sectionsMenusRepository.saveAll(sectionsMenus);
                menu.getSectionsMenus().clear();
                menu.getSectionsMenus().addAll(sectionsMenus);
            }else{
                sectionsMenusRepository.deleteAll(menu.getSectionsMenus());
                menu.getSectionsMenus().clear();
            }
            
            
            List<Long> menutypes = dto.getTypes();            
            if (menutypes != null && !menutypes.isEmpty()) {
                // Borrar categorías antiguas
                menuTypesRepository.deleteAll(menu.getMenuType());

                // Insertar las nuevas
                List<MenuType> menuType = menutypes.stream()
                    .map(meId -> {
                        SectionType sectionType = sectionTypeRepository.findById(meId)
                                .orElseThrow(() -> new RuntimeException("Category not found with id " + meId));
                        MenuType bc = new MenuType();
                        bc.setMenu(menu);
                        bc.setSectionType(sectionType);
                        return bc;
                    })
                    .toList();

                menuTypesRepository.saveAll(menuType);
                menu.getMenuType().clear();
                menu.getMenuType().addAll(menuType);
            }else{
                menuTypesRepository.deleteAll(menu.getMenuType());
                menu.getMenuType().clear();
            }
            
            Menu updatedMenu = menuService.saveMenu(menu);

            String successMessage = "Menu actualizado con ID " + updatedMenu.getId();
            ApiResponseDto<Menu> response = new ApiResponseDto<>(successMessage, updatedMenu, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar menu: " + e.getMessage();
            ApiResponseDto<Menu> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/menu/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteMenu(@PathVariable Long id) {
        try {
           Menu existingMenu = menuRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingMenu.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("El menu ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingMenu.setDeletedAt(ZonedDateTime.now());
            menuRepository.save(existingMenu);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Menu eliminado con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar el menu: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/web/menu/{slug}")
    public ResponseEntity<?> getMenuBySlug(@PathVariable String slug) {
        return menuService.getMenuBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/web/menuAll") 
    public List<Menu> getWebMenuAll(){
        List<Menu> menus = this.menuService.getMenuAll();
        logger.info("registos obtenidos:");
        menus.forEach(menu-> logger.info(menu.toString()));
        return menus;
    }
    
}
