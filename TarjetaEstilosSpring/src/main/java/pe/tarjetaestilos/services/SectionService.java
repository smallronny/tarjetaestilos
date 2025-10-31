/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.Section;
import pe.tarjetaestilos.repositories.SectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author julito
 */

@Service
public class SectionService implements ISectionService{
    @Autowired
    private SectionRepository sectionRepository;
    
    @Override
    public Page<Section> getSection(Pageable pageable) {
        return this.sectionRepository.findAll(pageable);
    }
    
    public Page<Section> search(
        String title,
        Integer section_type_id,
        Pageable pageable
    ) {
        return sectionRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }           
            
            if (section_type_id != null) {
                predicates.add(cb.equal(root.get("sectionType").get("id"), section_type_id));
            }           

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public List<Section> getSectionAll() {
        return this.sectionRepository.findAll();
    }

    @Override
    public Section getSectionById(Long id) {
        Section section = this.sectionRepository.findById(id).orElse(null);
        return section;
    }

    @Override
    public Section saveSection(Section section) {
        return this.sectionRepository.save(section);
    }

    @Override
    public void deleteSectionById(Long id) {
        this.sectionRepository.deleteById(id);
    }
}
