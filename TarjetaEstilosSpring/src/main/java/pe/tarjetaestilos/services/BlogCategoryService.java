/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.BlogCategory;
import pe.tarjetaestilos.repositories.BlogCategoryRepository;

/**
 *
 * @author julito
 */

@Service
public class BlogCategoryService implements IBlogCategoryService{
    
    @Autowired
    private BlogCategoryRepository blogCategoryRepository;
    
    @Override
    public List<BlogCategory> getBlogCategory() {
        return this.blogCategoryRepository.findAll();
    }

    @Override
    public BlogCategory getBlogCategoryById(Long id) {
        BlogCategory blogCategory = this.blogCategoryRepository.findById(id).orElse(null);
        return blogCategory;
    }

    @Override
    public void saveBlogCategory(BlogCategory blogCategory) {
        this.blogCategoryRepository.save(blogCategory);
    }

    @Override
    public void deleteBlogCategoryById(Long id) {
        this.blogCategoryRepository.deleteById(id);
    }
    
}
