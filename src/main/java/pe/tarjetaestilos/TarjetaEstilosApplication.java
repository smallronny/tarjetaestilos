package pe.tarjetaestilos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pe.tarjetaestilos.models.Blog;
import pe.tarjetaestilos.models.Promotion;

@SpringBootApplication
public class TarjetaEstilosApplication implements CommandLineRunner{

    public static void main(String[] args) {
        SpringApplication.run(TarjetaEstilosApplication.class, args);
        
        
    }

    @Override
    public void run(String... args) throws Exception {
        Blog blog = new Blog();
        blog.setTitle("Julio");
        blog.setDescription("descripcion");

        System.out.println(blog); // Esto usará el toString() generado por Lombok
        System.out.println("Nombre: " + blog.getTitle());
        System.out.println("Edad: " + blog.getDescription());
    }
}
