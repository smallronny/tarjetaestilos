/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.MenuType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author user
 */
public interface IMenuTypesService {
    Page<MenuType> getMenuType(Pageable pageable);
    
    List<MenuType> getMenuTypeAll();
    
    MenuType getMenuTypeById(Long id);
    
    MenuType saveMenuType(MenuType menuType);
    
    void deleteMenuTypeById(Long id);
}
