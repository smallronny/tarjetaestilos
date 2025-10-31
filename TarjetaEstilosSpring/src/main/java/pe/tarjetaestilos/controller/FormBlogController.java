/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.controller;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.UUID;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.time.ZonedDateTime;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import java.io.ByteArrayOutputStream;
import org.springframework.http.HttpHeaders;



//imports tarjetas
import pe.tarjetaestilos.models.FormBlog;
import pe.tarjetaestilos.models.Menu;
import pe.tarjetaestilos.services.FormBlogService;
import pe.tarjetaestilos.dto.formBlog.FormBlogCreateDto;
import pe.tarjetaestilos.dto.formBlog.FormBlogSearchDto;
import pe.tarjetaestilos.repositories.FormBlogRepository;
import pe.tarjetaestilos.repositories.MenuRepository;
import pe.tarjetaestilos.dto.ApiResponseDto;

/**
 *
 * @author julito
 */

@RestController
@RequestMapping("tarjeta-estilos") 
public class FormBlogController {
    private final FormBlogRepository formBlogRepository;
    private final MenuRepository menuRepository;

    public FormBlogController(
            FormBlogRepository formBlogRepository,
            MenuRepository menuRepository
    ) {
        this.formBlogRepository = formBlogRepository;
        this.menuRepository = menuRepository;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(CtaController.class);
    
    @Autowired
    private FormBlogService formBlogService;
    
    @GetMapping("/cms/descargar-excel")
    public ResponseEntity<byte[]> descargarExcel() throws IOException {
        List<FormBlog> registros = formBlogRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Form Blogs");

        // --- Crear estilos ---
        // Estilo de cabecera
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        // Estilo para las filas (blanco con bordes)
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // --- Cabecera ---
        Row header = sheet.createRow(0);
        String[] columnas = {"ID", "DNI", "Teléfono", "Email"};
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        // --- Datos ---
        int rowIdx = 1;
        for (FormBlog form : registros) {
            Row row = sheet.createRow(rowIdx++);
            Cell c0 = row.createCell(0);
            c0.setCellValue(form.getId());
            c0.setCellStyle(dataStyle);

            Cell c1 = row.createCell(1);
            c1.setCellValue(form.getDni());
            c1.setCellStyle(dataStyle);

            Cell c2 = row.createCell(2);
            c2.setCellValue(form.getPhone());
            c2.setCellStyle(dataStyle);

            Cell c3 = row.createCell(3);
            c3.setCellValue(form.getEmail());
            c3.setCellStyle(dataStyle);
        }

        // --- Ajustar ancho de columnas ---
        sheet.setColumnWidth(0, 10 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 20 * 256);
        sheet.setColumnWidth(3, 30 * 256);

        // --- Congelar la fila de cabecera ---
        sheet.createFreezePane(0, 1); // mantiene fija la fila 1 al hacer scroll

        // --- Convertir a bytes ---
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        byte[] excelBytes = out.toByteArray();

        // --- Retornar descarga ---
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=form_blog.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
    
    @GetMapping("/cms/form-blogAll") 
    public List<FormBlog> getCtaAll(){
        List<FormBlog> formBlogs = this.formBlogService.getFormBlogAll();
        logger.info("registos obtenidos:");
        formBlogs.forEach(formBlog-> logger.info(formBlog.toString()));
        return formBlogs;
    }
    
    @GetMapping("/cms/form-blog/{page}/{size}") 
    public Page<FormBlog> getFormBlog(
        @PathVariable int page,
        @PathVariable int size
    ) {
        Pageable pageable = PageRequest.of((page-1), size, Sort.by("id").descending());
        return formBlogService.getFormBlog(pageable);
    }
    
    @PostMapping("/cms/form-blog/search")
    public Page<FormBlog> searchFormBlog(@RequestBody FormBlogSearchDto dto) {
        Pageable pageable = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() - 0 : 10,
            Sort.by("id").descending()
        );

        return formBlogService.search(dto.getDni(),dto.getEmail(),dto.getMenu_id(), pageable);
    }
    
    @PostMapping(value = "/web/form-blog/create")
    public ResponseEntity<ApiResponseDto<FormBlog>> createCta(
            @Valid @RequestBody FormBlogCreateDto dto
    ) {
        try {
            FormBlog formBlog = new FormBlog();
            Menu menu = menuRepository.findById(dto.getMenu_id())
                    .orElseThrow(() -> new RuntimeException("Menu no encontrado id " + dto.getMenu_id()));
            formBlog.setDni(dto.getDni());
            formBlog.setEmail(dto.getEmail());
            formBlog.setPhone(dto.getPhone());          
            formBlog.setMenu(menu);
            
            FormBlog savedFormBlog = formBlogService.saveFormBlog(formBlog);
            

            FormBlog newFormBlog = formBlogService.getFormBlogById(savedFormBlog.getId());

            String successMessage = "FormBlog registrado con ID " + savedFormBlog.getId();
            ApiResponseDto<FormBlog> response = new ApiResponseDto<>(successMessage, newFormBlog, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            String errorMessage = "Error al registrar banner: " + e.getMessage();
            ApiResponseDto<FormBlog> response = new ApiResponseDto<>(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    
    
    
}
