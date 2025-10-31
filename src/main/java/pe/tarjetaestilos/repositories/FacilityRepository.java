/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.tarjetaestilos.models.Facility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/**
 *
 * @author user
 */

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long>{
    Page<Facility> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    @Query("""
        SELECT b 
        FROM Facility b 
        WHERE b.menu.slug = :slug
    """)
    List<Facility> findByMenuSlug(@Param("slug") String slug);
}
