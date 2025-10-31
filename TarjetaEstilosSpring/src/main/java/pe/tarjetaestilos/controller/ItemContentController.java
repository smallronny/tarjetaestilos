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
import pe.tarjetaestilos.models.ItemContent;
import pe.tarjetaestilos.models.PageContent;
import pe.tarjetaestilos.services.ItemContentService;
import pe.tarjetaestilos.dto.itemContent.ItemContentSearchDto;
import pe.tarjetaestilos.dto.itemContent.ItemContentCreateDto;
import pe.tarjetaestilos.repositories.ItemContentRepository;
import pe.tarjetaestilos.repositories.PageContentRepository;
import pe.tarjetaestilos.dto.ApiResponseDto;
/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos")
public class ItemContentController {
    private final ItemContentRepository itemContentRepository;
    private final PageContentRepository pageContentRepository;

    public ItemContentController(
            ItemContentRepository itemContentRepository,
            PageContentRepository pageContentRepository
    ) {
        this.pageContentRepository = pageContentRepository;
        this.itemContentRepository = itemContentRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(ItemContentController.class);
    
    @Autowired
    private ItemContentService itemContentService;
    
    
    @GetMapping("/cms/itemContentAll") 
    public List<ItemContent> getItemContentAll(){
        List<ItemContent> itemContents = this.itemContentService.getItemContentAll();
        logger.info("registos obtenidos:");
        itemContents.forEach(itemContent-> logger.info(itemContent.toString()));
        return itemContents;
    }
    
    @GetMapping("/cms/itemContent/{page}/{size}") 
    public Page<ItemContent> getItemContent(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return itemContentService.getItemContent(pageable);
    }
    
    @PostMapping("/cms/itemContent/search")
    public Page<ItemContent> searchItemContent(@RequestBody ItemContentSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return itemContentService.search(dto.getDescription(),dto.getPage_content_id(), pageable);
    }
    
    
    @PostMapping(value = "/cms/itemContent/create")
    public ResponseEntity<ApiResponseDto<ItemContent>> createItemContent(
            @Valid @RequestBody ItemContentCreateDto dto
    ) {
        try {
            ItemContent itemContents = new ItemContent();
            PageContent pageContent = pageContentRepository.findById(dto.getPage_content_id())
                    .orElseThrow(() -> new RuntimeException("Page content no encontrado id " + dto.getPage_content_id()));
            
            itemContents.setIcon(dto.getIcon());
            itemContents.setDescription(dto.getDescription());
            itemContents.setPageContent(pageContent);
            
            ItemContent savedItemContent = itemContentService.saveItemContent(itemContents);
            

            ItemContent newItemContent = itemContentService.getItemContentById(savedItemContent.getId());

            String successMessage = "ItemContent registrado con ID " + savedItemContent.getId();
            ApiResponseDto<ItemContent> response = new ApiResponseDto<>(successMessage, newItemContent, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar banner: " + e.getMessage();
            ApiResponseDto<ItemContent> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/itemContent/update/{id}")
    public ResponseEntity<ApiResponseDto<ItemContent>> updateItemContent(
            @PathVariable Long id,
            @Valid @RequestBody ItemContentCreateDto dto
    ) {
        try {
            ItemContent itemContent = itemContentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("ItemContent no encontrada con id " + id));
            
            PageContent pageContent = pageContentRepository.findById(dto.getPage_content_id())
                    .orElseThrow(() -> new RuntimeException("PageContent  no encontrada id " + dto.getPage_content_id()));
            
            itemContent.setIcon(dto.getIcon());
            itemContent.setDescription(dto.getDescription());
            itemContent.setPageContent(pageContent);
            
            
            ItemContent updatedItemContent = itemContentService.saveItemContent(itemContent);

            String successMessage = "ItemContent actualizado con ID " + updatedItemContent.getId();
            ApiResponseDto<ItemContent> response = new ApiResponseDto<>(successMessage, updatedItemContent, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar ItemContent: " + e.getMessage();
            ApiResponseDto<ItemContent> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/itemContent/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteItemContent(@PathVariable Long id) {
        try {
           ItemContent existingItemContent = itemContentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("ItemContent no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingItemContent.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("ItemContent ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingItemContent.setDeletedAt(ZonedDateTime.now());
            itemContentRepository.save(existingItemContent);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "ItemContent eliminado con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar el itemContent: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}
