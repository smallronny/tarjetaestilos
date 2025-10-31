/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import pe.tarjetaestilos.models.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */
public interface IPromotionService {
    Page<Promotion> getPromotion(Pageable pageable);
    
    List<Promotion> getPromotionAll();
    
    Promotion getPromotionById(Long id);
    
    Promotion savePromotion(Promotion promotion);
    
    void deletePromotionById(Long id);
}
