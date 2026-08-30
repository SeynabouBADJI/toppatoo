import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Patient, MALADIE_LABELS } from '../../../core/models/patient.model';
import { PatientService } from '../../../core/services/patient.service';

@Component({
  selector: 'app-patient-liste',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './patient-liste.component.html',
  styleUrls: ['./patient-liste.component.scss'],
})
export class PatientListeComponent implements OnInit {

  patients: Patient[] = [];
  loading = true;
  erreur = '';
  maladieLabels = MALADIE_LABELS;
  medecinId = sessionStorage.getItem('userId') ?? '';

  constructor(private patientService: PatientService, private router: Router) {}

  ngOnInit(): void {
    this.chargerPatients();
  }

  chargerPatients(): void {
    this.loading = true;
    this.patientService.getPatientsByMedecin(this.medecinId).subscribe({
      next: (data) => { this.patients = data; this.loading = false; },
      error: () => { this.erreur = 'Impossible de charger les patients.'; this.loading = false; },
    });
  }

  ouvrirDossier(patientId: string): void {
    this.router.navigate(['/medecin/patients', patientId, 'detail']);
  }

  nouveauPatient(): void {
    this.router.navigate(['/medecin/patients/nouveau']);
  }

  calculerAge(dateNaissance: string): number {
    const n = new Date(dateNaissance);
    const a = new Date();
    let age = a.getFullYear() - n.getFullYear();
    const m = a.getMonth() - n.getMonth();
    if (m < 0 || (m === 0 && a.getDate() < n.getDate())) age--;
    return age;
  }

  initiales(patient: Patient): string {
  if (!patient?.user) return '?';
  const p = patient.user.prenom?.charAt(0) ?? '';
  const n = patient.user.nom?.charAt(0) ?? '';
  return (p + n).toUpperCase() || '?';
}
}