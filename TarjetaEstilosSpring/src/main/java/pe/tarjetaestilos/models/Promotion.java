/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.models;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Where;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author julito
 */

@Entity
@Table(name="promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String image;
    
    private String logo;
    
    @Column(name = "promotion_link")
    private String promotion_link;
    
    @Column(name = "promotion_map")
    private String promotion_map;
    
    @Column(name = "outstanding", nullable = true)
    private Boolean outstanding;
    
    @Column(name = "featured", nullable = true)
    private Boolean featured;
    
    @Column(name = "isnew", nullable = true)
    private Boolean isnew;
    
    private String terms_conditions;
    
    private String discount;
        
    @Column(name = "home", nullable = true)
    private Boolean home;
    
    private Boolean exclusive;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
    
    @ManyToOne()
    @JoinColumn(name = "menu_id", nullable = false)
    @ToString.Exclude
    @JsonBackReference
    private Menu menu;
    
    @ManyToOne()
    @JoinColumn(name = "department_id", nullable = false)
    @ToString.Exclude
    private Department department;
    
    @ManyToOne()
    @JoinColumn(name = "affiliates_id", nullable = false)
    @ToString.Exclude
    private Affiliates affiliates;
    
    @OneToMany(mappedBy = "promotion")
    @JsonManagedReference
    @ToString.Exclude
    private List<PromotionsCategories> promotionsCategories = new ArrayList<>();
    
}
