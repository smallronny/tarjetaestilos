/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.controller;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.tarjetaestilos.models.BlogCategory;
import pe.tarjetaestilos.services.BlogCategoryService;
/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos") 
public class BlogCategoryController {
    private static final Logger logger = LoggerFactory.getLogger(BlogCategoryController.class);
    
    @Autowired
    private BlogCategoryService blogCategoryService;
    
    @GetMapping("/cms/blog-category") //http://localhost:8081/tarjeta-estilos-app/blog-category
    public List<BlogCategory> getBlogCategories(){
        List<BlogCategory> blogCategories = this.blogCategoryService.getBlogCategory();
        logger.info("registos obtenidos:");
        blogCategories.forEach(blogCategory-> logger.info(blogCategory.toString()));
        return blogCategories;
    }
}
