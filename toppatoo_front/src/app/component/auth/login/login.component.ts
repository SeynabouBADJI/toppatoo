import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

type Role = 'MEDECIN' | 'PATIENT';
type Vue = 'choix' | 'login' | 'inscription';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {

  vue: Vue = 'choix';
  role: Role | '' = '';
  loading = false;
  erreur = '';
  succes = '';

  loginForm = { email: '', password: '' };

  medecinForm = {
    nom: '', prenom: '', email: '', password: '', confirmPassword: '',
    telephone: '', specialite: '', etablissement: '', ville: ''
  };

  patientForm = {
    nom: '', prenom: '', email: '', password: '', confirmPassword: '',
    telephone: '', dateNaissance: '', sexe: '',
    maladieChronique: '', medecinId: ''
  };

  maladies = [
    { value: 'DIABETE_TYPE1',          label: 'Diabète type 1' },
    { value: 'DIABETE_TYPE2',          label: 'Diabète type 2' },
    { value: 'HYPERTENSION',           label: 'Hypertension' },
    { value: 'INSUFFISANCE_RENALE',    label: 'Insuffisance rénale' },
    { value: 'INSUFFISANCE_CARDIAQUE', label: 'Insuffisance cardiaque' },
    { value: 'ASTHME',                 label: 'Asthme' },
    { value: 'BPCO',                   label: 'BPCO' },
    { value: 'AUTRE',                  label: 'Autre' },
  ];

  constructor(private authService: AuthService, private router: Router) {}

  choisirRole(role: Role): void {
    this.role = role;
    this.erreur = '';
    this.vue = 'login';
  }

  allerInscription(): void {
    this.erreur = '';
    this.succes = '';
    this.vue = 'inscription';
  }

  retourChoix(): void {
    this.vue = 'choix';
    this.role = '';
    this.erreur = '';
    this.succes = '';
  }

  retourLogin(): void {
    this.vue = 'login';
    this.erreur = '';
    this.succes = '';
  }

  onLogin(): void {
    if (!this.loginForm.email || !this.loginForm.password) {
      this.erreur = 'Veuillez remplir tous les champs.';
      return;
    }

    this.loading = true;
    this.erreur = '';

    this.authService.login(this.loginForm).subscribe({
      next: (res) => {
  if (res.user?.role !== this.role) {
    this.erreur = `Ce compte n'est pas un compte ${this.role === 'MEDECIN' ? 'médecin' : 'patient'}.`;
    this.loading = false;
    return;
  }

  localStorage.setItem('accessToken', res.accessToken);
  localStorage.setItem('refreshToken', res.refreshToken);

  sessionStorage.setItem('role',   res.user.role);
  sessionStorage.setItem('nom',    res.user.nom);
  sessionStorage.setItem('prenom', res.user.prenom);

  if (res.user.role === 'MEDECIN') {
    sessionStorage.setItem('userId', String(res.user.medecinId)); // ← ID du médecin
    this.router.navigate(['/medecin/patients']);
  } else if (res.user.role === 'PATIENT') {
    sessionStorage.setItem('userId', String(res.user.patientId)); // ← ID du patient
    this.router.navigate(['/patient/dashboard']);
  }

  this.loading = false;
},
      error: (err) => {
        this.erreur = err.error?.message ?? 'Email ou mot de passe incorrect.';
        this.loading = false;
      },
    });
  }

  onInscription(): void {
    this.erreur = '';
    if (this.role === 'MEDECIN') {
      this.inscriptionMedecin();
    } else if (this.role === 'PATIENT') {
      this.inscriptionPatient();
    }
  }

  private inscriptionMedecin(): void {
    const f = this.medecinForm;
    if (!f.nom || !f.prenom || !f.email || !f.password || !f.specialite) {
      this.erreur = 'Veuillez remplir tous les champs obligatoires.';
      return;
    }
    if (f.password !== f.confirmPassword) {
      this.erreur = 'Les mots de passe ne correspondent pas.';
      return;
    }
    this.loading = true;

    // Envoyer seulement les champs attendus par le backend
    const payload = {
      nom: f.nom,
      prenom: f.prenom,
      email: f.email,
      password: f.password,
      telephone: f.telephone || '',
      specialite: f.specialite,
      etablissement: f.etablissement || '',
      ville: f.ville || ''
    };

    this.authService.registerMedecin(payload).subscribe({
      next: () => {
        this.succes = 'Compte créé ! Vous pouvez vous connecter.';
        this.loading = false;
        setTimeout(() => this.retourLogin(), 2000);
      },
      error: (err) => {
        this.erreur = err.error?.message ?? 'Erreur lors de la création du compte.';
        this.loading = false;
      },
    });
  }

  private inscriptionPatient(): void {
    const f = this.patientForm;
    if (!f.nom || !f.prenom || !f.email || !f.password
        || !f.dateNaissance || !f.sexe || !f.maladieChronique) {
      this.erreur = 'Veuillez remplir tous les champs obligatoires.';
      return;
    }
    if (f.password !== f.confirmPassword) {
      this.erreur = 'Les mots de passe ne correspondent pas.';
      return;
    }
    this.loading = true;
    this.authService.registerPatient({ ...f, medecinId: f.medecinId || null }).subscribe({
      next: () => {
        this.succes = 'Compte créé ! Vous pouvez vous connecter.';
        this.loading = false;
        setTimeout(() => this.retourLogin(), 2000);
      },
      error: (err) => {
        this.erreur = err.error?.message ?? 'Erreur lors de la création du compte.';
        this.loading = false;
      },
    });
  }
}