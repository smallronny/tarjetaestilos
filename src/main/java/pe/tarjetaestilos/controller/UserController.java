/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tarjetaestilos.controller;

/**
 *
 * @author user
 */
import java.util.UUID;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.tarjetaestilos.models.User;
import pe.tarjetaestilos.services.UserService;
import pe.tarjetaestilos.dto.user.LoginRequestDto;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.util.Collections;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import pe.tarjetaestilos.dto.user.LoginResponseDto;
import pe.tarjetaestilos.security.CustomUserDetails;
import pe.tarjetaestilos.services.JwtService;

@RestController
@RequestMapping("tarjeta-estilos") //http://localhost:8081/tarjeta-estilos
@CrossOrigin(value="http://localhost:5173") //puerto de vue
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authManager;
        

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @GetMapping("/cms/user") //http://localhost:8081/tarjeta-estilos-app/category
    public List<User> getUsers(){
        List<User> users = this.userService.getUser();
        logger.info("registos obtenidos:");
        users.forEach(user-> logger.info(user.toString()));
        return users;
    }
    
    @PostMapping(value = "/auth/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("fullname") String name,
            @RequestParam("phone") String phone,
            @RequestParam("rol") String rol,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            
            // Obtener nombre original y extensión
            String originalFilename = image.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String uniqueFilename = System.currentTimeMillis() + "_" + UUID.randomUUID() + extension;
            
            Path uploadPath = Paths.get("uploads/user/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setFullname(name);
            user.setPhone(phone);
            user.setRol(rol);
            user.setImage(filePath.toString()); 

            userService.saveUser(user);

            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado con foto");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar la foto");
        }
    }
    
    @PostMapping(value = "/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto authRequest) {
        try {
            logger.info("authentication");

            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            String token = jwtService.generateToken(userDetails);
            
            LoginResponseDto response = new LoginResponseDto(token, user,HttpStatus.OK.value(),"success");
            //return ResponseEntity.ok(response);
            return ResponseEntity.ok(response); 
        } catch (BadCredentialsException ex) {
            // Credenciales incorrectas
            LoginResponseDto response = new LoginResponseDto(null, null, HttpStatus.UNAUTHORIZED.value(), "Credenciales incorrectas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            // Otro error inesperado
            LoginResponseDto response = new LoginResponseDto(null, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error inesperado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }    
    }
    
}
