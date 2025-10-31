/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.Banners;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */

public interface IBannersService {
    Page<Banners> getBanners(Pageable pageable);
    
    List<Banners> getBannersAll();
    
    Banners getBannersById(Long id);
    
    Banners saveBanners(Banners banners);
    
    void deleteBannersById(Long id);
}
