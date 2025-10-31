/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.Cta;
import pe.tarjetaestilos.repositories.CtaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
/**
 *
 * @author julito
 */
@Service
public class CtaService implements ICtaService{
    @Autowired
    private CtaRepository ctaRepository;
    
    @Override
    public Page<Cta> getCta(Pageable pageable) {
        return this.ctaRepository.findAll(pageable);
    }
    
    public List<Cta> getCtaByMenuSlug(String slug) {
        return ctaRepository.findByMenuSlug(slug);
    }
    
    public Page<Cta> search(
        String url,
        Long menuId,
        Pageable pageable
    ) {
        return ctaRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (url != null && !url.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("url")), "%" + url.toLowerCase() + "%"));
            }

            if (menuId != null) {
                predicates.add(cb.equal(root.get("menu").get("id"), menuId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public List<Cta> getCtaAll() {
        return this.ctaRepository.findAll();
    }

    @Override
    public Cta getCtaById(Long id) {
        Cta cta = this.ctaRepository.findById(id).orElse(null);
        return cta;
    }

    @Override
    public Cta saveCta(Cta cta) {
        return this.ctaRepository.save(cta);
    }

    @Override
    public void deleteCtaById(Long id) {
        this.ctaRepository.deleteById(id);
    }
}
