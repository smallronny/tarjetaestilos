/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.CorporateData;
import pe.tarjetaestilos.repositories.CorporateDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
/**
 *
 * @author julito
 */
@Service
public class CorporateDataService implements ICorporateDataService{
    @Autowired
    private CorporateDataRepository corporateDataRepository;
    
    @Override
    public Page<CorporateData> getCorporateData(Pageable pageable) {
        return this.corporateDataRepository.findAll(pageable);
    }
    
    public Page<CorporateData> search(
        String title,
        String description,
        Long menuId,
        Pageable pageable
    ) {
        return corporateDataRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            
            if (description != null && !description.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            if (menuId != null) {
                predicates.add(cb.equal(root.get("menu").get("id"), menuId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public List<CorporateData> getCorporateDataAll() {
        return this.corporateDataRepository.findAll();
    }

    @Override
    public CorporateData getCorporateDataById(Long id) {
        CorporateData corporateData = this.corporateDataRepository.findById(id).orElse(null);
        return corporateData;
    }

    @Override
    public CorporateData saveCorporateData(CorporateData corporateData) {
        return this.corporateDataRepository.save(corporateData);
    }

    @Override
    public void deleteCorporateDataById(Long id) {
        this.corporateDataRepository.deleteById(id);
    }
}
