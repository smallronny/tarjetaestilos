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
import pe.tarjetaestilos.models.PromotionCategory;
import pe.tarjetaestilos.repositories.PromotionCategoryRepository;
/**
 *
 * @author user
 */

@Service
public class PromotionCategoryService implements IPromotionCategoryService{
    
    @Autowired
    private PromotionCategoryRepository promotionCategoryRepository;
    
    @Override
    public Page<PromotionCategory> getPromotionCategory(Pageable pageable) {
        return this.promotionCategoryRepository.findAll(pageable);
    }
    
    public Page<PromotionCategory> search(String name, Pageable pageable) {
        if (name != null && !name.isEmpty()) {
            return promotionCategoryRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            return promotionCategoryRepository.findAll(pageable);
        }
    }
    
    @Override
    public List<PromotionCategory> getPromotionCategoryAll() {
        return this.promotionCategoryRepository.findAll();
    }

    @Override
    public PromotionCategory getPromotionCategoryById(Long id) {
        PromotionCategory promotionCategory = this.promotionCategoryRepository.findById(id).orElse(null);
        return promotionCategory;
    }

    @Override
    public PromotionCategory savePromotionCategory(PromotionCategory promotionCategory) {
        return this.promotionCategoryRepository.save(promotionCategory);
    }

    @Override
    public void deletePromotionCategoryById(Long id) {
        this.promotionCategoryRepository.deleteById(id);
    }
}
