/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.models;

/**
 *
 * @author julito
 */
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class Menu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String title; 
    
    @Column(columnDefinition = "TEXT")
    private String slug; 
    
    @Column(columnDefinition = "TEXT")
    private String icon; 
    
    @Column(name = "featured", nullable = true)
    private Boolean page; 
    
    @Column(name = "product", nullable = true)
    private Boolean product; 
    
    @Column(name = "insurance", nullable = true)
    private Boolean insurance; 
    
    @Column(name = "position", nullable = true)
    private Integer position; 
    
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
    
    
    //RELACIONES
    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<SectionsMenus> sectionsMenus = new ArrayList<>();

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<MenuType> menuType = new ArrayList<>();

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Blog> blog = new ArrayList<>();

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Banners> banners = new ArrayList<>();

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<PageContent> pageContent = new ArrayList<>();

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<FrequentlyAskedQuestion> frequentlyAskedQuestion = new ArrayList<>();

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Facility> facility = new ArrayList<>();

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Cta> cta = new ArrayList<>();

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Shortcut> shorcut = new ArrayList<>();

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Promotion> promotion = new ArrayList<>();
    
    
    
    
}
