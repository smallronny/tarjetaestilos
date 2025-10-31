/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.Button;
import pe.tarjetaestilos.repositories.ButtonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */
@Service
public class ButtonService implements IButtonService{
    @Autowired
    private ButtonRepository buttonRepository;
    
    @Override
    public Page<Button> getButton(Pageable pageable) {
        return this.buttonRepository.findAll(pageable);
    }
    
    @Override
    public List<Button> getButtonAll() {
        return this.buttonRepository.findAll();
    }

    @Override
    public Button getButtonById(Long id) {
        Button button = this.buttonRepository.findById(id).orElse(null);
        return button;
    }

    @Override
    public Button saveButton(Button button) {
        return this.buttonRepository.save(button);
    }

    @Override
    public void deleteButtonById(Long id) {
        this.buttonRepository.deleteById(id);
    }
}
