/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.PageContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */
public interface IPageContentService {
    Page<PageContent> getPageContent(Pageable pageable);
    
    List<PageContent> getPageContentAll();
    
    PageContent getPageContentById(Long id);
    
    PageContent savePageContent(PageContent pageContent);
    
    void deletePageContentById(Long id);
}
