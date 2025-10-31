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

import pe.tarjetaestilos.models.Affiliates;
import pe.tarjetaestilos.models.AffiliatesNetworks;
import pe.tarjetaestilos.models.SocialNetworks;
import pe.tarjetaestilos.services.AffiliatesService;
import pe.tarjetaestilos.dto.affiliates.AffiliatesCreateDto;
import pe.tarjetaestilos.dto.affiliates.AffiliatesSearchDto;
import pe.tarjetaestilos.dto.affiliates.AffiliatesNetworkDto;
import pe.tarjetaestilos.repositories.AffiliatesRepository;
import pe.tarjetaestilos.repositories.AffiliatesNetworksRepository;
import pe.tarjetaestilos.repositories.SocialNetworksRepository;
import pe.tarjetaestilos.dto.ImageResponseDto;

/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos")
public class AffiliatesController {
    private final AffiliatesRepository affiliatesRepository;
    private final AffiliatesNetworksRepository affiliatesNetworksRepository;
    private final SocialNetworksRepository socialNetworksRepository;
    
    public AffiliatesController(
       AffiliatesRepository affiliatesRepository,
       AffiliatesNetworksRepository affiliatesNetworksRepository,
       SocialNetworksRepository socialNetworksRepository
    ){
        this.affiliatesNetworksRepository = affiliatesNetworksRepository;
        this.affiliatesRepository = affiliatesRepository;
        this.socialNetworksRepository = socialNetworksRepository;
    }
    private static final Logger logger = LoggerFactory.getLogger(AffiliatesController.class);
    
    @Autowired
    private AffiliatesService affiliatesService;
    
    
    @GetMapping("/cms/affiliates-all") 
    public List<Affiliates> getAffiliatesAll(){
        List<Affiliates> affiliates = this.affiliatesService.getAffiliatesAll();
        logger.info("registos obtenidos:");
        affiliates.forEach(affiliate-> logger.info(affiliate.toString()));
        return affiliates;
    }
    
    @GetMapping("/cms/affiliates/{page}/{size}") 
    public Page<Affiliates> getBlogs(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size,Sort.by("id").descending());
        return affiliatesService.getAffiliates(pageable);
    }
    
    @PostMapping("/cms/affiliates/search")
    public Page<Affiliates> searchBlog(@RequestBody AffiliatesSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );
        return affiliatesService.search(dto.getTitle(), pageable);
    }
    
    
    @PostMapping(value = "/cms/affiliates/create")
    public ResponseEntity<ApiResponseDto<Affiliates>> createAffiliates(
            @Valid @RequestBody AffiliatesCreateDto dto
    ) {
        try {
            Affiliates affiliates = new Affiliates();
            
            affiliates.setTitle(dto.getTitle());
            affiliates.setImage(dto.getImage());
            
            Affiliates savedAffiliates = affiliatesService.saveAffiliates(affiliates);
            
            List<AffiliatesNetworkDto> networks = dto.getNetworks();

            if (networks != null && !networks.isEmpty()) {
                List<AffiliatesNetworks> affiliatesNetworks = networks.stream()
                    .map(netId -> {
                        SocialNetworks socialNetworks = socialNetworksRepository.findById(netId.getId())
                                .orElseThrow(() -> new RuntimeException("Red social no encontrada id " + netId));
                        AffiliatesNetworks bc = new AffiliatesNetworks();
                        bc.setAffiliates(savedAffiliates);
                        bc.setLink(netId.getLink());
                        bc.setSocialNetworks(socialNetworks);
                        return bc;
                    })
                    .toList();
                affiliatesNetworksRepository.saveAll(affiliatesNetworks);
                savedAffiliates.getAffiliatesNetworks().addAll(affiliatesNetworks);
            }

            Affiliates newAffiliates = affiliatesService.getAffiliatesById(savedAffiliates.getId());

            String successMessage = "Afiliado registrado con ID " + savedAffiliates.getId();
            ApiResponseDto<Affiliates> response = new ApiResponseDto<>(successMessage, newAffiliates, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar afiliado: " + e.getMessage();
            ApiResponseDto<Affiliates> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/affiliates/update/{id}")
    public ResponseEntity<ApiResponseDto<Affiliates>> updatePromotion(
            @PathVariable Long id,
            @Valid @RequestBody AffiliatesCreateDto dto
    ) {
        try {
            Affiliates affiliates = affiliatesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Afiliado no encontrado con id " + id));
            affiliates.setTitle(dto.getTitle());                        
            
            if (dto.getImage() != null && !dto.getImage().trim().isEmpty()) {
                affiliates.setImage(dto.getImage());
            }
            List<AffiliatesNetworkDto> networks = dto.getNetworks();
            // Actualizar categorías
            if (networks != null) {
                // Borrar categorías antiguas
                affiliatesNetworksRepository.deleteAll(affiliates.getAffiliatesNetworks());

                // Insertar las nuevas
                List<AffiliatesNetworks> affiliatesNetworks = networks.stream()
                    .map(catId -> {
                        SocialNetworks socialNetworks = socialNetworksRepository.findById(catId.getId())
                                .orElseThrow(() -> new RuntimeException("Red no encontrada id " + catId));
                        AffiliatesNetworks pc = new AffiliatesNetworks();
                        pc.setAffiliates(affiliates);
                        pc.setSocialNetworks(socialNetworks);
                        return pc;
                    })
                    .toList();

                affiliatesNetworksRepository.saveAll(affiliatesNetworks);
                affiliates.getAffiliatesNetworks().clear();
                affiliates.getAffiliatesNetworks().addAll(affiliatesNetworks);
            }

            Affiliates updatedAffiliates = affiliatesService.saveAffiliates(affiliates);

            String successMessage = "Promoción actualizada con ID " + updatedAffiliates.getId();
            ApiResponseDto<Affiliates> response = new ApiResponseDto<>(successMessage, updatedAffiliates, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar promoción: " + e.getMessage();
            ApiResponseDto<Affiliates> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/affiliates/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deletePromotion(@PathVariable Long id) {
        try {
           Affiliates existingAffiliates = affiliatesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Afiliado no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingAffiliates.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("El afiliado ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingAffiliates.setDeletedAt(ZonedDateTime.now());
            affiliatesRepository.save(existingAffiliates);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Afiliado eliminado con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar el afiliado: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping(value = "/cms/affiliates/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<String>> createImage(
        @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            Path uploadPath = Paths.get("uploads/affiliates/image/");
            
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
            String errorMessage = "Error al registrar noticia: " + e.getMessage();
            ApiResponseDto<String> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}
