/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;

/**
 *
 * @author user
 */
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.Shortcut;
import pe.tarjetaestilos.repositories.ShortcutRepository;

@Service
public class ShortcutService implements IShortcutService{
    @Autowired
    private ShortcutRepository shortcutRepository;
    
    @Override
    public Page<Shortcut> getShortcut(Pageable pageable) {
        return this.shortcutRepository.findAll(pageable);
    }
    
    public List<Shortcut> getShortcutByMenuSlug(String slug) {
        return shortcutRepository.findByMenuSlug(slug);
    }
    
    public Page<Shortcut> search(String title, Pageable pageable) {
        if (title != null && !title.isEmpty()) {
            return shortcutRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else {
            return shortcutRepository.findAll(pageable);
        }
    }
    
    @Override
    public List<Shortcut> getShortcutAll() {
        return this.shortcutRepository.findAll();
    }

    @Override
    public Shortcut getShortcutById(Long id) {
        Shortcut shortcut = this.shortcutRepository.findById(id).orElse(null);
        return shortcut;
    }

    @Override
    public Shortcut saveShortcut(Shortcut shortcut) {
        return this.shortcutRepository.save(shortcut);
    }

    @Override
    public void deleteShortcutById(Long id) {
        this.shortcutRepository.deleteById(id);
    }
}
