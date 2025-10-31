/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;

/**
 *
 * @author julito
 */
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.FrequentlyAskedQuestion;
import pe.tarjetaestilos.repositories.FrequentlyAskedQuestionRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

@Service
public class FrequentlyAskedQuestionService implements IFrequentlyAskedQuestionService {
    @Autowired
    private FrequentlyAskedQuestionRepository frequentlyAskedQuestionRepository;
    
    @Override
    public Page<FrequentlyAskedQuestion> getFrequentlyAskedQuestion(Pageable pageable) {
        return this.frequentlyAskedQuestionRepository.findAll(pageable);
    }
    
    public List<FrequentlyAskedQuestion> getFaqByMenuSlug(String slug) {
        return frequentlyAskedQuestionRepository.findByMenuSlug(slug);
    }
    
    public Page<FrequentlyAskedQuestion> search(
        String question,
        String answer,
        Long menuId,
        Pageable pageable
    ) {
        return frequentlyAskedQuestionRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (question != null && !question.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("question")), "%" + question.toLowerCase() + "%"));
            }
            
            if (answer != null && !answer.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("answer")), "%" + answer.toLowerCase() + "%"));
            }

            if (menuId != null) {
                predicates.add(cb.equal(root.get("menu").get("id"), menuId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public List<FrequentlyAskedQuestion> getFrequentlyAskedQuestionAll() {
        return this.frequentlyAskedQuestionRepository.findAll();
    }

    @Override
    public FrequentlyAskedQuestion getFrequentlyAskedQuestionById(Long id) {
        FrequentlyAskedQuestion frequentlyAskedQuestion = this.frequentlyAskedQuestionRepository.findById(id).orElse(null);
        return frequentlyAskedQuestion;
    }

    @Override
    public FrequentlyAskedQuestion saveFrequentlyAskedQuestion(FrequentlyAskedQuestion frequentlyAskedQuestion) {
        return this.frequentlyAskedQuestionRepository.save(frequentlyAskedQuestion);
    }

    @Override
    public void deleteFrequentlyAskedQuestionById(Long id) {
        this.frequentlyAskedQuestionRepository.deleteById(id);
    }
}
