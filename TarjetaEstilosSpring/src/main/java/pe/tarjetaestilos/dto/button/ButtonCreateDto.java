/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.button;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Null;
import java.util.List;
/**
 *
 * @author julito
 */
public class ButtonCreateDto {
    
    @NotBlank(message = "Campo obligatorio")
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String title;
    
    @NotBlank(message = "Campo obligatorio")
    private String add_class;
    
    @NotBlank(message = "Campo obligatorio")
    private String link;
    
    @NotBlank(message = "Campo obligatorio")    
    private Long banners_id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdd_class() {
        return add_class;
    }

    public void setAdd_class(String add_class) {
        this.add_class = add_class;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Long getBanners_id() {
        return banners_id;
    }

    public void setBanners_id(Long banners_id) {
        this.banners_id = banners_id;
    }
    
    
    
    
}
