/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.SocialNetworks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */
public interface ISocialNetworksService {
    Page<SocialNetworks> getSocialNetworks(Pageable pageable);
    
    List<SocialNetworks> getSocialNetworksAll();
    
    SocialNetworks getSocialNetworksById(Long id);
    
    SocialNetworks saveSocialNetworks(SocialNetworks socialNetworks);
    
    void deleteSocialNetworksById(Long id);
}
