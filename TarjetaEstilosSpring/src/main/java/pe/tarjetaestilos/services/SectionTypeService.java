/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.SectionType;
import pe.tarjetaestilos.repositories.SectionTypeRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author julito
 */

@Service
public class SectionTypeService implements ISectionTypeService{
    @Autowired
    private SectionTypeRepository sectionTypeRepository;
    
    @Override
    public Page<SectionType> getSectionType(Pageable pageable) {
        return this.sectionTypeRepository.findAll(pageable);
    }
    
    public Page<SectionType> search(
        String title,
        Pageable pageable
    ) {
        return sectionTypeRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }           

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public List<SectionType> getSectionTypeAll() {
        return this.sectionTypeRepository.findAll();
    }

    @Override
    public SectionType getSectionTypeById(Long id) {
        SectionType sectionType = this.sectionTypeRepository.findById(id).orElse(null);
        return sectionType;
    }

    @Override
    public SectionType saveSectionType(SectionType sectionType) {
        return this.sectionTypeRepository.save(sectionType);
    }

    @Override
    public void deleteSectionTypeById(Long id) {
        this.sectionTypeRepository.deleteById(id);
    }
}
