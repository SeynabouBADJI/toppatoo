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
  styleUrls: ['./patient-liste.component.scss']
})
export class PatientListeComponent implements OnInit {

  // ─── Données ───────────────────────────────────────────────────
  patients: Patient[] = [];
  loading = true;
  erreur = '';

  // ✅ Exposer MALADIE_LABELS au template
  maladieLabels = MALADIE_LABELS;

  // ─── Constructeur ──────────────────────────────────────────────
  constructor(
    private patientService: PatientService,
    private router: Router
  ) {}

  // ─── Init ──────────────────────────────────────────────────────
  ngOnInit(): void {
    this.chargerPatients();
  }

  chargerPatients(): void {
    this.loading = true;
    this.erreur = '';

    this.patientService.getAllPatients().subscribe({
      next: (data: Patient[]) => {
        this.patients = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Erreur chargement patients:', err);
        this.erreur = 'Erreur lors du chargement des patients.';
        this.loading = false;
      }
    });
  }

  // ─── Méthodes utilisées dans le template ──────────────────────

  // ✅ Ouvrir le dossier d'un patient
  ouvrirDossier(id: number): void {
    this.router.navigate(['/medecin/patients', id, 'detail']);
  }

  // ✅ Nouveau patient
  nouveauPatient(): void {
    this.router.navigate(['/medecin/nouveau-patient']);
  }

  // ✅ Initiales pour l'avatar
  initiales(patient: Patient): string {
    if (!patient?.user) return '';
    const prenom = patient.user.prenom?.charAt(0) ?? '';
    const nom = patient.user.nom?.charAt(0) ?? '';
    return (prenom + nom).toUpperCase();
  }

  // ✅ Calculer l'âge
  calculerAge(dateNaissance: string): number {
    if (!dateNaissance) return 0;
    const n = new Date(dateNaissance);
    const a = new Date();
    let age = a.getFullYear() - n.getFullYear();
    const m = a.getMonth() - n.getMonth();
    if (m < 0 || (m === 0 && a.getDate() < n.getDate())) age--;
    return age;
  }
}