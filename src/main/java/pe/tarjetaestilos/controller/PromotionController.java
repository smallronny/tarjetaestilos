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
import pe.tarjetaestilos.models.Promotion;
import pe.tarjetaestilos.services.PromotionService;
import pe.tarjetaestilos.dto.promotion.PromotionSearchDto;
import pe.tarjetaestilos.dto.promotion.PromotionCreateDto;
import pe.tarjetaestilos.repositories.PromotionRepository;
import pe.tarjetaestilos.repositories.MenuRepository;
import pe.tarjetaestilos.repositories.PromotionCategoryRepository;
import pe.tarjetaestilos.repositories.PromotionsCategoriesRepository;
import pe.tarjetaestilos.repositories.AffiliatesRepository;

import org.springframework.http.MediaType;
import pe.tarjetaestilos.dto.ApiResponseDto;
import pe.tarjetaestilos.dto.ImageResponseDto;
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
import pe.tarjetaestilos.models.PromotionsCategories;
import pe.tarjetaestilos.models.PromotionCategory;
import pe.tarjetaestilos.models.Department;
import pe.tarjetaestilos.models.Menu;
import pe.tarjetaestilos.models.Affiliates;
import pe.tarjetaestilos.repositories.DepartmentRepository;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.time.ZonedDateTime;
import jakarta.validation.Valid;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos") 
public class PromotionController {
    private final PromotionRepository promotionRepository;
    private final PromotionCategoryRepository promotionCategoryRepository;
    private final PromotionsCategoriesRepository promotionsCategoriesRepository;
    private final DepartmentRepository departmentRepository;
    private final MenuRepository menuRepository;
    private final AffiliatesRepository affiliatesRepository;

    public PromotionController(
            PromotionRepository promotionRepository,
            PromotionCategoryRepository promotionCategoryRepository,
            PromotionsCategoriesRepository promotionsCategoriesRepository,
            DepartmentRepository departmentRepository,
            MenuRepository menuRepository,
            AffiliatesRepository affiliatesRepository
    ) {
        this.promotionRepository = promotionRepository;
        this.promotionCategoryRepository = promotionCategoryRepository;
        this.promotionsCategoriesRepository = promotionsCategoriesRepository;
        this.departmentRepository = departmentRepository;
        this.menuRepository = menuRepository;
        this.affiliatesRepository = affiliatesRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(PromotionController.class);
    
    @Autowired
    private PromotionService promotionService;
    
    
    @GetMapping("/web/promotion/byMenu/{slug}")
    public ResponseEntity<List<Promotion>> getPageContentByMenuSlug(@PathVariable String slug) {
        List<Promotion> promotion = promotionService.getPromotionByMenuSlug(slug);
        if (promotion.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(promotion);
    }
    
    
    @GetMapping("/cms/promotionAll") 
    public List<Promotion> getPromotionsAll(){
        List<Promotion> promotions = this.promotionService.getPromotionAll();
        logger.info("registos obtenidos:");
        promotions.forEach(promotion-> logger.info(promotion.toString()));
        return promotions;
    }
    
    @GetMapping("/cms/promotions/{page}/{size}") 
    public Page<Promotion> getPromotions(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return promotionService.getPromotion(pageable);
    }
    
    @PostMapping("/cms/promotion/search")
    public Page<Promotion> searchPromotions(@RequestBody PromotionSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return promotionService.search(dto.getTitle(),dto.getDescription(),dto.getDiscount(),dto.getIsnew(),dto.getDepartment_id(),dto.getHome(),dto.getExclusive(), pageable);
    }
    
    
    
    @PostMapping(value = "/cms/promotion/create")
    public ResponseEntity<ApiResponseDto<Promotion>> createPromotion(
            @Valid @RequestBody PromotionCreateDto dto
    ) {
        try {
            Promotion promotion = new Promotion();
            Department department = departmentRepository.findById(dto.getDepartment_id())
                    .orElseThrow(() -> new RuntimeException("Categoria de promoción no encontrada id " + dto.getDepartment_id()));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            
            Affiliates affiliates = affiliatesRepository.findById(dto.getAffiliates_id())
                    .orElseThrow(() -> new RuntimeException("Afiliado no encontrado id " + dto.getAffiliates_id()));
            
            promotion.setTitle(dto.getTitle());
            promotion.setDescription(dto.getDescription());
            promotion.setTerms_conditions(dto.getTerms_conditions());
            promotion.setPromotion_link(dto.getPromotion_link());
            promotion.setPromotion_map(dto.getPromotion_map());
            promotion.setDiscount(dto.getDiscount());
            promotion.setDepartment(department);
            promotion.setOutstanding(dto.getOutstanding());
            promotion.setFeatured(dto.getFeatured());
            promotion.setIsnew(dto.getIsnew());
            promotion.setImage(dto.getImage());
            promotion.setLogo(dto.getLogo());
            promotion.setMenu(menu);
            promotion.setAffiliates(affiliates);
            promotion.setHome(dto.getHome());
            
            Promotion savedPromotion = promotionService.savePromotion(promotion);
            List<Long> categoryIds = dto.getCategoryIds();
            if (categoryIds != null && !categoryIds.isEmpty()) {
                List<PromotionsCategories> promotionsCategories = categoryIds.stream()
                    .map(catId -> {
                        PromotionCategory promotionCategory = promotionCategoryRepository.findById(catId)
                                .orElseThrow(() -> new RuntimeException("Categoria de promoción no encontrada id " + catId));
                        PromotionsCategories bc = new PromotionsCategories();
                        bc.setPromotion(savedPromotion);
                        bc.setPromotionCategory(promotionCategory);
                        return bc;
                    })
                    .toList();
                promotionsCategoriesRepository.saveAll(promotionsCategories);
                savedPromotion.getPromotionsCategories().addAll(promotionsCategories);
            }

            Promotion newPromotion = promotionService.getNewPromotionById(savedPromotion.getId());

            String successMessage = "Promoción registrada con ID " + savedPromotion.getId();
            ApiResponseDto<Promotion> response = new ApiResponseDto<>(successMessage, newPromotion, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar noticia: " + e.getMessage();
            ApiResponseDto<Promotion> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/promotion/update/{id}")
    public ResponseEntity<ApiResponseDto<Promotion>> updatePromotion(
            @PathVariable Long id,
            @Valid @RequestBody PromotionCreateDto dto
    ) {
        try {
            Promotion promotion = promotionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Promoción no encontrada con id " + id));

            
            Department department = departmentRepository.findById(dto.getDepartment_id())
                    .orElseThrow(() -> new RuntimeException("Categoria de promoción no encontrada id " + dto.getDepartment_id()));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            
            Affiliates affiliates = affiliatesRepository.findById(dto.getAffiliates_id())
                    .orElseThrow(() -> new RuntimeException("Afiliado no encontrado id " + dto.getAffiliates_id()));
            
            promotion.setTitle(dto.getTitle());
            promotion.setDescription(dto.getDescription());
            promotion.setTerms_conditions(dto.getTerms_conditions());
            promotion.setPromotion_link(dto.getPromotion_link());
            promotion.setPromotion_map(dto.getPromotion_map());
            promotion.setDiscount(dto.getDiscount());
            promotion.setDepartment(department);
            promotion.setOutstanding(dto.getOutstanding());
            promotion.setFeatured(dto.getFeatured());
            promotion.setIsnew(dto.getIsnew());
            promotion.setMenu(menu);
            promotion.setAffiliates(affiliates);
            promotion.setHome(dto.getHome());
            if (dto.getImage() != null && !dto.getImage().trim().isEmpty()) {
                promotion.setImage(dto.getImage());
            }

            if (dto.getLogo() != null && !dto.getLogo().trim().isEmpty()) {
                promotion.setLogo(dto.getLogo());
            }
            
            List<Long> categoryIds = dto.getCategoryIds();
            // Actualizar categorías
            if (categoryIds != null) {
                // Borrar categorías antiguas
                promotionsCategoriesRepository.deleteAll(promotion.getPromotionsCategories());

                // Insertar las nuevas
                List<PromotionsCategories> promotionsCategories = categoryIds.stream()
                    .map(catId -> {
                        PromotionCategory promotionCategory = promotionCategoryRepository.findById(catId)
                                .orElseThrow(() -> new RuntimeException("Categoria de promoción no encontrada id " + catId));
                        PromotionsCategories pc = new PromotionsCategories();
                        pc.setPromotion(promotion);
                        pc.setPromotionCategory(promotionCategory);
                        return pc;
                    })
                    .toList();

                promotionsCategoriesRepository.saveAll(promotionsCategories);
                promotion.getPromotionsCategories().clear();
                promotion.getPromotionsCategories().addAll(promotionsCategories);
            }

            Promotion updatedPromotion = promotionService.savePromotion(promotion);

            String successMessage = "Promoción actualizada con ID " + updatedPromotion.getId();
            ApiResponseDto<Promotion> response = new ApiResponseDto<>(successMessage, updatedPromotion, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar promoción: " + e.getMessage();
            ApiResponseDto<Promotion> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/promotion/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deletePromotion(@PathVariable Long id) {
        try {
           Promotion existingPromotion = promotionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Promoción no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingPromotion.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("La promoción ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingPromotion.setDeletedAt(ZonedDateTime.now());
            promotionRepository.save(existingPromotion);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Promoción eliminada con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar la promoción: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    
    @PostMapping(value = "/cms/promotion/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    
    @PostMapping(value = "/cms/promotion/image-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
