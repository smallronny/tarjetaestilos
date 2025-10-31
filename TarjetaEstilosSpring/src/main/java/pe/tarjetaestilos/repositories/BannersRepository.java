/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.repositories;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.tarjetaestilos.models.Banners;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/**
 *
 * @author julito
 */

@Repository
public interface BannersRepository extends JpaRepository<Banners, Long>, JpaSpecificationExecutor<Banners>{
    @Query("""
        SELECT b 
        FROM Banners b 
        WHERE b.menu.slug = :slug
    """)
    List<Banners> findByMenuSlug(@Param("slug") String slug);
}
