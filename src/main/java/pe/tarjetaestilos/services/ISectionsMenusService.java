/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.SectionsMenus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */
public interface ISectionsMenusService {
    Page<SectionsMenus> getSectionsMenus(Pageable pageable);
    
    List<SectionsMenus> getSectionsMenusAll();
    
    SectionsMenus getSectionsMenusById(Long id);
    
    SectionsMenus saveSectionsMenus(SectionsMenus sectionsMenus);
    
    void deleteSectionsMenusById(Long id);
}
