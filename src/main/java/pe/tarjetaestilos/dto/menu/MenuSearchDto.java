/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.menu;
import jakarta.validation.constraints.Null;
import java.util.List;
/**
 *
 * @author julito
 */
public class MenuSearchDto {
    private String title;
    private String slug;
    private Integer page;
    private Integer size;
    
    @Null
    private List<Long> sections; 

    public List<Long> getSections() {
        return sections;
    }

    public void setSections(List<Long> sections) {
        this.sections = sections;
    }
    
    

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
    
    
    
}
