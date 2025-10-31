/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import pe.tarjetaestilos.models.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author user
 */
public interface IDepartmentService {
    Page<Department> getDepartment(Pageable pageable);
    
    List<Department> getDepartmentAll();
    
    Department getDepartmentById(Long id);
    
    Department saveDepartment(Department blog);
    
    void deleteDepartmentById(Long id);
}
