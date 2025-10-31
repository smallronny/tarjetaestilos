/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.formBlog;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
/**
 *
 * @author julito
 */
public class FormBlogCreateDto {
    @NotBlank(message = "Campo obligatorio")
    private String dni;
    
    
    @NotBlank(message = "Campo obligatorio")
    @Email
    private String email;
    
    
    @NotBlank(message = "Campo obligatorio")
    private String phone;
    
    
    @NotBlank(message = "Campo obligatorio")
    private Boolean accept;
    
    
    @NotBlank(message = "Campo obligatorio")    
    private Long menu_id;

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getAccept() {
        return accept;
    }

    public void setAccept(Boolean accept) {
        this.accept = accept;
    }

    public Long getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(Long menu_id) {
        this.menu_id = menu_id;
    }
    
    
}
