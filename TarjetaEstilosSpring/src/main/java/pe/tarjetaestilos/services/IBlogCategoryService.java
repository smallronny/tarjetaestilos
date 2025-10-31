/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import pe.tarjetaestilos.models.BlogCategory;
/**
 *
 * @author julito
 */
public interface IBlogCategoryService {
    List<BlogCategory> getBlogCategory();
    
    BlogCategory getBlogCategoryById(Long id);
    
    void saveBlogCategory(BlogCategory blog);
    
    void deleteBlogCategoryById(Long id);
}
