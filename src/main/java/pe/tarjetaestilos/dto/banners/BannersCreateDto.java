/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.banners;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Null;
import java.util.List;
import pe.tarjetaestilos.dto.button.ButtonCreateDto;
/**
 *
 * @author julito
 */
public class BannersCreateDto {
    @NotBlank(message = "Campo obligatorio")
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String title;
    
    
    @NotBlank(message = "Campo obligatorio")
    @Size(max = 250, message = "Máximo 150 caracteres")
    private String subtitle;
    
    @NotBlank(message = "Campo obligatorio")    
    private String image;
    
    @Null
    private String label;
    
    @NotBlank(message = "Campo obligatorio")    
    private Long menu_id;
    
    @Null
    private List<ButtonCreateDto> buttons;
    
    @Null
    private Boolean blog_form;
    

    public Boolean getBlog_form() {
        return blog_form;
    }

    public void setBlog_form(Boolean blog_form) {
        this.blog_form = blog_form;
    }

    
    public List<ButtonCreateDto> getButtons() {
        return buttons;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    
    
    public void setButtons(List<ButtonCreateDto> buttons) {
        this.buttons = buttons;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
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
