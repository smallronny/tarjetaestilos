/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.Affiliates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author user
 */
public interface IAffiliatesService {
    Page<Affiliates> getAffiliates(Pageable pageable);
    
    List<Affiliates> getAffiliatesAll();
    
    Affiliates getAffiliatesById(Long id);
    
    Affiliates saveAffiliates(Affiliates affiliates);
    
    void deleteAffiliatesById(Long id);
}
