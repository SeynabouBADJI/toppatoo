package com.projet.toppatoo.security;

import com.projet.toppatoo.model.User;
import com.projet.toppatoo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 Recherche de l'utilisateur: " + username); // ✅ LOG
        
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    System.out.println("❌ Utilisateur non trouvé: " + username);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + username);
                });

        System.out.println("✅ Utilisateur trouvé: " + user.getEmail() + ", rôle: " + user.getRole());
        System.out.println("🔑 Mot de passe hashé: " + user.getPassword());

        if (!user.getActif()) {
            System.out.println("⚠️ Compte désactivé: " + username);
            throw new UsernameNotFoundException("Compte désactivé");
        }

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}