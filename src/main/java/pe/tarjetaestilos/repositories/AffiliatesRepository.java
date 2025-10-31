/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.tarjetaestilos.models.Affiliates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/**
 *
 * @author julito
 */

@Repository
public interface AffiliatesRepository extends JpaRepository<Affiliates, Long>, JpaSpecificationExecutor<Affiliates>{
     
}
