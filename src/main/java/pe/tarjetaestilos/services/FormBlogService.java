/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.FormBlog;
import pe.tarjetaestilos.repositories.FormBlogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
/**
 *
 * @author julito
 */
@Service
public class FormBlogService implements IFormBlogService {
    @Autowired
    private FormBlogRepository formBlogRepository;
    
    @Override
    public Page<FormBlog> getFormBlog(Pageable pageable) {
        return this.formBlogRepository.findAll(pageable);
    }
    
    public Page<FormBlog> search(
        String dni,
        String email,    
        Long menuId,
        Pageable pageable
    ) {
        return formBlogRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dni != null && !dni.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("dni")), "%" + dni.toLowerCase() + "%"));
            }
            
            if (email != null && !email.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            if (menuId != null) {
                predicates.add(cb.equal(root.get("menu").get("id"), menuId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public List<FormBlog> getFormBlogAll() {
        return this.formBlogRepository.findAll();
    }

    @Override
    public FormBlog getFormBlogById(Long id) {
        FormBlog formBlog = this.formBlogRepository.findById(id).orElse(null);
        return formBlog;
    }

    @Override
    public FormBlog saveFormBlog(FormBlog formBlog) {
        return this.formBlogRepository.save(formBlog);
    }

    @Override
    public void deleteFormBlogById(Long id) {
        this.formBlogRepository.deleteById(id);
    }
}
