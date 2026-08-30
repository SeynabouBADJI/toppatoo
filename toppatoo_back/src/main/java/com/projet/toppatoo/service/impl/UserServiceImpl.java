
package com.projet.toppatoo.service.impl;

import com.projet.toppatoo.dto.UserDTO;
import com.projet.toppatoo.model.User;
import com.projet.toppatoo.repository.UserRepository;
import com.projet.toppatoo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + id));
        return UserDTO.from(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + email));
        return UserDTO.from(user);
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setNom(userDTO.getNom());
        user.setPrenom(userDTO.getPrenom());
        user.setTelephone(userDTO.getTelephone());
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : "PATIENT");
        user.setActif(userDTO.getActif() != null ? userDTO.getActif() : true);
        // Mot de passe : à encoder depuis une source séparée (pas dans le DTO)
        user.setPassword(passwordEncoder.encode("changeme123"));

        return UserDTO.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + id));

        if (userDTO.getNom() != null)      user.setNom(userDTO.getNom());
        if (userDTO.getPrenom() != null)   user.setPrenom(userDTO.getPrenom());
        if (userDTO.getTelephone() != null) user.setTelephone(userDTO.getTelephone());
        if (userDTO.getRole() != null)     user.setRole(userDTO.getRole());
        if (userDTO.getActif() != null)    user.setActif(userDTO.getActif());

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new RuntimeException("Cet email est déjà utilisé");
            }
            user.setEmail(userDTO.getEmail());
        }

        return UserDTO.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDTO activerUser(Long id, boolean actif) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + id));
        user.setActif(actif);
        return UserDTO.from(userRepository.save(user));
    }
}