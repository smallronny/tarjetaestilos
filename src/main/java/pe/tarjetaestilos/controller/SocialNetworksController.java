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


//import de proyecto
import pe.tarjetaestilos.dto.ApiResponseDto;
import pe.tarjetaestilos.services.SocialNetworksService;
import pe.tarjetaestilos.repositories.SocialNetworksRepository;
import pe.tarjetaestilos.dto.socialNetworks.SocialNetworksCreateDto;
import pe.tarjetaestilos.dto.socialNetworks.SocialNetworksSearchDto;
import pe.tarjetaestilos.models.SocialNetworks;

/**
 *
 * @author julito
 */
@RestController
@RequestMapping("tarjeta-estilos")
public class SocialNetworksController {
    
    private final SocialNetworksRepository socialNetworksRepository;
    
    public SocialNetworksController(
            SocialNetworksRepository socialNetworksRepository
    ){
        this.socialNetworksRepository = socialNetworksRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(SocialNetworksRepository.class);
    
    @Autowired
    private SocialNetworksService socialNetworksService;
    
    @GetMapping("/cms/socialNetworksAll") 
    public List<SocialNetworks> getSocialNetworksAll(){
        List<SocialNetworks> socialNetworks = this.socialNetworksService.getSocialNetworksAll();
        logger.info("registos obtenidos:");
        socialNetworks.forEach(soN-> logger.info(soN.toString()));
        return socialNetworks;
    }
    
    @GetMapping("/cms/socialNetworks/{page}/{size}") 
    public Page<SocialNetworks> getSocialNetworks(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return socialNetworksService.getSocialNetworks(pageable);
    }
    
    @PostMapping(value = "/cms/socialNetworks/create")
    public ResponseEntity<ApiResponseDto<SocialNetworks>> createSocialNetworks(
            @Valid @RequestBody SocialNetworksCreateDto dto
    ) {
        try {
            SocialNetworks socialNetworks = new SocialNetworks();
            
            socialNetworks.setTitle(dto.getTitle());
            socialNetworks.setIcono(dto.getIcono());
            
            SocialNetworks savedSocialNetworks = socialNetworksService.saveSocialNetworks(socialNetworks);
            

            SocialNetworks newSocialNetworks = socialNetworksService.getSocialNetworksById(savedSocialNetworks.getId());

            String successMessage = "Red social registrada con ID " + savedSocialNetworks.getId();
            ApiResponseDto<SocialNetworks> response = new ApiResponseDto<>(successMessage, newSocialNetworks, HttpStatus.CREATED.value());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar red social: " + e.getMessage();
            ApiResponseDto<SocialNetworks> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    
    @PutMapping(value = "/cms/socialNetworks/update/{id}")
    public ResponseEntity<ApiResponseDto<SocialNetworks>> updateSocialNetworks(
            @PathVariable Long id,
            @Valid @RequestBody SocialNetworksCreateDto dto
    ) {
        try {
            SocialNetworks socialNetworks = socialNetworksRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Red social no encontrada con id " + id));
            
            
            socialNetworks.setTitle(dto.getTitle());
            socialNetworks.setIcono(dto.getIcono());
            
            SocialNetworks updatedSocialNetworks = socialNetworksService.saveSocialNetworks(socialNetworks);

            String successMessage = "Red social actualizado con ID " + updatedSocialNetworks.getId();
            ApiResponseDto<SocialNetworks> response = new ApiResponseDto<>(successMessage, updatedSocialNetworks, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar PageContent: " + e.getMessage();
            ApiResponseDto<SocialNetworks> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/socialNetworks/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deletePageContent(@PathVariable Long id) {
        try {
           SocialNetworks existingSocialNetworks = socialNetworksRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Red social no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingSocialNetworks.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("Red social ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingSocialNetworks.setDeletedAt(ZonedDateTime.now());
            socialNetworksRepository.save(existingSocialNetworks);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Red social eliminado con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar la red social: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}
