/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.tarjetaestilos.models.PromotionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author user
 */
@Repository
public interface PromotionCategoryRepository extends JpaRepository<PromotionCategory, Long>{
    Page<PromotionCategory> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
}
