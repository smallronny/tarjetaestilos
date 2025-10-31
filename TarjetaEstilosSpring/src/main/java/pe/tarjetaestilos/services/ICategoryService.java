/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import pe.tarjetaestilos.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author user
 */
public interface ICategoryService {
    Page<Category> getCategory(Pageable pageable);
    
    List<Category> getCategoryAll();
    
    Category getCategoryById(Long id);
    
    Category saveCategory(Category blog);
    
    void deleteCategoryById(Long id);
}
