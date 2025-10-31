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
import pe.tarjetaestilos.models.PageContent;
import pe.tarjetaestilos.models.Menu;
import pe.tarjetaestilos.services.PageContentService;
import pe.tarjetaestilos.dto.pageContent.PageContentSearchDto;
import pe.tarjetaestilos.dto.pageContent.PageContentCreateDto;
import pe.tarjetaestilos.repositories.PageContentRepository;
import pe.tarjetaestilos.repositories.MenuRepository;
import pe.tarjetaestilos.dto.ApiResponseDto;
import pe.tarjetaestilos.dto.itemContent.ItemContentCreateDto;
import pe.tarjetaestilos.models.ItemContent;
import pe.tarjetaestilos.repositories.ItemContentRepository;
/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos")
public class PageContentController {
    private final PageContentRepository pageContentRepository;
    private final MenuRepository menuRepository;
    private final ItemContentRepository itemContentRepository;

    public PageContentController(
            PageContentRepository pageContentRepository,
            MenuRepository menuRepository,
            ItemContentRepository itemContentRepository
    ) {
        this.pageContentRepository = pageContentRepository;
        this.menuRepository = menuRepository;
        this.itemContentRepository = itemContentRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(PageContentController.class);
    
    @Autowired
    private PageContentService pageContentService;
    
    
    @GetMapping("/web/page-content/byMenu/{slug}")
    public ResponseEntity<List<PageContent>> getPageContentByMenuSlug(@PathVariable String slug) {
        List<PageContent> pageContent = pageContentService.getPageContentByMenuSlug(slug);

        if (pageContent.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(pageContent);
    }
    
    
    @GetMapping("/cms/page-content-all") 
    public List<PageContent> getPageContentAll(){
        List<PageContent> pageContents = this.pageContentService.getPageContentAll();
        logger.info("registos obtenidos:");
        pageContents.forEach(pageContent-> logger.info(pageContent.toString()));
        return pageContents;
    }
    
    @GetMapping("/cms/page-content/{page}/{size}") 
    public Page<PageContent> getPageContent(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return pageContentService.getPageContent(pageable);
    }
    
    @PostMapping("/cms/page-content/search")
    public Page<PageContent> searchPageContent(@RequestBody PageContentSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return pageContentService.search(dto.getTitle(),dto.getDescription(),dto.getMenu_id(), pageable);
    }
    
    
    @PostMapping(value = "/cms/page-content/create")
    public ResponseEntity<ApiResponseDto<PageContent>> createPageContent(
            @Valid @RequestBody PageContentCreateDto dto
    ) {
        try {
            PageContent pageContents = new PageContent();
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            
            pageContents.setTitle(dto.getTitle());
            pageContents.setDescription(dto.getDescription());
            pageContents.setImage_position(dto.getImage_position());
            pageContents.setImage(dto.getImage());
            pageContents.setMenu(menu);
            
            PageContent savedPageContent = pageContentService.savePageContent(pageContents);
            
            List<ItemContentCreateDto> itemContentCreateDtos = dto.getItemContents();

            if (itemContentCreateDtos != null && !itemContentCreateDtos.isEmpty()) {
                List<ItemContent> itemContent = itemContentCreateDtos.stream()
                    .map(netId -> {
                        
                        ItemContent bc = new ItemContent();
                        bc.setPageContent(savedPageContent);
                        bc.setDescription(netId.getDescription());
                        bc.setIcon(netId.getIcon());
                        return bc;
                    })
                    .toList();
                itemContentRepository.saveAll(itemContent);
                savedPageContent.getItemContents().addAll(itemContent);
            }
            PageContent newPageContent = pageContentService.getPageContentById(savedPageContent.getId());

            String successMessage = "PageContent registrado con ID " + savedPageContent.getId();
            ApiResponseDto<PageContent> response = new ApiResponseDto<>(successMessage, newPageContent, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar banner: " + e.getMessage();
            ApiResponseDto<PageContent> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/page-content/update/{id}")
    public ResponseEntity<ApiResponseDto<PageContent>> updatePageContent(
            @PathVariable Long id,
            @Valid @RequestBody PageContentCreateDto dto
    ) {
        try {
            PageContent pageContent = pageContentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("PageContent no encontrada con id " + id));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu  no encontrada id " + dto.getMenu_id()));
            
            pageContent.setTitle(dto.getTitle());
            pageContent.setDescription(dto.getDescription());
            pageContent.setImage_position(dto.getImage_position());
            pageContent.setMenu(menu);
            
            if (dto.getImage() != null && !dto.getImage().trim().isEmpty()) {
                pageContent.setImage(dto.getImage());
            }
            
            List<ItemContentCreateDto> itemContentCreateDtos = dto.getItemContents();

            if (itemContentCreateDtos != null && !itemContentCreateDtos.isEmpty()) {
                
                itemContentRepository.deleteAll(pageContent.getItemContents());
                
                List<ItemContent> itemContent = itemContentCreateDtos.stream()
                    .map(netId -> {
                        
                        ItemContent bc = new ItemContent();
                        bc.setDescription(netId.getDescription());
                        bc.setIcon(netId.getIcon());
                        bc.setPageContent(pageContent);
                        return bc;
                    })
                    .toList();
                itemContentRepository.saveAll(itemContent);
                pageContent.getItemContents().clear();
                pageContent.getItemContents().addAll(itemContent);

            }
            
            PageContent updatedPageContent = pageContentService.savePageContent(pageContent);

            String successMessage = "PageContent actualizado con ID " + updatedPageContent.getId();
            ApiResponseDto<PageContent> response = new ApiResponseDto<>(successMessage, updatedPageContent, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar PageContent: " + e.getMessage();
            ApiResponseDto<PageContent> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/page-content/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deletePageContent(@PathVariable Long id) {
        try {
           PageContent existingPageContent = pageContentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("PageContent no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingPageContent.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("Banner ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingPageContent.setDeletedAt(ZonedDateTime.now());
            pageContentRepository.save(existingPageContent);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "PageContent eliminado con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar el pageContent: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping(value = "/cms/page-content/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<String>> createImage(
        @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            Path uploadPath = Paths.get("uploads/pageContent/image/");
            
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
