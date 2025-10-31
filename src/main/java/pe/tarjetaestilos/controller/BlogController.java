/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.data.domain.Sort;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import pe.tarjetaestilos.dto.ApiResponseDto;
import jakarta.validation.Valid;
import pe.tarjetaestilos.dto.blog.BlogCreateDto;

import pe.tarjetaestilos.models.Blog;
import pe.tarjetaestilos.models.Menu;
import pe.tarjetaestilos.models.BlogCategory;
import pe.tarjetaestilos.models.Category;
import pe.tarjetaestilos.services.BlogService;
import pe.tarjetaestilos.dto.blog.BlogSearchDto;
import pe.tarjetaestilos.dto.blog.BlogResponseDto;
import pe.tarjetaestilos.repositories.BlogCategoryRepository;
import pe.tarjetaestilos.repositories.CategoryRepository;
import pe.tarjetaestilos.repositories.BlogRepository;
import pe.tarjetaestilos.repositories.MenuRepository;
import pe.tarjetaestilos.dto.ImageResponseDto;
/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos")
public class BlogController {
    private final CategoryRepository categoryRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final BlogRepository blogRepository;
    private final MenuRepository menuRepository;

    public BlogController(
            BlogCategoryRepository blogCategoryRepository,
            CategoryRepository categoryRepository,
            BlogRepository blogRepository,
            MenuRepository menuRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.blogCategoryRepository = blogCategoryRepository;
        this.blogRepository = blogRepository;
        this.menuRepository = menuRepository;
    }
    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);
    
    @Autowired
    private BlogService blogService;
    
    
    @GetMapping("/web/blog/byMenu/{slug}")
    public ResponseEntity<List<Blog>> getBlogsByMenuSlug(@PathVariable String slug) {
        List<Blog> blogs = blogService.getBlogsByMenuSlug(slug);

        if (blogs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(blogs);
    }
    
    @GetMapping("/cms/blog/{page}/{size}") 
    public Page<Blog> getBlogs(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size,Sort.by("id").descending());
        return blogService.getBlog(pageable);
    }
    
    @PostMapping("/cms/blog/search")
    public Page<Blog> searchBlog(@RequestBody BlogSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );
        return blogService.search(dto.getTitle(),dto.getDescription(),dto.getSummary(),dto.getStartDate(),dto.getEndDate(),dto.getCategory_id(), pageable);
    }
    
    @PostMapping(value = "/cms/blog/create")
    public ResponseEntity<BlogResponseDto<Blog>> createBlog(
            @Valid @RequestBody BlogCreateDto dto
    ) {
        try {
            //formato para fecha de publicación
            ZonedDateTime publicationDate = ZonedDateTime.parse(dto.getPublication_date());                  

            Blog blog = new Blog();
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            
            blog.setTitle(dto.getTitle());
            blog.setDescription(dto.getDescription());
            blog.setSummary(dto.getSummary());
            blog.setPublicationDate(publicationDate);
            blog.setCardImage(dto.getCard_image());
            blog.setMainImage(dto.getMain_image());
            blog.setMenu(menu); 

            Blog savedBlog = blogService.saveBlog(blog);
            List<Long> categoryIds = dto.getCategory_id();
            if (categoryIds != null && !categoryIds.isEmpty()) {
                List<BlogCategory> blogCategories = categoryIds.stream()
                    .map(catId -> {
                        Category category = categoryRepository.findById(catId)
                                .orElseThrow(() -> new RuntimeException("Category not found with id " + catId));

                        BlogCategory bc = new BlogCategory();
                        bc.setBlog(savedBlog);
                        bc.setCategory(category);
                        return bc;
                    })
                    .toList();
                blogCategoryRepository.saveAll(blogCategories);
                savedBlog.getBlogCategories().addAll(blogCategories);
            }
            
            Blog newBlog = blogService.getNewBlogById(savedBlog.getId());
            
            String successMessage = "Noticia registrada con ID " + savedBlog.getId();
            BlogResponseDto<Blog> response = new BlogResponseDto<>(successMessage, newBlog, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "Error al registrar noticia: " + e.getMessage();
            BlogResponseDto<Blog> response = new BlogResponseDto<>(errorMessage, null,HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/blog/update/{id}")
    public ResponseEntity<BlogResponseDto<Blog>> updateBlog(
            @PathVariable Long id,
            @Valid @RequestBody BlogCreateDto dto
    ) {
        try {
            Blog existingBlog = blogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Blog no encontrado con id " + id));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            
            if (dto.getPublication_date() != null && !dto.getPublication_date().isBlank()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(dto.getPublication_date(), formatter);
                ZonedDateTime publicationDate = localDate.atStartOfDay(ZoneId.systemDefault());
                existingBlog.setPublicationDate(publicationDate);
            }

            existingBlog.setTitle(dto.getTitle());
            existingBlog.setDescription(dto.getDescription());
            existingBlog.setSummary(dto.getSummary());
            existingBlog.setMenu(menu);
            
            if (dto.getCard_image()!= null && !dto.getCard_image().trim().isEmpty()) {
                existingBlog.setCardImage(dto.getCard_image());
            }

            if (dto.getMain_image() != null && !dto.getMain_image().trim().isEmpty()) {
                existingBlog.setMainImage(dto.getMain_image());
            }

            List<Long> categoryIds = dto.getCategory_id();
            // Actualizar categorías
            if (categoryIds != null) {
                // Borrar categorías antiguas
                blogCategoryRepository.deleteAll(existingBlog.getBlogCategories());

                // Insertar las nuevas
                List<BlogCategory> blogCategories = categoryIds.stream()
                    .map(catId -> {
                        Category category = categoryRepository.findById(catId)
                                .orElseThrow(() -> new RuntimeException("Category not found with id " + catId));
                        BlogCategory bc = new BlogCategory();
                        bc.setBlog(existingBlog);
                        bc.setCategory(category);
                        return bc;
                    })
                    .toList();

                blogCategoryRepository.saveAll(blogCategories);
                existingBlog.getBlogCategories().clear();
                existingBlog.getBlogCategories().addAll(blogCategories);
            }

            Blog updatedBlog = blogService.saveBlog(existingBlog);

            String successMessage = "Noticia actualizada con ID " + updatedBlog.getId();
            BlogResponseDto<Blog> response = new BlogResponseDto<>(successMessage, updatedBlog, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); 
            String errorMessage = "Error al actualizar noticia: " + e.getMessage();
            BlogResponseDto<Blog> response = new BlogResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/blog/delete/{id}")
    public ResponseEntity<BlogResponseDto<Void>> deleteBlog(@PathVariable Long id) {
        try {
            Blog existingBlog = blogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Blog no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingBlog.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BlogResponseDto<>("El blog ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingBlog.setDeletedAt(ZonedDateTime.now());
            blogRepository.save(existingBlog);

            BlogResponseDto<Void> response = new BlogResponseDto<>(
                    "Noticia eliminada con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BlogResponseDto<Void> response = new BlogResponseDto<>(
                    "Error al eliminar noticia: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping(value = "/cms/blog/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<String>> createImage(
        @RequestParam(value = "image", required = false) MultipartFile image,
        @RequestParam(value = "type", required = false) String type
    ) {
        try {
            Path uploadPath;
            if ("card".equalsIgnoreCase(type)) {
                uploadPath = Paths.get("uploads/promotion/card/");
            } else {
                uploadPath = Paths.get("uploads/promotion/logo/");
            }
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
                    "Imagen card creada ",
                    filePath != null ? filePath.toString() : null,
                    HttpStatus.CREATED.value()
            );            
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            String errorMessage = "Error al registrar noticia: " + e.getMessage();
            ApiResponseDto<String> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping(value = "/cms/blog/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageResponseDto<String>> ImageUpload(
        @RequestParam(value = "file", required = false) MultipartFile image
    ) {
        try {
            Path uploadPath = Paths.get("uploads/promotion/imageUpload/");
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
            
            BufferedImage bufferedImage = ImageIO.read(filePath.toFile());
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            
            ImageResponseDto<String> response = new ImageResponseDto<>(
                    "Imagen creada ",
                    filePath != null ? filePath.toString().replace("\\", "/") : null,
                    HttpStatus.CREATED.value(),
                    width,
                    height
            );            
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            String errorMessage = "Error al registrar noticia: " + e.getMessage();
            ImageResponseDto<String> response = new ImageResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value(),0,0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
        
    
}
