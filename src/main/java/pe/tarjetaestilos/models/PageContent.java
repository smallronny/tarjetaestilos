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
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name="page_content")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class PageContent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "TEXT")
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String image;
    
    @Column(columnDefinition = "TEXT")
    private String image_position;
    
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
    
    @OneToMany(mappedBy = "pageContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemContent> itemContents = new ArrayList<>();
    
}
