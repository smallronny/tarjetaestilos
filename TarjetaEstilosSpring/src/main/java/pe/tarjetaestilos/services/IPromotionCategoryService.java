/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import pe.tarjetaestilos.models.PromotionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */
public interface IPromotionCategoryService {
    Page<PromotionCategory> getPromotionCategory(Pageable pageable);
    
    List<PromotionCategory> getPromotionCategoryAll();
    
    PromotionCategory getPromotionCategoryById(Long id);
    
    PromotionCategory savePromotionCategory(PromotionCategory blog);
    
    void deletePromotionCategoryById(Long id);
}
