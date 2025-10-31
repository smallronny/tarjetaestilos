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
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


//imports tarjetas
import pe.tarjetaestilos.models.Button;
import pe.tarjetaestilos.models.Banners;
import pe.tarjetaestilos.services.ButtonService;
import pe.tarjetaestilos.dto.button.ButtonCreateDto;
import pe.tarjetaestilos.repositories.BannersRepository;
import pe.tarjetaestilos.repositories.ButtonRepository;
import pe.tarjetaestilos.dto.ApiResponseDto;
import pe.tarjetaestilos.dto.ImageResponseDto;

/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos") 
public class ButtonController {
    
    private final BannersRepository bannersRepository;
    private final ButtonRepository buttonRepository;

    public ButtonController(
            BannersRepository bannersRepository,
            ButtonRepository buttonRepository
    ) {
        this.bannersRepository = bannersRepository;
        this.buttonRepository = buttonRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(ButtonController.class);
    
    
    @Autowired
    private ButtonService buttonService;
    
    @GetMapping("/cms/buttonAll") 
    public List<Button> getButtonAll(){
        List<Button> buttons = this.buttonService.getButtonAll();
        logger.info("registos obtenidos:");
        buttons.forEach(button-> logger.info(button.toString()));
        return buttons;
    }
    
    
    @PostMapping(value = "/cms/buttons/create")
    public ResponseEntity<ApiResponseDto<Button>> createButton(
            @Valid @RequestBody ButtonCreateDto dto
    ) {
        try {
            Button button = new Button();
            Banners banners = bannersRepository.findById(dto.getBanners_id())
                    .orElseThrow(() -> new RuntimeException("Banner no encontrado id " + dto.getBanners_id()));
            button.setTitle(dto.getTitle());
            button.setAdd_class(dto.getAdd_class());
            button.setLink(dto.getLink());
            button.setBanners(banners);
            
            Button savedButton = buttonService.saveButton(button);
            

            Button newButton = buttonService.getButtonById(savedButton.getId());

            String successMessage = "Button registrado con ID " + savedButton.getId();
            ApiResponseDto<Button> response = new ApiResponseDto<>(successMessage, newButton, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar button: " + e.getMessage();
            ApiResponseDto<Button> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/buttons/update/{id}")
    public ResponseEntity<ApiResponseDto<Button>> updateButton(
            @PathVariable Long id,
            @Valid @RequestBody ButtonCreateDto dto
    ) {
        try {
            Button button = buttonRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Button no encontrado con id " + id));
            
            Banners banners = bannersRepository.findById(dto.getBanners_id())
                    .orElseThrow(() -> new RuntimeException("Banner no encontrado id " + dto.getBanners_id()));
            
            button.setTitle(dto.getTitle());
            button.setAdd_class(dto.getAdd_class());
            button.setLink(dto.getLink());
            button.setBanners(banners);
            
            
            Button updatedButton = buttonService.saveButton(button);

            String successMessage = "Button actualizado con ID " + updatedButton.getId();
            ApiResponseDto<Button> response = new ApiResponseDto<>(successMessage, updatedButton, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar banner: " + e.getMessage();
            ApiResponseDto<Button> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/button/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteButton(@PathVariable Long id) {
        try {
           Button existingButton = buttonRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Button no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingButton.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("Button ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingButton.setDeletedAt(ZonedDateTime.now());
            buttonRepository.save(existingButton);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Banner eliminado con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar el botón: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}
