/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.affiliates;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Null;
import java.util.List;
import pe.tarjetaestilos.dto.affiliates.AffiliatesNetworkDto;
/**
 *
 * @author user
 */
public class AffiliatesCreateDto {
    @NotBlank(message = "Campo obligatorio")
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String title;
    
    @NotBlank(message = "Campo obligatorio")    
    private String image;
    
    @Null
    private List<AffiliatesNetworkDto> networks; 

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<AffiliatesNetworkDto> getNetworks() {
        return networks;
    }

    public void setNetworks(List<AffiliatesNetworkDto> networks) {
        this.networks = networks;
    }
    
    
}
