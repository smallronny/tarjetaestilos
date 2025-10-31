/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto;

/**
 *
 * @author user
 */
public class ImageResponseDto<T> {
    private String message;
    private T location;
    private Integer status;
    private Integer width;
    private Integer height;
    
    public ImageResponseDto(String message, T location, int status, int width, int height) {
        this.message = message;
        this.location = location;
        this.status = status;
        this.width = width;
        this.height = height;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getLocation() {
        return location;
    }

    public void setLocation(T location) {
        this.location = location;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
    
    
}
