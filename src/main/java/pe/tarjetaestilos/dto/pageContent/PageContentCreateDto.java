/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.pageContent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import java.util.List;
import pe.tarjetaestilos.dto.itemContent.ItemContentCreateDto;
/**
 *
 * @author user
 */
public class PageContentCreateDto {
    @NotBlank(message = "Campo obligatorio")
    private String title;
    
    private String description;
    
    @NotBlank(message = "Campo obligatorio")    
    private String image;
    
    @NotBlank(message = "Campo obligatorio")    
    private String image_position;
    
    @NotBlank(message = "Campo obligatorio")    
    private Long menu_id;
    
    @Null
    private List<ItemContentCreateDto> itemContents; 

    public List<ItemContentCreateDto> getItemContents() {
        return itemContents;
    }

    public void setItemContents(List<ItemContentCreateDto> itemContents) {
        this.itemContents = itemContents;
    }

    

    
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_position() {
        return image_position;
    }

    public void setImage_position(String image_position) {
        this.image_position = image_position;
    }

    public Long getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(Long menu_id) {
        this.menu_id = menu_id;
    }
    
    
    
}
