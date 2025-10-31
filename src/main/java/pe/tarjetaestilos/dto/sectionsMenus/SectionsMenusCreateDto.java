/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.sectionsMenus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Null;
import java.util.List;
/**
 *
 * @author user
 */
public class SectionsMenusCreateDto {
    
    @NotBlank(message = "Campo obligatorio")    
    private Long section_id;
    
    @NotBlank(message = "Campo obligatorio")    
    private Long menu_id;

    public Long getSection_id() {
        return section_id;
    }

    public void setSection_id(Long section_id) {
        this.section_id = section_id;
    }

    public Long getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(Long menu_id) {
        this.menu_id = menu_id;
    }
    
    
    
}
