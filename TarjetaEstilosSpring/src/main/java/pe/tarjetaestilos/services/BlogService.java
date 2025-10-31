/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.Blog;
import pe.tarjetaestilos.repositories.BlogRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.ZoneId;
import java.time.ZonedDateTime;


import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import pe.tarjetaestilos.models.BlogCategory;
import pe.tarjetaestilos.models.Category;

/**
 *
 * @author julito
 */

@Service
public class BlogService implements IBlogService{

    @Autowired
    private BlogRepository blogRepository;
    
    @Override
    public Page<Blog> getBlog(Pageable pageable) {
        return this.blogRepository.findAll(pageable);
    }
    
    public Page<Blog> search(
        String title,
        String description,
        String summary,
        String startDate,
        String endDate,
        List<Long> categoryIds,
        Pageable pageable
    ) {
        return blogRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (description != null && !description.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            if (summary != null) {
                predicates.add(cb.equal(root.get("summary"), summary));
            }

             // Parseo de fechas
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            ZonedDateTime start = null;
            ZonedDateTime end = null;

            try {
                if (startDate != null && !startDate.isEmpty()) {
                    start = LocalDate.parse(startDate, formatter)
                                     .atStartOfDay(ZoneId.systemDefault()); // ⏰ inicio del día
                }
                if (endDate != null && !endDate.isEmpty()) {
                    end = LocalDate.parse(endDate, formatter)
                                   .atTime(23, 59, 59)
                                   .atZone(ZoneId.systemDefault()); // ⏰ fin del día
                }
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Formato de fecha inválido. Usa yyyy-MM-dd");
            }

            // Filtro de rango de fechas
            if (start != null && end != null) {
                predicates.add(cb.between(root.get("publicationDate"), start, end));
            } else if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("publicationDate"), start));
            } else if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("publicationDate"), end));
            }

            // Filtro por categorías
            if (categoryIds != null && !categoryIds.isEmpty()) {
                // Hacemos join desde Blog -> BlogCategory -> Category
                Join<Blog, BlogCategory> blogCategoryJoin = root.join("blogCategories");
                Join<BlogCategory, Category> categoryJoin = blogCategoryJoin.join("category");

                // Creamos el in() para filtrar por category_id
                CriteriaBuilder.In<Long> inClause = cb.in(categoryJoin.get("id"));
                for (Long id : categoryIds) {
                    inClause.value(id);
                }

                predicates.add(inClause);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    public Blog getBlogById(Long id) {
        Blog blog = this.blogRepository.findById(id).orElse(null);
        return blog;
    }
    
    public Blog getNewBlogById(Long id) {
        return blogRepository.findByIdWithCategories(id)
                .orElseThrow(() -> new RuntimeException("Blog no encontrado con id " + id));
    }

    @Override
    public Blog saveBlog(Blog blog) {
        return this.blogRepository.save(blog);
    }

    @Override
    public void deleteBlogById(Long id) {
        this.blogRepository.deleteById(id);
    }
    
    
    public List<Blog> getBlogsByMenuSlug(String slug) {
        return blogRepository.findByMenuSlug(slug);
    }
    
}
