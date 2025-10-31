/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.models;

/**
 *
 * @author julito
 */
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Where;


@Entity
@Table(name = "section")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class Section {
         
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
            
    private String title;        
    
    @ManyToOne
    @JoinColumn(name = "section_type_id", nullable = false)
    private SectionType sectionType;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
            
}
