package com.projet.toppatoo.controller;

import com.projet.toppatoo.dto.AuthRequestDTO;
import com.projet.toppatoo.dto.AuthResponseDTO;
import com.projet.toppatoo.dto.RegisterRequestDTO;
import com.projet.toppatoo.dto.UserDTO;
import com.projet.toppatoo.model.Medecin;
import com.projet.toppatoo.model.Patient;
import com.projet.toppatoo.model.User;
import com.projet.toppatoo.repository.MedecinRepository;
import com.projet.toppatoo.repository.PatientRepository;
import com.projet.toppatoo.repository.UserRepository;
import com.projet.toppatoo.security.JwtTokenProvider;
import com.projet.toppatoo.security.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final RateLimiter rateLimiter;

    // ─── LOGIN ──────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody AuthRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        String clientIp = httpRequest.getRemoteAddr();

        System.out.println("=========================================");
        System.out.println("TENTATIVE DE CONNEXION");
        System.out.println("Email: " + request.getEmail());
        System.out.println("IP: " + clientIp);
        System.out.println("=========================================");

        if (rateLimiter.isBlocked(clientIp)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Trop de tentatives de connexion. Réessayez dans 15 minutes.");
            return ResponseEntity.status(429).body(response);
        }

        try {
            // 1. Vérifier si l'utilisateur existe
            User user = userRepository.findByEmail(request.getEmail()).orElse(null);

            if (user == null) {
                rateLimiter.recordAttempt(clientIp);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Email ou mot de passe incorrect");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 2. Vérifier si le compte est actif
            if (!user.getActif()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Compte désactivé. Contactez l'administrateur.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 3. Vérifier le mot de passe
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                rateLimiter.recordAttempt(clientIp);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Email ou mot de passe incorrect");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 4. Authentification Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            rateLimiter.resetAttempts(clientIp);

            // 5. Génération des tokens
            String accessToken  = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);

            // 6. Récupérer l'ID métier selon le rôle
            Long medecinId = null;
            Long patientId = null;

            if ("MEDECIN".equals(user.getRole())) {
                medecinId = medecinRepository.findByUserId(user.getId())
                        .map(Medecin::getId)
                        .orElse(null);
                System.out.println("MedecinId: " + medecinId);
            } else if ("PATIENT".equals(user.getRole())) {
                patientId = patientRepository.findByUserId(user.getId())
                        .map(Patient::getId)
                        .orElse(null);
                System.out.println("PatientId: " + patientId);
            }

            // 7. Construction de la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("success",      true);
            response.put("message",      "Connexion réussie");
            response.put("accessToken",  accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType",    "Bearer");
            response.put("expiresIn",    3600000L);
            response.put("user", Map.of(
                "id",       user.getId(),
                "nom",      user.getNom(),
                "prenom",   user.getPrenom(),
                "email",    user.getEmail(),
                "role",     user.getRole(),
                "actif",    user.getActif(),
                "medecinId", medecinId != null ? medecinId : "",
                "patientId", patientId != null ? patientId : ""
            ));

            System.out.println("CONNEXION REUSSIE pour: " + user.getEmail());
            System.out.println("Role: " + user.getRole());
            System.out.println("MedecinId: " + medecinId + " | PatientId: " + patientId);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            rateLimiter.recordAttempt(clientIp);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Email ou mot de passe incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (Exception e) {
            System.err.println("ERREUR GENERALE: " + e.getMessage());
            rateLimiter.recordAttempt(clientIp);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la connexion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ─── REGISTER ───────────────────────────────────────────────────
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("=========================================");
            System.out.println("📝 INSCRIPTION");
            System.out.println("📧 Email: " + request.getEmail());
            System.out.println("👤 Nom: " + request.getNom());
            System.out.println("👤 Prénom: " + request.getPrenom());
            System.out.println("🎭 Rôle: " + request.getRole());
            System.out.println("=========================================");

            // ==============================
            // 1. VALIDATION EMAIL
            // ==============================
            if (userRepository.existsByEmail(request.getEmail())) {
                System.out.println("❌ Email déjà utilisé: " + request.getEmail());
                response.put("success", false);
                response.put("message", "Un compte existe déjà avec cet email.");
                return ResponseEntity.badRequest().body(response);
            }

            // ==============================
            // 2. VALIDATION ROLE
            // ==============================
            if (request.getRole() == null || request.getRole().trim().isEmpty()) {
                System.out.println("❌ Rôle manquant");
                response.put("success", false);
                response.put("message", "Le rôle est obligatoire.");
                return ResponseEntity.badRequest().body(response);
            }

            String role = request.getRole().trim().toUpperCase();

            if (!role.equals("PATIENT") && !role.equals("MEDECIN")) {
                System.out.println("❌ Rôle invalide: " + role);
                response.put("success", false);
                response.put("message", "Rôle invalide. Utilisez PATIENT ou MEDECIN.");
                return ResponseEntity.badRequest().body(response);
            }

            // ==============================
            // 3. VALIDATION MEDECIN
            // ==============================
            if (role.equals("MEDECIN")) {
                if (request.getSpecialite() == null || request.getSpecialite().trim().isEmpty()) {
                    System.out.println("❌ Spécialité manquante pour médecin");
                    response.put("success", false);
                    response.put("message", "La spécialité est obligatoire pour un médecin.");
                    return ResponseEntity.badRequest().body(response);
                }
                System.out.println("✅ Spécialité: " + request.getSpecialite());
            }

            // ==============================
            // 4. VALIDATION PATIENT
            // ==============================
            if (role.equals("PATIENT")) {
                if (request.getDateNaissance() == null) {
                    System.out.println("❌ Date de naissance manquante");
                    response.put("success", false);
                    response.put("message", "La date de naissance est obligatoire.");
                    return ResponseEntity.badRequest().body(response);
                }
                if (request.getSexe() == null || request.getSexe().trim().isEmpty()) {
                    System.out.println("❌ Sexe manquant");
                    response.put("success", false);
                    response.put("message", "Le sexe est obligatoire.");
                    return ResponseEntity.badRequest().body(response);
                }
                if (request.getMaladieChronique() == null || request.getMaladieChronique().trim().isEmpty()) {
                    System.out.println("❌ Maladie chronique manquante");
                    response.put("success", false);
                    response.put("message", "La maladie chronique est obligatoire.");
                    return ResponseEntity.badRequest().body(response);
                }
                System.out.println("✅ Date naissance: " + request.getDateNaissance());
                System.out.println("✅ Sexe: " + request.getSexe());
                System.out.println("✅ Maladie: " + request.getMaladieChronique());
            }

            // ==============================
            // 5. CREATION USER
            // ==============================
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setNom(request.getNom());
            user.setPrenom(request.getPrenom());
            user.setTelephone(request.getTelephone());
            user.setRole(role);
            user.setActif(true);

            User savedUser = userRepository.save(user);
            System.out.println("✅ Utilisateur créé avec ID: " + savedUser.getId());

            // ==============================
            // 6. CREATION MEDECIN
            // ==============================
            if (role.equals("MEDECIN")) {
                Medecin medecin = Medecin.builder()
                        .user(savedUser)
                        .specialite(request.getSpecialite())
                        .etablissement(request.getEtablissement())
                        .ville(request.getVille())
                        .build();

                medecinRepository.save(medecin);
                System.out.println("✅ Médecin créé avec succès");
                System.out.println("=========================================");
                System.out.println("✅ INSCRIPTION MÉDECIN RÉUSSIE");
                System.out.println("=========================================");

                response.put("success", true);
                response.put("message", "Médecin créé avec succès");
                response.put("userId", savedUser.getId());
                response.put("role", role);
                return ResponseEntity.ok(response);
            }

            // ==============================
            // 7. CREATION PATIENT
            // ==============================
            if (role.equals("PATIENT")) {
                Patient patient = Patient.builder()
                        .user(savedUser)
                        .dateNaissance(request.getDateNaissance())
                        .sexe(request.getSexe())
                        .maladieChronique(request.getMaladieChronique())
                        .medecinId(request.getMedecinId())
                        .build();

                patientRepository.save(patient);
                System.out.println("✅ Patient créé avec succès");
                System.out.println("=========================================");
                System.out.println("✅ INSCRIPTION PATIENT RÉUSSIE");
                System.out.println("=========================================");

                response.put("success", true);
                response.put("message", "Patient créé avec succès");
                response.put("userId", savedUser.getId());
                response.put("role", role);
                return ResponseEntity.ok(response);
            }

            response.put("success", false);
            response.put("message", "Erreur lors de l'inscription.");
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            System.err.println("❌ ERREUR INSCRIPTION: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Erreur lors de la création du compte: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ─── REFRESH TOKEN ─────────────────────────────────────────────
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        Map<String, Object> response = new HashMap<>();
        
        System.out.println("🔄 TENTATIVE DE RAFFRAÎCHISSEMENT DE TOKEN");
        
        if (refreshToken == null || refreshToken.isEmpty()) {
            System.out.println("❌ Refresh token manquant");
            response.put("success", false);
            response.put("message", "Refresh token requis");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            if (tokenProvider.validateToken(refreshToken)) {
                String username = tokenProvider.getUsernameFromToken(refreshToken);
                System.out.println("✅ Username extrait du token: " + username);
                
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, null);
                
                String newAccessToken = tokenProvider.generateToken(authentication);
                System.out.println("✅ Nouveau Access Token généré");
                
                response.put("success", true);
                response.put("accessToken", newAccessToken);
                response.put("refreshToken", refreshToken);
                response.put("message", "Token rafraîchi avec succès");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ Refresh token invalide");
                response.put("success", false);
                response.put("message", "Refresh token invalide");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur refresh token: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erreur lors du rafraîchissement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ─── LOGOUT ─────────────────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        System.out.println("🚪 DÉCONNEXION");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Déconnexion réussie");
        response.put("timestamp", LocalDateTime.now().toString());
        
        System.out.println("✅ Déconnexion réussie");
        return ResponseEntity.ok(response);
    }

    // ─── HEALTH CHECK ──────────────────────────────────────────────
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Auth Service");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}