/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.itemContent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Null;
import java.util.List;
/**
 *
 * @author julito
 */
public class ItemContentCreateDto {
    @NotBlank(message = "Campo obligatorio")
    private String icon;
    
    @NotBlank(message = "Campo obligatorio")
    private String description;
    
    @NotBlank(message = "Campo obligatorio")    
    private Long page_content_id;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPage_content_id() {
        return page_content_id;
    }

    public void setPage_content_id(Long page_content_id) {
        this.page_content_id = page_content_id;
    }
    
    
    
}
