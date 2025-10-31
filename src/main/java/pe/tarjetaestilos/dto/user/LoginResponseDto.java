/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.user;

import pe.tarjetaestilos.models.User;

/**
 *
 * @author user
 */
public class LoginResponseDto {
    private String token;
    private Integer status;
    private User user;
    private String message;

    public LoginResponseDto(String token, User user, Integer status, String message) {
        this.token = token;
        this.user = user;
        this.status = status;
        this.message = message;
    }

    public String getToken() {
        return token;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
}
