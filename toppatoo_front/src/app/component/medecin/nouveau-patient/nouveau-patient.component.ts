import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PatientService, PatientCreeResponse } from '../../../core/services/patient.service';

@Component({
  selector: 'app-nouveau-patient',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './nouveau-patient.component.html',
  styleUrls: ['./nouveau-patient.component.scss'],
})
export class NouveauPatientComponent {

  loading = false;
  erreur = '';
  resultat: PatientCreeResponse | null = null;

  medecinId = sessionStorage.getItem('userId') ?? '';

  patient = {
    nom: '', prenom: '', email: '', telephone: '',
    dateNaissance: '', sexe: '', maladieChronique: '',
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

  constructor(
    private patientService: PatientService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.patient.nom || !this.patient.prenom || !this.patient.email
        || !this.patient.dateNaissance || !this.patient.sexe
        || !this.patient.maladieChronique) {
      this.erreur = 'Veuillez remplir tous les champs obligatoires.';
      return;
    }

    this.loading = true;
    this.erreur = '';

    const payload = { ...this.patient, medecinId: this.medecinId };

    this.patientService.creerPatient(payload).subscribe({
      next: (res) => {
        this.resultat = res;
        this.loading = false;
      },
      error: (err) => {
        this.erreur = err.error?.message ?? 'Erreur lors de la création du patient.';
        this.loading = false;
      },
    });
  }

  copierIdentifiants(): void {
    if (this.resultat) {
      const texte = `Email: ${this.resultat.email}\nMot de passe temporaire: ${this.resultat.motDePasseTemporaire}`;
      navigator.clipboard.writeText(texte);
    }
  }

  terminer(): void {
    this.router.navigate(['/medecin/patients']);
  }

  annuler(): void {
    this.router.navigate(['/medecin/patients']);
  }
}