/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.tarjetaestilos.models.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
/**
 *
 * @author julito
 */

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long>, JpaSpecificationExecutor<Promotion> {
    @Query("SELECT p FROM Promotion p LEFT JOIN FETCH p.promotionsCategories pc LEFT JOIN FETCH pc.promotionCategory WHERE p.id = :id")
    Optional<Promotion> findByIdWithCategories(@Param("id") Long id);
    
    
    @Query("""
        SELECT b 
        FROM Promotion b 
        WHERE b.menu.slug = :slug
    """)
    List<Promotion> findByMenuSlug(@Param("slug") String slug);
}