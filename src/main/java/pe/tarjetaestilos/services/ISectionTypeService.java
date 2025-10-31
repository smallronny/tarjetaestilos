/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.SectionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */
public interface ISectionTypeService {
    Page<SectionType> getSectionType(Pageable pageable);
    
    List<SectionType> getSectionTypeAll();
    
    SectionType getSectionTypeById(Long id);
    
    SectionType saveSectionType(SectionType sectionType);
    
    void deleteSectionTypeById(Long id);
}
