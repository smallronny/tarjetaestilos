/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.section;

/**
 *
 * @author julito
 */
public class SectionSearchDto {
    private String title;
    private Integer section_type_id;
    private Integer page;
    private Integer size;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSection_type_id() {
        return section_type_id;
    }

    public void setSection_type_id(Integer section_type_id) {
        this.section_type_id = section_type_id;
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
