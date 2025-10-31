/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.PromotionsCategories;
import pe.tarjetaestilos.repositories.PromotionsCategoriesRepository;

/**
 *
 * @author julito
 */

@Service
public class PromotionsCategoriesService implements IPromotionsCategoriesService{
    
    @Autowired
    private PromotionsCategoriesRepository promotionsCategoriesRepository;
    
    @Override
    public List<PromotionsCategories> getPromotionsCategories() {
        return this.promotionsCategoriesRepository.findAll();
    }

    @Override
    public PromotionsCategories getPromotionsCategoriesById(Long id) {
        PromotionsCategories promotionsCategories = this.promotionsCategoriesRepository.findById(id).orElse(null);
        return promotionsCategories;
    }

    @Override
    public void savePromotionsCategories(PromotionsCategories promotionsCategories) {
        this.promotionsCategoriesRepository.save(promotionsCategories);
    }

    @Override
    public void deletePromotionsCategoriesById(Long id) {
        this.promotionsCategoriesRepository.deleteById(id);
    }
}
