/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */
public interface IMenuService {
    Page<Menu> getMenu(Pageable pageable);
    
    List<Menu> getMenuAll();
    
    Menu getMenuById(Long id);
    
    Menu saveMenu(Menu menu);
    
    void deleteMenuById(Long id);
}
