/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.SectionsMenus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.tarjetaestilos.repositories.SectionsMenusRepository;
/**
 *
 * @author julito
 */
@Service
public class SectionsMenusService implements ISectionsMenusService{
    
    @Autowired
    private SectionsMenusRepository secionsMenusRepository;
    
    @Override
    public Page<SectionsMenus> getSectionsMenus(Pageable pageable) {
        return this.secionsMenusRepository.findAll(pageable);
    }
    
    @Override
    public List<SectionsMenus> getSectionsMenusAll() {
        return this.secionsMenusRepository.findAll();
    }

    @Override
    public SectionsMenus getSectionsMenusById(Long id) {
        SectionsMenus sectionsMenus = this.secionsMenusRepository.findById(id).orElse(null);
        return sectionsMenus;
    }

    @Override
    public SectionsMenus saveSectionsMenus(SectionsMenus sectionsMenus) {
        return this.secionsMenusRepository.save(sectionsMenus);
    }

    @Override
    public void deleteSectionsMenusById(Long id) {
        this.secionsMenusRepository.deleteById(id);
    }
    
}
