import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Patient } from '../../../core/models/patient.model';
import { Mesure, LigneMedicament } from '../../../core/models/consultation.model';
import { PatientService } from '../../../core/services/patient.service';
import { ConsultationService } from '../../../core/services/consultation.service';
import { RendezVousService } from '../../../core/services/rendez-vous.service';

@Component({
  selector: 'app-consultation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './consultation.component.html',
  styleUrls: ['./consultation.component.scss'],
})
export class ConsultationComponent implements OnInit {

  // IDs
  patientId = '';
  medecinId = sessionStorage.getItem('userId') ?? '';

  // Données
  patient: Patient | null = null;
  loading = true;
  saving = false;
  erreur = '';
  etape = 1; // 1: Consultation, 2: Mesures, 3: Ordonnance, 4: RDV

  // Etape 1 — Consultation
  consultation = {
  compteRendu: '',
  objectifsMois: '',
  type: 'SUIVI' as string,
};
  consultationId = '';

  typesConsultation = [
    { value: 'PREMIERE', label: 'Première consultation' },
    { value: 'SUIVI',    label: 'Consultation de suivi' },
    { value: 'URGENCE',  label: 'Urgence' },
  ];

  // Etape 2 — Mesures
  mesures: Mesure[] = [];
  nouvelleMesure: Mesure = { type: 'GLYCEMIE', valeur: 0, unite: 'g/L' };

  typesMesure = [
    { value: 'GLYCEMIE',           label: 'Glycémie',           unite: 'g/L' },
    { value: 'TENSION_ARTERIELLE', label: 'Tension artérielle', unite: 'mmHg' },
    { value: 'POIDS',              label: 'Poids',              unite: 'kg' },
    { value: 'FREQUENCE_CARDIAQUE',label: 'Fréquence cardiaque',unite: 'bpm' },
    { value: 'SPO2',               label: 'SpO2',               unite: '%' },
    { value: 'TEMPERATURE',        label: 'Température',        unite: '°C' },
  ];

  // Etape 3 — Ordonnance
  ordonnance = {
    notes: '',
    dateExpiration: '',
    medicaments: [] as LigneMedicament[],
  };

  nouveauMedicament: LigneMedicament = {
    nomMedicament: '', dosage: '', posologie: '', dureeJours: 30, heuresRappel: ''
  };

  // Etape 4 — RDV
  rdv = {
    dateHeure: '',
    motif: 'Consultation de suivi mensuelle',
    lieu: '',
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService,
    private consultationService: ConsultationService,
    private rendezVousService: RendezVousService,
  ) {}

  ngOnInit(): void {
    this.patientId = this.route.snapshot.paramMap.get('id') ?? '';
    this.chargerPatient();
  }

  chargerPatient(): void {
    this.patientService.getPatientById(this.patientId).subscribe({
      next: (p) => { this.patient = p; this.loading = false; },
      error: () => { this.erreur = 'Patient introuvable.'; this.loading = false; },
    });
  }

  // ─── ETAPE 1 : Créer la consultation ───────────────────────────────────────
  validerConsultation(): void {
    if (!this.consultation.compteRendu.trim()) {
      this.erreur = 'Le compte-rendu est obligatoire.';
      return;
    }
    this.saving = true;
    this.erreur = '';

    this.consultationService.creer({
      patientId: this.patientId,
      medecinId: this.medecinId,
      compteRendu: this.consultation.compteRendu,
      objectifsMois: this.consultation.objectifsMois,
      type: this.consultation.type,
    }).subscribe({
      next: (c) => {
        this.consultationId = c.id!;
        this.saving = false;
        this.etape = 2;
      },
      error: () => { this.erreur = 'Erreur lors de la création.'; this.saving = false; },
    });
  }

  // ─── ETAPE 2 : Mesures ─────────────────────────────────────────────────────
  onTypeMesureChange(): void {
    const found = this.typesMesure.find(t => t.value === this.nouvelleMesure.type);
    if (found) this.nouvelleMesure.unite = found.unite;
  }

  ajouterMesure(): void {
    if (!this.nouvelleMesure.valeur) return;
    this.saving = true;

    this.consultationService.ajouterMesure(this.consultationId, this.nouvelleMesure).subscribe({
      next: (m) => {
        this.mesures.push(m);
        this.nouvelleMesure = { type: 'GLYCEMIE', valeur: 0, unite: 'g/L' };
        this.saving = false;
      },
      error: () => { this.erreur = 'Erreur lors de l\'ajout.'; this.saving = false; },
    });
  }

  isTension(): boolean {
    return this.nouvelleMesure.type === 'TENSION_ARTERIELLE';
  }

  // ─── ETAPE 3 : Ordonnance ──────────────────────────────────────────────────
  ajouterMedicament(): void {
    if (!this.nouveauMedicament.nomMedicament.trim()) return;
    this.ordonnance.medicaments.push({ ...this.nouveauMedicament });
    this.nouveauMedicament = { nomMedicament: '', dosage: '', posologie: '', dureeJours: 30, heuresRappel: '' };
  }

  supprimerMedicament(index: number): void {
    this.ordonnance.medicaments.splice(index, 1);
  }

  validerOrdonnance(): void {
    if (this.ordonnance.medicaments.length === 0) {
      this.etape = 4;
      return;
    }
    this.saving = true;

    this.consultationService.ajouterOrdonnance(this.consultationId, {
      notes: this.ordonnance.notes,
      dateExpiration: this.ordonnance.dateExpiration,
      medicaments: this.ordonnance.medicaments,
    }).subscribe({
      next: () => { this.saving = false; this.etape = 4; },
      error: () => { this.erreur = 'Erreur ordonnance.'; this.saving = false; },
    });
  }

  // ─── ETAPE 4 : RDV ─────────────────────────────────────────────────────────
  validerRdv(): void {
    if (!this.rdv.dateHeure) {
      this.terminer();
      return;
    }
    this.saving = true;

    this.rendezVousService.planifier({
      patientId: this.patientId,
      medecinId: this.medecinId,
      dateHeure: this.rdv.dateHeure,
      motif: this.rdv.motif,
      lieu: this.rdv.lieu,
    }).subscribe({
      next: () => { this.saving = false; this.terminer(); },
      error: () => { this.erreur = 'Erreur RDV.'; this.saving = false; },
    });
  }

  terminer(): void {
    this.router.navigate(['/medecin/patients']);
  }

 retour(): void {
  this.router.navigate(['/medecin/patients', this.patientId, 'detail']);
}

  calculerAge(dateNaissance: string): number {
    const n = new Date(dateNaissance);
    const a = new Date();
    let age = a.getFullYear() - n.getFullYear();
    const m = a.getMonth() - n.getMonth();
    if (m < 0 || (m === 0 && a.getDate() < n.getDate())) age--;
    return age;
  }

  getNiveauClass(niveau?: string): string {
    if (niveau === 'CRITIQUE') return 'badge-danger';
    if (niveau === 'ATTENTION') return 'badge-warn';
    return 'badge-ok';
  }
}