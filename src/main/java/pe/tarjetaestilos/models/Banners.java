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
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="banners")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class Banners {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "TEXT")
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String subtitle;
    
    @Column(columnDefinition = "TEXT")
    private String image;
        
    @Column(columnDefinition = "TEXT")
    private String label;
    
    @ManyToOne()
    @JoinColumn(name = "menu_id", nullable = false)
    @ToString.Exclude
    @JsonBackReference
    private Menu menu;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
    
    @Column(name = "blog_form", nullable = true)
    private Boolean blog_form;
    
    
    @OneToMany(mappedBy = "banners", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Button> buttons = new ArrayList<>();
    
    
}
