/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.repositories;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.tarjetaestilos.models.PageContent;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/**
 *
 * @author julito
 */
@Repository
public interface PageContentRepository extends JpaRepository<PageContent, Long>, JpaSpecificationExecutor<PageContent>{
    @Query("""
        SELECT b 
        FROM PageContent b 
        WHERE b.menu.slug = :slug
    """)
    List<PageContent> findByMenuSlug(@Param("slug") String slug);
}
