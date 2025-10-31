/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.models;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
/**
 *
 * @author julito
 */

@Entity
@Table(name = "sections_menus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class SectionsMenus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude 
    private Menu menu;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    @ToString.Exclude
    @NotFound(action = NotFoundAction.IGNORE)
    private Section section;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
    
}
