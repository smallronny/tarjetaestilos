/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.blog;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Null;
import java.util.List;

/**
 *
 * @author user
 */
public class BlogCreateDto {
    @NotBlank(message = "Campo obligatorio")
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String title;
    
    @NotBlank(message = "Campo obligatorio")    
    private String description;
    
    @NotBlank(message = "Campo obligatorio")    
    private String summary;
    
    @Null
    private String readingTime;
    
    @NotBlank(message = "Campo obligatorio")
    private String publication_date;
    
    @NotBlank(message = "Campo obligatorio")    
    private String card_image;
    
    @NotBlank(message = "Campo obligatorio")    
    private String main_image;
    
    @NotBlank(message = "Campo obligatorio")    
    private Long menu_id;

    public Long getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(Long menu_id) {
        this.menu_id = menu_id;
    }
    
    
    
    @Null
    private List<Long> category_id; 

    public String getCard_image() {
        return card_image;
    }

    public void setCard_image(String card_image) {
        this.card_image = card_image;
    }

    public String getMain_image() {
        return main_image;
    }

    public void setMain_image(String main_image) {
        this.main_image = main_image;
    }
    
    
    

    public String getPublication_date() {
        return publication_date;
    }

    public void setPublication_date(String publication_date) {
        this.publication_date = publication_date;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(String readingTime) {
        this.readingTime = readingTime;
    }

    public List<Long> getCategory_id() {
        return category_id;
    }

    public void setCategory_id(List<Long> category_id) {
        this.category_id = category_id;
    }

    

    

    
    
    
}
