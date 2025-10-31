/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.tarjetaestilos.models.Shortcut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/**
 *
 * @author julito
 */

@Repository
public interface ShortcutRepository extends JpaRepository<Shortcut, Long> {
    Page<Shortcut> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    @Query("""
        SELECT b 
        FROM Shortcut b 
        WHERE b.menu.slug = :slug
    """)
    List<Shortcut> findByMenuSlug(@Param("slug") String slug);
    
}