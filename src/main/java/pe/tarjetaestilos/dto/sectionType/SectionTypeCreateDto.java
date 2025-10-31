/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.sectionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Null;
import java.util.List;

/**
 *
 * @author user
 */
public class SectionTypeCreateDto {
    @NotBlank(message = "Campo obligatorio")
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String title;
    
    @NotBlank(message = "Campo obligatorio")    
    private String logo;
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    
    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    
}
