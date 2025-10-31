/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import pe.tarjetaestilos.models.Facility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author julito
 */

public interface IFacilityService {
    Page<Facility> getFacility(Pageable pageable);
    
    List<Facility> getFacilityAll();
    
    Facility getFacilityById(Long id);
    
    Facility saveFacility(Facility blog);
    
    void deleteFacilityById(Long id);
}
