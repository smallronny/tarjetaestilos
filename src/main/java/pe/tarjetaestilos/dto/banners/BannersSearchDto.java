/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.banners;

/**
 *
 * @author julito
 */
public class BannersSearchDto {
    private String title;
    private String subtitle;
    private Long menu_id;
    private Integer page;
    private Integer size;
    private Boolean blog_form;

    public Boolean getBlog_form() {
        return blog_form;
    }

    public void setBlog_form(Boolean blog_form) {
        this.blog_form = blog_form;
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

    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Long getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(Long menu_id) {
        this.menu_id = menu_id;
    }
        
    
}
