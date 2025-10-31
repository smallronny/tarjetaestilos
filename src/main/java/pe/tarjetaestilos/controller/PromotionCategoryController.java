/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.controller;

/**
 *
 * @author julito
 */
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import pe.tarjetaestilos.models.PromotionCategory;
import pe.tarjetaestilos.services.PromotionCategoryService;
import pe.tarjetaestilos.dto.promotionCategory.PromotionCategoryCreateDto;
import pe.tarjetaestilos.dto.ApiResponseDto;
import pe.tarjetaestilos.dto.promotionCategory.PromotionCategorySearchDto;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PutMapping;
import pe.tarjetaestilos.repositories.PromotionCategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.server.ResponseStatusException;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("tarjeta-estilos")
public class PromotionCategoryController {
    
    private final PromotionCategoryRepository promotionCategoryRepository;
    
    public PromotionCategoryController(
            PromotionCategoryRepository promotionCategoryRepository
    ) {
        this.promotionCategoryRepository = promotionCategoryRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(PromotionCategoryController.class);
    
    @Autowired
    private PromotionCategoryService promotionCategoryService;
    
    @GetMapping("/cms/promotion-category-all") 
    public List<PromotionCategory> getPromotionCategoryAll(){
        List<PromotionCategory> promotionCategory = this.promotionCategoryService.getPromotionCategoryAll();
        logger.info("registos obtenidos:");
        promotionCategory.forEach(promotionCat-> logger.info(promotionCat.toString()));
        return promotionCategory;
    }
    
    @GetMapping("/cms/promotion-category/{page}/{size}") 
    public Page<PromotionCategory> getPromotionCategory(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size);
        return promotionCategoryService.getPromotionCategory(pageable);
    }
    
    @PostMapping("/cms/promotion-category/search")
    public Page<PromotionCategory> searchPromotionCategory(@RequestBody PromotionCategorySearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );
        return promotionCategoryService.search(dto.getName(), pageable);
    }
    
    //Crear categoria
    @PostMapping(value = "/cms/promotion-category/create")
    public ResponseEntity<ApiResponseDto<PromotionCategory>> createPromotionCategory(
            @Valid @RequestBody PromotionCategoryCreateDto dto
    ) {
        try {
            PromotionCategory promotionCategory = new PromotionCategory();
            promotionCategory.setName(dto.getName());

            PromotionCategory savedPromotionCategory = promotionCategoryService.savePromotionCategory(promotionCategory);
            
            
            PromotionCategory newPromotionCategory = promotionCategoryService.getPromotionCategoryById(savedPromotionCategory.getId());
            
            String successMessage = "Categoria de promoción registrada con ID " + savedPromotionCategory.getId();
            ApiResponseDto<PromotionCategory> response = new ApiResponseDto<>(successMessage, newPromotionCategory, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            String errorMessage = "Error al registrar categoria: " + e.getMessage();
            ApiResponseDto<PromotionCategory> response = new ApiResponseDto<>(errorMessage, null,HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //crear categoria
    
    
    //Modificar categoria
    @PutMapping(value = "/cms/promotion-category/update/{id}")
    public ResponseEntity<ApiResponseDto<PromotionCategory>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody PromotionCategoryCreateDto dto
    ) {
        try {
            PromotionCategory existingPromotionCategory = promotionCategoryRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría de Promoción no encontrada con id " + id));

            existingPromotionCategory.setName(dto.getName());
            
            existingPromotionCategory.setIcono(dto.getIcono());

            PromotionCategory updatedPromotionCategory = promotionCategoryService.savePromotionCategory(existingPromotionCategory);

            String successMessage = "Categoría de Promoción actualizada con ID " + updatedPromotionCategory.getId();
            ApiResponseDto<PromotionCategory> response =
                    new ApiResponseDto<>(successMessage, updatedPromotionCategory, HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar categoría de promoción: " + e.getMessage();
            ApiResponseDto<PromotionCategory> response =
                    new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //Modificar categoria
    
    
    //Eliminar categoria
    @DeleteMapping("/cms/promotion-category/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteBlog(@PathVariable Long id) {
        try {
            PromotionCategory existingPromotionCategory = promotionCategoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Categoria de promoción no encontrada con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingPromotionCategory.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("La categoria de promocin ya fue eliminada", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingPromotionCategory.setDeletedAt(ZonedDateTime.now());
            promotionCategoryRepository.save(existingPromotionCategory);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Categoria de promoción eliminada con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar categoria de promoción: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //Eliminar Categoria
    
    
}
