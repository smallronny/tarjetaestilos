/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.tarjetaestilos.services;

import java.util.List;
import pe.tarjetaestilos.models.User;
/**
 *
 * @author julito
 */
public interface IUserService {
    List<User> getUser();
    
    User getUserById(Long id);
    
    void saveUser(User blog);
    
    void deleteUserById(Long id);
    
    User login(String email, String password);
    
    User getByEmail(String email);
}
