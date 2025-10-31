/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import pe.tarjetaestilos.models.FrequentlyAskedQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author user
 */
public interface IFrequentlyAskedQuestionService {
    
    Page<FrequentlyAskedQuestion> getFrequentlyAskedQuestion(Pageable pageable);
    
    List<FrequentlyAskedQuestion> getFrequentlyAskedQuestionAll();
    
    FrequentlyAskedQuestion getFrequentlyAskedQuestionById(Long id);
    
    FrequentlyAskedQuestion saveFrequentlyAskedQuestion(FrequentlyAskedQuestion blog);
    
    void deleteFrequentlyAskedQuestionById(Long id);
}
