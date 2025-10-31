/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.MenuType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.tarjetaestilos.repositories.MenuTypesRepository;
/**
 *
 * @author julito
 */
@Service
public class MenuTypesService implements IMenuTypesService{
    @Autowired
    private MenuTypesRepository menuTypesRepository;
    
    @Override
    public Page<MenuType> getMenuType(Pageable pageable) {
        return this.menuTypesRepository.findAll(pageable);
    }
    
    @Override
    public List<MenuType> getMenuTypeAll() {
        return this.menuTypesRepository.findAll();
    }

    @Override
    public MenuType getMenuTypeById(Long id) {
        MenuType menuType = this.menuTypesRepository.findById(id).orElse(null);
        return menuType;
    }

    @Override
    public MenuType saveMenuType(MenuType menuType) {
        return this.menuTypesRepository.save(menuType);
    }

    @Override
    public void deleteMenuTypeById(Long id) {
        this.menuTypesRepository.deleteById(id);
    }
    
}
