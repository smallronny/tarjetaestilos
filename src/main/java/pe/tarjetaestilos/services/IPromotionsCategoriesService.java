/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import pe.tarjetaestilos.models.PromotionsCategories;
/**
 *
 * @author user
 */
public interface IPromotionsCategoriesService {
    List<PromotionsCategories> getPromotionsCategories();
    
    PromotionsCategories getPromotionsCategoriesById(Long id);
    
    void savePromotionsCategories(PromotionsCategories blog);
    
    void deletePromotionsCategoriesById(Long id);
}
