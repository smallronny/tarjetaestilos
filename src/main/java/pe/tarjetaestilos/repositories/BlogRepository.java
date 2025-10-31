/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.tarjetaestilos.models.Blog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/**
 *
 * @author julito
 */
@Repository
public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {
    
    @Query("SELECT b FROM Blog b LEFT JOIN FETCH b.blogCategories bc LEFT JOIN FETCH bc.category WHERE b.id = :id")
    Optional<Blog> findByIdWithCategories(@Param("id") Long id);
    
    
    @Query("""
        SELECT b 
        FROM Blog b 
        WHERE b.menu.slug = :slug
    """)
    List<Blog> findByMenuSlug(@Param("slug") String slug);
}
