/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;
import java.util.List;
import pe.tarjetaestilos.models.CorporateData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author julito
 */
public interface ICorporateDataService {
    Page<CorporateData> getCorporateData(Pageable pageable);
    
    List<CorporateData> getCorporateDataAll();
    
    CorporateData getCorporateDataById(Long id);
    
    CorporateData saveCorporateData(CorporateData corporateData);
    
    void deleteCorporateDataById(Long id);
}
