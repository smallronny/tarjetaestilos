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
import pe.tarjetaestilos.models.CorporateData;
import pe.tarjetaestilos.models.Menu;
import pe.tarjetaestilos.services.CorporateDataService;
import pe.tarjetaestilos.dto.corporateData.CorporateDataCreateDto;
import pe.tarjetaestilos.dto.corporateData.CorporateDataSearchDto;
import pe.tarjetaestilos.repositories.CorporateDataRepository;
import pe.tarjetaestilos.repositories.MenuRepository;
import pe.tarjetaestilos.dto.ApiResponseDto;
/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos")
public class CorporateDataController {
    private final CorporateDataRepository corporateDataRepository;
    private final MenuRepository menuRepository;

    public CorporateDataController(
            CorporateDataRepository corporateDataRepository,
            MenuRepository menuRepository
    ) {
        this.corporateDataRepository = corporateDataRepository;
        this.menuRepository = menuRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(CorporateDataController.class);
    
    @Autowired
    private CorporateDataService corporateDataService;
    
    
    @GetMapping("/cms/corporate-data-all") 
    public List<CorporateData> getCorporateDataAll(){
        List<CorporateData> corporateDatas = this.corporateDataService.getCorporateDataAll();
        logger.info("registos obtenidos:");
        corporateDatas.forEach(corporateData-> logger.info(corporateData.toString()));
        return corporateDatas;
    }
    
    @GetMapping("/cms/corporate-data/{page}/{size}") 
    public Page<CorporateData> getCorporateData(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return corporateDataService.getCorporateData(pageable);
    }
    
    @PostMapping("/cms/corporate-data/search")
    public Page<CorporateData> searchCorporateData(@RequestBody CorporateDataSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return corporateDataService.search(dto.getTitle(),dto.getDescription(),dto.getMenu_id(), pageable);
    }
    
    
    @PostMapping(value = "/cms/corporate-data/create")
    public ResponseEntity<ApiResponseDto<CorporateData>> createCorporateData(
            @Valid @RequestBody CorporateDataCreateDto dto
    ) {
        try {
            CorporateData corporateDatas = new CorporateData();
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            
            corporateDatas.setTitle(dto.getTitle());
            corporateDatas.setDescription(dto.getDescription());
            corporateDatas.setIcon(dto.getIcon());
            corporateDatas.setMenu(menu);
            
            CorporateData savedCorporateData = corporateDataService.saveCorporateData(corporateDatas);
            

            CorporateData newCorporateData = corporateDataService.getCorporateDataById(savedCorporateData.getId());

            String successMessage = "CorporateData registrado con ID " + savedCorporateData.getId();
            ApiResponseDto<CorporateData> response = new ApiResponseDto<>(successMessage, newCorporateData, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar CorporateData: " + e.getMessage();
            ApiResponseDto<CorporateData> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/corporate-data/update/{id}")
    public ResponseEntity<ApiResponseDto<CorporateData>> updatePageContent(
            @PathVariable Long id,
            @Valid @RequestBody CorporateDataCreateDto dto
    ) {
        try {
            CorporateData corporateData = corporateDataRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("CorporateData no encontrada con id " + id));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu  no encontrada id " + dto.getMenu_id()));
            
            corporateData.setTitle(dto.getTitle());
            corporateData.setDescription(dto.getDescription());
            corporateData.setIcon(dto.getIcon());
            corporateData.setMenu(menu);
            
            
            CorporateData updatedCorporateData = corporateDataService.saveCorporateData(corporateData);

            String successMessage = "CorporateData actualizado con ID " + updatedCorporateData.getId();
            ApiResponseDto<CorporateData> response = new ApiResponseDto<>(successMessage, updatedCorporateData, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar CorporateData: " + e.getMessage();
            ApiResponseDto<CorporateData> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/corporate-data/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteCorporateData(@PathVariable Long id) {
        try {
           CorporateData existingCorporateData = corporateDataRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("CorporateData no encontrado con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingCorporateData.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("CorporateData ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingCorporateData.setDeletedAt(ZonedDateTime.now());
            corporateDataRepository.save(existingCorporateData);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "CorporateData eliminado con ID " + id,
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
}
