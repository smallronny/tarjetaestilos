/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.ItemContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author user
 */
public interface IItemContentService {
    Page<ItemContent> getItemContent(Pageable pageable);
    
    List<ItemContent> getItemContentAll();
    
    ItemContent getItemContentById(Long id);
    
    ItemContent saveItemContent(ItemContent itemContent);
    
    void deleteItemContentById(Long id);
}
