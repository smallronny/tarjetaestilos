/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import pe.tarjetaestilos.models.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author julito
 */
public interface IBlogService {
    Page<Blog> getBlog(Pageable pageable); 
    
    Blog getBlogById(Long id);
    
    Blog saveBlog(Blog blog);
    
    void deleteBlogById(Long id);
}
