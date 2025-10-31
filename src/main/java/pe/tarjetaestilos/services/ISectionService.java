/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */
public interface ISectionService {
    Page<Section> getSection(Pageable pageable);
    
    List<Section> getSectionAll();
    
    Section getSectionById(Long id);
    
    Section saveSection(Section section);
    
    void deleteSectionById(Long id);
}
