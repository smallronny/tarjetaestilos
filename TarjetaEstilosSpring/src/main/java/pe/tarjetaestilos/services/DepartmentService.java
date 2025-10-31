/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.tarjetaestilos.models.Department;
import pe.tarjetaestilos.repositories.DepartmentRepository;
/**
 *
 * @author user
 */

@Service
public class DepartmentService implements IDepartmentService{
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Override
    public Page<Department> getDepartment(Pageable pageable) {
        return this.departmentRepository.findAll(pageable);
    }
    
    public Page<Department> search(String name, Pageable pageable) {
        if (name != null && !name.isEmpty()) {
            return departmentRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            return departmentRepository.findAll(pageable);
        }
    }
    
    @Override
    public List<Department> getDepartmentAll() {
        return this.departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(Long id) {
        Department department = this.departmentRepository.findById(id).orElse(null);
        return department;
    }

    @Override
    public Department saveDepartment(Department department) {
        return this.departmentRepository.save(department);
    }

    @Override
    public void deleteDepartmentById(Long id) {
        this.departmentRepository.deleteById(id);
    }
}
