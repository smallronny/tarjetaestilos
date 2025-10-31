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
import pe.tarjetaestilos.models.Category;
import pe.tarjetaestilos.services.CategoryService;
import pe.tarjetaestilos.dto.category.CategorySearchDto;
import pe.tarjetaestilos.dto.ApiResponseDto;
import pe.tarjetaestilos.dto.category.CategoryCreateDto;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PutMapping;
import pe.tarjetaestilos.repositories.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.server.ResponseStatusException;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("tarjeta-estilos")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    
    public CategoryController(
            CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/cms/categoryAll") //http://localhost:8081/tarjeta-estilos-app/category
    public List<Category> getCategoriesAll(){
        List<Category> categories = this.categoryService.getCategoryAll();
        logger.info("registos obtenidos:");
        categories.forEach(category-> logger.info(category.toString()));
        return categories;
    }
    
    @GetMapping("/cms/category/{page}/{size}") 
    public Page<Category> getCategories(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size);
        return categoryService.getCategory(pageable);
    }
    
    @PostMapping("/cms/category/search")
    public Page<Category> searchCategories(@RequestBody CategorySearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );
        return categoryService.search(dto.getName(), pageable);
    }
    
    //Crear categoria
    @PostMapping(value = "/cms/category/create")
    public ResponseEntity<ApiResponseDto<Category>> createCategory(
            @Valid @RequestBody CategoryCreateDto dto
    ) {
        try {
            Category category = new Category();
            category.setName(dto.getName());

            Category savedCategory = categoryService.saveCategory(category);
            
            
            Category newCategory = categoryService.getCategoryById(savedCategory.getId());
            
            String successMessage = "Categoria registrada con ID " + savedCategory.getId();
            ApiResponseDto<Category> response = new ApiResponseDto<>(successMessage, newCategory, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            String errorMessage = "Error al registrar categoria: " + e.getMessage();
            ApiResponseDto<Category> response = new ApiResponseDto<>(errorMessage, null,HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //crear categoria
    
    
    //Modificar categoria
    @PutMapping(value = "/cms/category/update/{id}")
    public ResponseEntity<ApiResponseDto<Category>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryCreateDto dto
    ) {
        try {
            Category existingCategory = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con id " + id));

            existingCategory.setName(dto.getName());

            Category updatedCategory = categoryService.saveCategory(existingCategory);

            String successMessage = "Categoría actualizada con ID " + updatedCategory.getId();
            ApiResponseDto<Category> response =
                    new ApiResponseDto<>(successMessage, updatedCategory, HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar categoría: " + e.getMessage();
            ApiResponseDto<Category> response =
                    new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //Modificar categoria
    
    
    //Eliminar categoria
    @DeleteMapping("/cms/category/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteCategory(@PathVariable Long id) {
        try {
            Category existingCategory = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Categoria no encontrada con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingCategory.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("La categoria ya fue eliminada", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingCategory.setDeletedAt(ZonedDateTime.now());
            categoryRepository.save(existingCategory);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Categoria eliminada con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar categoria: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //Eliminar Categoria
    
}
