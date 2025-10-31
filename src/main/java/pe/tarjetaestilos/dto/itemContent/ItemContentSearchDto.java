/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.itemContent;

/**
 *
 * @author user
 */
public class ItemContentSearchDto {
    private String description;
    private Long page_content_id;
    private Integer page;
    private Integer size;

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
