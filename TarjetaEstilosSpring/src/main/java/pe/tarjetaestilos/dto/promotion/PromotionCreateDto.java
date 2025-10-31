/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.dto.promotion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Null;
import java.util.List;

/**
 *
 * @author user
 */
public class PromotionCreateDto {
    @NotBlank(message = "Campo obligatorio")
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String title;
    
    @NotBlank(message = "Campo obligatorio")    
    private String description;
    
    @NotBlank(message = "Campo obligatorio")    
    private String image;
    
    @NotBlank(message = "Campo obligatorio")    
    private String logo;
    
    @NotBlank(message = "Campo obligatorio")    
    private String promotion_link;
    
    @NotBlank(message = "Campo obligatorio")    
    private String promotion_map;
    
    @Null
    private Boolean outstanding;
    
    @Null
    private Boolean featured;
    
    @Null
    private Boolean isnew;
    
    @Null
    private Boolean exclusive;
    
    @NotBlank(message = "Campo obligatorio")    
    private String terms_conditions;
    
    @Null
    private String discount;
    
    @Null
    private List<Long> categoryIds; 
    
    @NotBlank(message = "Campo obligatorio")    
    private Long department_id;
    
    @NotBlank(message = "Campo obligatorio")    
    private Long menu_id;    
    
    @NotBlank(message = "Campo obligatorio")    
    private Long affiliates_id;
    
    @Null
    private Boolean home;

    public Boolean getExclusive() {
        return exclusive;
    }

    public void setExclusive(Boolean exclusive) {
        this.exclusive = exclusive;
    }
    
    
    

    public Boolean getHome() {
        return home;
    }

    public void setHome(Boolean home) {
        this.home = home;
    }
    

    public Long getAffiliates_id() {
        return affiliates_id;
    }

    public void setAffiliates_id(Long affiliates_id) {
        this.affiliates_id = affiliates_id;
    }

    
    public Long getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(Long menu_id) {
        this.menu_id = menu_id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getPromotion_link() {
        return promotion_link;
    }

    public void setPromotion_link(String promotion_link) {
        this.promotion_link = promotion_link;
    }

    public String getPromotion_map() {
        return promotion_map;
    }

    public void setPromotion_map(String promotion_map) {
        this.promotion_map = promotion_map;
    }

    public Boolean getOutstanding() {
        return outstanding;
    }

    public void setOutstanding(Boolean outstanding) {
        this.outstanding = outstanding;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Boolean getIsnew() {
        return isnew;
    }

    public void setIsnew(Boolean isnew) {
        this.isnew = isnew;
    }

    public String getTerms_conditions() {
        return terms_conditions;
    }

    public void setTerms_conditions(String terms_conditions) {
        this.terms_conditions = terms_conditions;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Long getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Long department_id) {
        this.department_id = department_id;
    }
    
    
    
    
}
