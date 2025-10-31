/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * @author user
 */
public class DepartmentCreateDto {
    
    @NotBlank(message = "Campo obligatorio")
    @Size(max = 100, message = "Máximo 150 caracteres")
    private String name;
    private String phone;
    private String label_phone;

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLabel_phone() {
        return label_phone;
    }

    public void setLabel_phone(String label_phone) {
        this.label_phone = label_phone;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}
