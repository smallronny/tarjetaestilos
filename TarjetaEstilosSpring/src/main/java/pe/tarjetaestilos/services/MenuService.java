/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.Menu;
import pe.tarjetaestilos.repositories.MenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.Optional;

/**
 *
 * @author julito
 */
@Service
public class MenuService implements IMenuService{
    
    @Autowired
    private MenuRepository menuRepository;
    
    @Override
    public Page<Menu> getMenu(Pageable pageable) {
        return this.menuRepository.findAll(pageable);
    }
    
    public Optional<Menu> getMenuBySlug(String slug) {
        return menuRepository.findBySlugWithRelations(slug);
    }
    
    public Page<Menu> search(
        String title,
        String slug,
        List<Long> sections,
        Pageable pageable
    ) {
        return menuRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (slug != null && !slug.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("slug")), "%" + slug.toLowerCase() + "%"));
            }
            
            if (sections != null && !sections.isEmpty()) {
                // Hacemos JOIN con sectionsMenus y luego con section
                Join<Object, Object> joinSectionsMenus = root.join("sectionsMenus", JoinType.LEFT);
                Join<Object, Object> joinSection = joinSectionsMenus.join("section", JoinType.LEFT);

                // Creamos el predicado: section.id IN (:sections)
                predicates.add(joinSection.get("id").in(sections));

                // Evita duplicados si un menú pertenece a varias secciones
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public List<Menu> getMenuAll() {
        return this.menuRepository.findAll();
    }

    @Override
    public Menu getMenuById(Long id) {
        Menu menu = this.menuRepository.findById(id).orElse(null);
        return menu;
    }

    @Override
    public Menu saveMenu(Menu menu) {
        return this.menuRepository.save(menu);
    }

    @Override
    public void deleteMenuById(Long id) {
        this.menuRepository.deleteById(id);
    }
    
}
