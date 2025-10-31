/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.tarjetaestilos.models.SocialNetworks;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/**
 *
 * @author user
 */
@Repository
public interface SocialNetworksRepository extends JpaRepository<SocialNetworks, Long>, JpaSpecificationExecutor<SocialNetworks>{
    
}
