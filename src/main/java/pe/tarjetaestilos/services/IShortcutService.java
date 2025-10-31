/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import pe.tarjetaestilos.models.Shortcut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author user
 */
public interface IShortcutService {
    Page<Shortcut> getShortcut(Pageable pageable);
    
    List<Shortcut> getShortcutAll();
    
    Shortcut getShortcutById(Long id);
    
    Shortcut saveShortcut(Shortcut blog);
    
    void deleteShortcutById(Long id);
}
