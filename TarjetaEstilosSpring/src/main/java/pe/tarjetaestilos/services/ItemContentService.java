/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.ItemContent;
import pe.tarjetaestilos.repositories.ItemContentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
/**
 *
 * @author julito
 */
@Service
public class ItemContentService implements IItemContentService{
    @Autowired
    private ItemContentRepository itemContentRepository;
    
    @Override
    public Page<ItemContent> getItemContent(Pageable pageable) {
        return this.itemContentRepository.findAll(pageable);
    }
    
    public Page<ItemContent> search(
        String description,
        Long pageContentId,
        Pageable pageable
    ) {
        return itemContentRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (description != null && !description.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }
            
            if (pageContentId != null) {
                predicates.add(cb.equal(root.get("pageContent").get("id"), pageContentId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public List<ItemContent> getItemContentAll() {
        return this.itemContentRepository.findAll();
    }

    @Override
    public ItemContent getItemContentById(Long id) {
        ItemContent itemContent = this.itemContentRepository.findById(id).orElse(null);
        return itemContent;
    }

    @Override
    public ItemContent saveItemContent(ItemContent itemContent) {
        return this.itemContentRepository.save(itemContent);
    }

    @Override
    public void deleteItemContentById(Long id) {
        this.itemContentRepository.deleteById(id);
    }
}
