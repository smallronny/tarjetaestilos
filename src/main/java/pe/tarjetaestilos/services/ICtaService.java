/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.Cta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author Julito
 */
public interface ICtaService {
    Page<Cta> getCta(Pageable pageable);
    
    List<Cta> getCtaAll();
    
    Cta getCtaById(Long id);
    
    Cta saveCta(Cta cta);
    
    void deleteCtaById(Long id);
}
