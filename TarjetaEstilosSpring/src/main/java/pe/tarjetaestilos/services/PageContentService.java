/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.PageContent;
import pe.tarjetaestilos.repositories.PageContentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
/**
 * 
 * @author julito
 */
@Service
public class PageContentService implements IPageContentService{
    @Autowired
    private PageContentRepository pageContentRepository;
    
    @Override
    public Page<PageContent> getPageContent(Pageable pageable) {
        return this.pageContentRepository.findAll(pageable);
    }
    
    
    public List<PageContent> getPageContentByMenuSlug(String slug) {
        return pageContentRepository.findByMenuSlug(slug);
    }
    
    public Page<PageContent> search(
        String title,
        String description,
        Long menuId,
        Pageable pageable
    ) {
        return pageContentRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            
            if (description != null && !description.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            if (menuId != null) {
                predicates.add(cb.equal(root.get("menu").get("id"), menuId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public List<PageContent> getPageContentAll() {
        return this.pageContentRepository.findAll();
    }

    @Override
    public PageContent getPageContentById(Long id) {
        PageContent pageContent = this.pageContentRepository.findById(id).orElse(null);
        return pageContent;
    }

    @Override
    public PageContent savePageContent(PageContent pageContent) {
        return this.pageContentRepository.save(pageContent);
    }

    @Override
    public void deletePageContentById(Long id) {
        this.pageContentRepository.deleteById(id);
    }
}
