/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.facility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Null;
import java.util.List;
/**
 *
 * @author user
 */
public class FacilityCreateDto {
    
    @NotBlank(message = "Campo obligatorio")
    private String label;    
    
    @NotBlank(message = "Campo obligatorio")
    private String title;
    
    @NotBlank(message = "Campo obligatorio")
    private String button_label;
    
    @NotBlank(message = "Campo obligatorio")
    private String image;
    
    @NotBlank(message = "Campo obligatorio")
    private Long menu_id;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getButton_label() {
        return button_label;
    }

    public void setButton_label(String button_label) {
        this.button_label = button_label;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(Long menu_id) {
        this.menu_id = menu_id;
    }
    
    
    
}
