/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;

/**
 *
 * @author julito
 */

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.Category;
import pe.tarjetaestilos.repositories.CategoryRepository;

@Service
public class CategoryService implements ICategoryService{
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Override
    public Page<Category> getCategory(Pageable pageable) {
        return this.categoryRepository.findAll(pageable);
    }
    
    public Page<Category> search(String name, Pageable pageable) {
        if (name != null && !name.isEmpty()) {
            return categoryRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            return categoryRepository.findAll(pageable);
        }
    }
    
    @Override
    public List<Category> getCategoryAll() {
        return this.categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        Category category = this.categoryRepository.findById(id).orElse(null);
        return category;
    }

    @Override
    public Category saveCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    @Override
    public void deleteCategoryById(Long id) {
        this.categoryRepository.deleteById(id);
    }
}
