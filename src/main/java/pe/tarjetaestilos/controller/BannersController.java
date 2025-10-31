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
import pe.tarjetaestilos.models.Banners;
import pe.tarjetaestilos.models.Button;
import pe.tarjetaestilos.services.BannersService;
import pe.tarjetaestilos.dto.banners.BannersSearchDto;
import pe.tarjetaestilos.dto.banners.BannersCreateDto;
import pe.tarjetaestilos.repositories.BannersRepository;
import pe.tarjetaestilos.repositories.MenuRepository;
import pe.tarjetaestilos.repositories.ButtonRepository;
import pe.tarjetaestilos.dto.ApiResponseDto;
import pe.tarjetaestilos.dto.button.ButtonCreateDto;
import pe.tarjetaestilos.models.Menu;

/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos") 
public class BannersController {
    private final BannersRepository bannersRepository;
    private final MenuRepository menuRepository;
    private final ButtonRepository buttonRepository;

    public BannersController(
            BannersRepository bannersRepository,
            MenuRepository menuRepository,
            ButtonRepository buttonRepository
    ) {
        this.bannersRepository = bannersRepository;
        this.menuRepository = menuRepository;
        this.buttonRepository = buttonRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(BannersController.class);
    
    @Autowired
    private BannersService bannersService;
    
    @GetMapping("/web/banners/byMenu/{slug}")
    public ResponseEntity<List<Banners>> getBannersByMenuSlug(@PathVariable String slug) {
        List<Banners> banners = bannersService.getBannersByMenuSlug(slug);

        if (banners.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(banners);
    }
    
    @GetMapping("/cms/banners/all") 
    public List<Banners> getBannersAll(){
        List<Banners> banners = this.bannersService.getBannersAll();
        logger.info("registos obtenidos:");
        banners.forEach(banner-> logger.info(banner.toString()));
        return banners;
    }
    
    @GetMapping("/cms/banners/{page}/{size}") 
    public Page<Banners> getBanners(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return bannersService.getBanners(pageable);
    }
    
    @PostMapping("/cms/banners/search")
    public Page<Banners> searchBanners(@RequestBody BannersSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return bannersService.search(dto.getTitle(),dto.getSubtitle(),dto.getMenu_id(),dto.getBlog_form(), pageable);
    }
    
    
    @PostMapping(value = "/cms/banners/create")
    public ResponseEntity<ApiResponseDto<Banners>> createBanners(
            @Valid @RequestBody BannersCreateDto dto
    ) {
        try {
            Banners banners = new Banners();
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            banners.setTitle(dto.getTitle());
            banners.setSubtitle(dto.getSubtitle());
            banners.setImage(dto.getImage());
            banners.setMenu(menu);
            banners.setLabel(dto.getLabel());
            banners.setBlog_form(dto.getBlog_form());
            
            Banners savedBanners = bannersService.saveBanners(banners);
            
           
            List<ButtonCreateDto> buttonCreateDtos = dto.getButtons();

            if (buttonCreateDtos != null && !buttonCreateDtos.isEmpty()) {
                List<Button> button = buttonCreateDtos.stream()
                    .map(netId -> {
                        
                        Button bc = new Button();
                        bc.setBanners(savedBanners);
                        bc.setLink(netId.getLink());
                        bc.setTitle(netId.getTitle());
                        bc.setAdd_class(netId.getAdd_class());
                        return bc;
                    })
                    .toList();
                buttonRepository.saveAll(button);
                savedBanners.getButtons().addAll(button);

            }

            String successMessage = "Banner registrado con ID " + savedBanners.getId();
            ApiResponseDto<Banners> response = new ApiResponseDto<>(successMessage, savedBanners, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar banner: " + e.getMessage();
            ApiResponseDto<Banners> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/banners/update/{id}")
    public ResponseEntity<ApiResponseDto<Banners>> updateBanners(
            @PathVariable Long id,
            @Valid @RequestBody BannersCreateDto dto
    ) {
        try {
            Banners banners = bannersRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Banner no encontrada con id " + id));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu  no encontrada id " + dto.getMenu_id()));
            banners.setTitle(dto.getTitle());
            banners.setSubtitle(dto.getSubtitle());
            banners.setMenu(menu);
            banners.setLabel(dto.getLabel());
            banners.setBlog_form(dto.getBlog_form());
            
            if (dto.getImage() != null && !dto.getImage().trim().isEmpty()) {
                banners.setImage(dto.getImage());
            }
            
            List<ButtonCreateDto> buttonCreateDtos = dto.getButtons();

            if (buttonCreateDtos != null && !buttonCreateDtos.isEmpty()) {
                
                buttonRepository.deleteAll(banners.getButtons());
                
                List<Button> button = buttonCreateDtos.stream()
                    .map(netId -> {
                        
                        Button bc = new Button();
                        bc.setBanners(banners);
                        bc.setLink(netId.getLink());
                        bc.setTitle(netId.getTitle());
                        bc.setAdd_class(netId.getAdd_class());
                        return bc;
                    })
                    .toList();
                buttonRepository.saveAll(button);
                banners.getButtons().clear();
                banners.getButtons().addAll(button);

            }
            
            Banners updatedBanners = bannersService.saveBanners(banners);

            String successMessage = "Banner actualizado con ID " + updatedBanners.getId();
            ApiResponseDto<Banners> response = new ApiResponseDto<>(successMessage, updatedBanners, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar banner: " + e.getMessage();
            ApiResponseDto<Banners> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/banners/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteBanners(@PathVariable Long id) {
        try {
           Banners existingBanners = bannersRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Banner no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingBanners.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("Banner ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingBanners.setDeletedAt(ZonedDateTime.now());
            bannersRepository.save(existingBanners);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Banner eliminado con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar el banner: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping(value = "/cms/banners/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<String>> createImage(
        @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            Path uploadPath = Paths.get("uploads/banners/image/");
            
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
