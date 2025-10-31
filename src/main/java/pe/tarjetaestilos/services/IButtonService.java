/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.Button;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */
public interface IButtonService {
    Page<Button> getButton(Pageable pageable);
    
    List<Button> getButtonAll();
    
    Button getButtonById(Long id);
    
    Button saveButton(Button button);
    
    void deleteButtonById(Long id);
}
