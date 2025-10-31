/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.tarjetaestilos.models.FormBlog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/**
 *
 * @author julito
 */

@Repository
public interface FormBlogRepository extends JpaRepository<FormBlog, Long>, JpaSpecificationExecutor<FormBlog>{
    
}
