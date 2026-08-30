package com.projet.toppatoo.service;

import com.projet.toppatoo.dto.UserDTO;
import java.util.List;

public interface UserService {
    
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    UserDTO createUser(UserDTO userDTO);  // ✅ Utilise UserDTO
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserDTO activerUser(Long id, boolean actif);
}