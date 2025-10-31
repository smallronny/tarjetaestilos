/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.Promotion;
import pe.tarjetaestilos.repositories.PromotionRepository;

/**
 *
 * @author julito
 */

@Service
public class PromotionService implements IPromotionService{
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    @Override
    public Page<Promotion> getPromotion(Pageable pageable) {
        return this.promotionRepository.findAll(pageable);
    }
    
    public List<Promotion> getPromotionByMenuSlug(String slug) {
        return promotionRepository.findByMenuSlug(slug);
    }
    
    public Page<Promotion> search(
        String title,
        String description,
        String discount,
        Boolean isnew,
        Long departmentId,
        Boolean home,
        Boolean exclusive,
        Pageable pageable
    ) {
        return promotionRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (description != null && !description.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            if (discount != null) {
                predicates.add(cb.equal(root.get("discount"), discount));
            }

            if (isnew != null) {
                predicates.add(cb.equal(root.get("isnew"), isnew)); 
            }
            
            if (exclusive != null) {
                predicates.add(cb.equal(root.get("exclusive"), exclusive)); 
            }
            
            if (home != null) {
                predicates.add(cb.equal(root.get("home"), home)); 
            }

            if (departmentId != null) {
                predicates.add(cb.equal(root.get("department").get("id"), departmentId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public List<Promotion> getPromotionAll() {
        return this.promotionRepository.findAll();
    }

    @Override
    public Promotion getPromotionById(Long id) {
        Promotion promotion = this.promotionRepository.findById(id).orElse(null);
        return promotion;
    }
    
    public Promotion getNewPromotionById(Long id) {
        return promotionRepository.findByIdWithCategories(id)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada con id " + id));
    }

    @Override
    public Promotion savePromotion(Promotion promotion) {
        return this.promotionRepository.save(promotion);
    }

    @Override
    public void deletePromotionById(Long id) {
        this.promotionRepository.deleteById(id);
    }
}
