/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;

/**
 *
 * @author user
 */
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.Facility;
import pe.tarjetaestilos.repositories.FacilityRepository;

@Service
public class FacilityService implements IFacilityService{
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Override
    public Page<Facility> getFacility(Pageable pageable) {
        return this.facilityRepository.findAll(pageable);
    }
    
    
    public List<Facility> getFacilityByMenuSlug(String slug) {
        return facilityRepository.findByMenuSlug(slug);
    }
    
    public Page<Facility> search(String title, Pageable pageable) {
        if (title != null && !title.isEmpty()) {
            return facilityRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else {
            return facilityRepository.findAll(pageable);
        }
    }
    
    @Override
    public List<Facility> getFacilityAll() {
        return this.facilityRepository.findAll();
    }

    @Override
    public Facility getFacilityById(Long id) {
        Facility facility = this.facilityRepository.findById(id).orElse(null);
        return facility;
    }
    
    
    
    @Override
    public Facility saveFacility(Facility facility) {
        return this.facilityRepository.save(facility);
    }

    @Override
    public void deleteFacilityById(Long id) {
        this.facilityRepository.deleteById(id);
    }
}
