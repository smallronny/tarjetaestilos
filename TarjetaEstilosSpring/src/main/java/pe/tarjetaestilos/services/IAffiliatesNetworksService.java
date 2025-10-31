/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.AffiliatesNetworks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */
public interface IAffiliatesNetworksService {
    Page<AffiliatesNetworks> getAffiliatesNetworks(Pageable pageable);
    
    List<AffiliatesNetworks> getAffiliatesNetworksAll();
    
    AffiliatesNetworks getAffiliatesNetworksById(Long id);
    
    AffiliatesNetworks saveAffiliatesNetworks(AffiliatesNetworks affiliatesNetworks);
    
    void deleteAffiliatesNetworksById(Long id);
}
