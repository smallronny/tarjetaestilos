/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.menuType;
import jakarta.validation.constraints.NotBlank;
/**
 *
 * @author user
 */
public class MenuTypeCreateDto {
    @NotBlank(message = "Campo obligatorio")    
    private Long section_type_id;
    
    @NotBlank(message = "Campo obligatorio")    
    private Long menu_id;

    public Long getSection_type_id() {
        return section_type_id;
    }

    public void setSection_type_id(Long section_type_id) {
        this.section_type_id = section_type_id;
    }

    public Long getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(Long menu_id) {
        this.menu_id = menu_id;
    }
    
    
    
}
