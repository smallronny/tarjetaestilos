/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.FormBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author Julito
 */
public interface IFormBlogService {
    Page<FormBlog> getFormBlog(Pageable pageable);
    
    List<FormBlog> getFormBlogAll();
    
    FormBlog getFormBlogById(Long id);
    
    FormBlog saveFormBlog(FormBlog formBlog);
    
    void deleteFormBlogById(Long id);
}
