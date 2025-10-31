/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.Affiliates;
import pe.tarjetaestilos.repositories.AffiliatesRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
/**
 *
 * @author julito
 */
@Service
public class AffiliatesService implements IAffiliatesService{
    
    @Autowired
    private AffiliatesRepository affiliatesRepository;
    
    public Page<Affiliates> search(
        String title,
        Pageable pageable
    ) {
        return affiliatesRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }           

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public Page<Affiliates> getAffiliates(Pageable pageable) {
        return this.affiliatesRepository.findAll(pageable);
    }
    
    @Override
    public List<Affiliates> getAffiliatesAll() {
        return this.affiliatesRepository.findAll();
    }
    
    @Override
    public Affiliates getAffiliatesById(Long id) {
        Affiliates affiliates = this.affiliatesRepository.findById(id).orElse(null);
        return affiliates;
    }
    
    

    @Override
    public Affiliates saveAffiliates(Affiliates affiliates) {
        return this.affiliatesRepository.save(affiliates);
    }

    @Override
    public void deleteAffiliatesById(Long id) {
        this.affiliatesRepository.deleteById(id);
    }
}
