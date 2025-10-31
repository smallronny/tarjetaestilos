/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import pe.tarjetaestilos.models.Department;
import pe.tarjetaestilos.services.DepartmentService;
import pe.tarjetaestilos.dto.department.DepartmentSearchDto;
import org.springframework.data.domain.Sort;

import pe.tarjetaestilos.dto.ApiResponseDto;
import pe.tarjetaestilos.dto.department.DepartmentCreateDto;

import org.springframework.web.bind.annotation.PutMapping;
import pe.tarjetaestilos.repositories.DepartmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.server.ResponseStatusException;
import java.time.ZonedDateTime;

/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos")
public class DepartmentController {
    private final DepartmentRepository departmentRepository;
    
    public DepartmentController(
            DepartmentRepository departmentRepository
    ) {
        this.departmentRepository = departmentRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);
    
    @Autowired
    private DepartmentService departmentService;
    
    @GetMapping("/cms/departmentAll") //http://localhost:8081/tarjeta-estilos-app/category
    public List<Department> getDepartmentsAll(){
        List<Department> departments = this.departmentService.getDepartmentAll();
        logger.info("registos obtenidos:");
        departments.forEach(department-> logger.info(department.toString()));
        return departments;
    }
    
    @GetMapping("/cms/department/{page}/{size}") 
    public Page<Department> getDepartments(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return departmentService.getDepartment(pageable);
    }
    
    @PostMapping("/cms/department/search")
    public Page<Department> searchDepartments(@RequestBody DepartmentSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return departmentService.search(dto.getName(), pageable);
    }
    
    //Crear departamento
    @PostMapping(value = "/cms/department/create")
    public ResponseEntity<ApiResponseDto<Department>> createBlog(
            @Valid @RequestBody DepartmentCreateDto dto
    ) {
        try {
            Department department = new Department();
            department.setName(dto.getName());

            Department savedDepartment = departmentService.saveDepartment(department);
            
            
            Department newDepartment = departmentService.getDepartmentById(savedDepartment.getId());
            
            String successMessage = "Departamento registrado con ID " + savedDepartment.getId();
            ApiResponseDto<Department> response = new ApiResponseDto<>(successMessage, newDepartment, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            String errorMessage = "Error al registrar departamento: " + e.getMessage();
            ApiResponseDto<Department> response = new ApiResponseDto<>(errorMessage, null,HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //crear departamento
    
    
    //Modificar departamento
    @PutMapping(value = "/cms/department/update/{id}")
    public ResponseEntity<ApiResponseDto<Department>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentCreateDto dto
    ) {
        try {
            Department existingDepartment = departmentRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Departamento no encontrada con id " + id));

            existingDepartment.setName(dto.getName());
            existingDepartment.setLabel_phone(dto.getLabel_phone());
            existingDepartment.setPhone(dto.getPhone());

            Department updatedDepartment = departmentService.saveDepartment(existingDepartment);

            String successMessage = "Departamento actualizado con ID " + updatedDepartment.getId();
            ApiResponseDto<Department> response =
                    new ApiResponseDto<>(successMessage, updatedDepartment, HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al actualizar departamento: " + e.getMessage();
            ApiResponseDto<Department> response =
                    new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //Modificar departamento
    
    
    //Eliminar departamento
    @DeleteMapping("/cms/department/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteBlog(@PathVariable Long id) {
        try {
            Department existingDepartment = departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Departamento no encontrada con id " + id));

            // Si ya estaba eliminado, puedes devolver un 400 opcionalmente
            if (existingDepartment.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseDto<>("El departamento ya fue eliminado", null, HttpStatus.BAD_REQUEST.value()));
            }

            // Marcar como eliminado (soft delete)
            existingDepartment.setDeletedAt(ZonedDateTime.now());
            departmentRepository.save(existingDepartment);

            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Departamento eliminado con ID " + id,
                    null,
                    HttpStatus.OK.value()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDto<Void> response = new ApiResponseDto<>(
                    "Error al eliminar departamento: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //Eliminar Categoria
}
