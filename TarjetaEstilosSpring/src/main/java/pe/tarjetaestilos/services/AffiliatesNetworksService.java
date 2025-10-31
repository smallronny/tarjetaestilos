/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.AffiliatesNetworks;
import pe.tarjetaestilos.repositories.AffiliatesNetworksRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author JULITO
 */
@Service
public class AffiliatesNetworksService implements IAffiliatesNetworksService{
    
    @Autowired
    private AffiliatesNetworksRepository affiliatesNetworksRepository;
    
    @Override
    public Page<AffiliatesNetworks> getAffiliatesNetworks(Pageable pageable) {
        return this.affiliatesNetworksRepository.findAll(pageable);
    }
    
    @Override
    public List<AffiliatesNetworks> getAffiliatesNetworksAll() {
        return this.affiliatesNetworksRepository.findAll();
    }

    @Override
    public AffiliatesNetworks getAffiliatesNetworksById(Long id) {
        AffiliatesNetworks affiliatesNetworks = this.affiliatesNetworksRepository.findById(id).orElse(null);
        return affiliatesNetworks;
    }

    @Override
    public AffiliatesNetworks saveAffiliatesNetworks(AffiliatesNetworks affiliatesNetworks) {
        return this.affiliatesNetworksRepository.save(affiliatesNetworks);
    }

    @Override
    public void deleteAffiliatesNetworksById(Long id) {
        this.affiliatesNetworksRepository.deleteById(id);
    }
    
}
