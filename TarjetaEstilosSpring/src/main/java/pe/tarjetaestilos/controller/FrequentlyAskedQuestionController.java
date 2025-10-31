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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.time.ZonedDateTime;

//import tarjeta
import pe.tarjetaestilos.models.FrequentlyAskedQuestion;
import pe.tarjetaestilos.models.Menu;
import pe.tarjetaestilos.services.FrequentlyAskedQuestionService;
import pe.tarjetaestilos.dto.faq.FaqSearchDto;
import pe.tarjetaestilos.dto.faq.FaqCreateDto;
import pe.tarjetaestilos.repositories.FrequentlyAskedQuestionRepository;
import pe.tarjetaestilos.repositories.MenuRepository;
import pe.tarjetaestilos.dto.ApiResponseDto;
/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos") //http://localhost:8081/tarjeta-estilos
@CrossOrigin(value="http://localhost:3000") //puerto de vue
public class FrequentlyAskedQuestionController {
    private final FrequentlyAskedQuestionRepository frequentlyAskedQuestionRepository;
    private final MenuRepository menuRepository;
    
    public FrequentlyAskedQuestionController(
            FrequentlyAskedQuestionRepository frequentlyAskedQuestionRepository,
            MenuRepository menuRepository
    ) {
        this.frequentlyAskedQuestionRepository = frequentlyAskedQuestionRepository;
        this.menuRepository = menuRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(FrequentlyAskedQuestionController.class);
    
    @Autowired
    private FrequentlyAskedQuestionService frequentlyAskedQuestionService;
    
    
    @GetMapping("/web/faq/byMenu/{slug}")
    public ResponseEntity<List<FrequentlyAskedQuestion>> getFaqByMenuSlug(@PathVariable String slug) {
        List<FrequentlyAskedQuestion> faq = frequentlyAskedQuestionService.getFaqByMenuSlug(slug);

        if (faq.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(faq);
    }
    
    
    @GetMapping("/cms/faq") 
    public List<FrequentlyAskedQuestion> getDepartmentsAll(){
        List<FrequentlyAskedQuestion> frequentlyAskedQuestions = this.frequentlyAskedQuestionService.getFrequentlyAskedQuestionAll();
        logger.info("registos obtenidos:");
        frequentlyAskedQuestions.forEach(frequentlyAskedQuestion-> logger.info(frequentlyAskedQuestion.toString()));
        return frequentlyAskedQuestions;
    }
    
    @GetMapping("/cms/faq/{page}/{size}") 
    public Page<FrequentlyAskedQuestion> getShortcuts(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return frequentlyAskedQuestionService.getFrequentlyAskedQuestion(pageable);
    }
    
    @PostMapping("/cms/faq/search")
    public Page<FrequentlyAskedQuestion> searchFaqs(@RequestBody FaqSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return frequentlyAskedQuestionService.search(dto.getQuestion(),dto.getAnswer(),dto.getMenu_id(), pageable);
    }
    
    @PostMapping(value = "/cms/faq/create")
    public ResponseEntity<ApiResponseDto<FrequentlyAskedQuestion>> createFrequentlyAskedQuestion(
            @Valid @RequestBody FaqCreateDto dto
    ) {
        try {
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            
            FrequentlyAskedQuestion frequentlyAskedQuestion = new FrequentlyAskedQuestion();
            frequentlyAskedQuestion.setAnswer(dto.getAnswer());
            frequentlyAskedQuestion.setQuestion(dto.getQuestion());
            frequentlyAskedQuestion.setMenu(menu);
            
            
            FrequentlyAskedQuestion savedFrequentlyAskedQuestion = frequentlyAskedQuestionService.saveFrequentlyAskedQuestion(frequentlyAskedQuestion);                        
            FrequentlyAskedQuestion newFrequentlyAskedQuestion = frequentlyAskedQuestionService.getFrequentlyAskedQuestionById(savedFrequentlyAskedQuestion.getId());
            
            String successMessage = "Pregunta registrada con ID " + savedFrequentlyAskedQuestion.getId();
            ApiResponseDto<FrequentlyAskedQuestion> response = new ApiResponseDto<>(successMessage, newFrequentlyAskedQuestion, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            String errorMessage = "Error al registrar noticia: " + e.getMessage();
            ApiResponseDto<FrequentlyAskedQuestion> response = new ApiResponseDto<>(errorMessage, null,HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping(value = "/cms/faq/update/{id}")
    public ResponseEntity<ApiResponseDto<FrequentlyAskedQuestion>> updateFacility(
            @PathVariable Long id,
            @Valid @RequestBody FaqCreateDto dto
    ) {
        try {
            FrequentlyAskedQuestion existingFrequentlyAskedQuestion = frequentlyAskedQuestionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pregunta no encontrada con id " + id));
            
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            
            existingFrequentlyAskedQuestion.setAnswer(dto.getAnswer());
            existingFrequentlyAskedQuestion.setQuestion(dto.getQuestion());
            existingFrequentlyAskedQuestion.setMenu(menu);
                     
            FrequentlyAskedQuestion updatedFrequentlyAskedQuestion = frequentlyAskedQuestionService.saveFrequentlyAskedQuestion(existingFrequentlyAskedQuestion);

            String successMessage = "Facilidad actualizada con ID " + updatedFrequentlyAskedQuestion.getId();
            ApiResponseDto<FrequentlyAskedQuestion> response = new ApiResponseDto<>(successMessage, updatedFrequentlyAskedQuestion, HttpStatus.OK.value());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar facilidad: " + e.getMessage();
            ApiResponseDto<FrequentlyAskedQuestion> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/cms/faq/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteFrequentlyAskedQuestion(@PathVariable Long id) {
        try {
            FrequentlyAskedQuestion existingFrequentlyAskedQuestion = frequentlyAskedQuestionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pregunta no encontrada con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingFrequentlyAskedQuestion.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("La pregunta ya fue eliminada", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingFrequentlyAskedQuestion.setDeletedAt(ZonedDateTime.now());
            frequentlyAskedQuestionRepository.save(existingFrequentlyAskedQuestion);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Pregunta eliminada con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar pregunta: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}
