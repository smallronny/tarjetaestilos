/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.tarjetaestilos.models.SectionType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/**
 *
 * @author user
 */
@Repository
public interface SectionTypeRepository extends JpaRepository<SectionType, Long>, JpaSpecificationExecutor<SectionType>{
    
}
