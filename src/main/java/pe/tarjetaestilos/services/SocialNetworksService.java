/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.SocialNetworks;
import pe.tarjetaestilos.repositories.SocialNetworksRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
/**
 *
 * @author julito
 */
@Service
public class SocialNetworksService implements ISocialNetworksService{
    
    @Autowired
    private SocialNetworksRepository socialNetworksRepository;
    
    public Page<SocialNetworks> search(
        String title,
        Pageable pageable
    ) {
        return socialNetworksRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }           

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public Page<SocialNetworks> getSocialNetworks(Pageable pageable) {
        return this.socialNetworksRepository.findAll(pageable);
    }
    
    @Override
    public List<SocialNetworks> getSocialNetworksAll() {
        return this.socialNetworksRepository.findAll();
    }
    
    @Override
    public SocialNetworks getSocialNetworksById(Long id) {
        SocialNetworks socialNetworks = this.socialNetworksRepository.findById(id).orElse(null);
        return socialNetworks;
    }
    
    

    @Override
    public SocialNetworks saveSocialNetworks(SocialNetworks socialNetworks) {
        return this.socialNetworksRepository.save(socialNetworks);
    }

    @Override
    public void deleteSocialNetworksById(Long id) {
        this.socialNetworksRepository.deleteById(id);
    }
    
}
