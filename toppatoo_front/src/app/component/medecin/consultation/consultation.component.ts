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

  patientId: number = 0;
  medecinId: number = 0;
  consultationId: number = 0;

  patient: Patient | null = null;
  loading = true;
  saving = false;
  erreur = '';
  etape = 1;

  // ═══════════════════════════════════════════════════════════════
  // ÉTAPE 1 — CONSULTATION
  // ═══════════════════════════════════════════════════════════════
  consultation = {
    compteRendu: '',
    objectifsMois: '',
    type: 'SUIVI' as string,
  };

  typesConsultation = [
    { value: 'PREMIERE', label: 'Première consultation' },
    { value: 'SUIVI',    label: 'Consultation de suivi' },
    { value: 'URGENCE',  label: 'Urgence' },
  ];

  // ═══════════════════════════════════════════════════════════════
  // ÉTAPE 2 — MESURES (UNE SEULE DÉCLARATION)
  // ═══════════════════════════════════════════════════════════════
  mesures: Mesure[] = [];

  nouvelleMesure: any = {
    type: 'GLYCEMIE',
    valeur: null,
    valeur2: null,
    unite: 'g/L',
    notes: ''
  };

  typesMesure = [
    { value: 'GLYCEMIE',             label: 'Glycémie',            unite: 'g/L',  besoinValeur2: false },
    { value: 'TENSION_ARTERIELLE',   label: 'Tension artérielle',  unite: 'mmHg', besoinValeur2: true  },
    { value: 'TEMPERATURE',          label: 'Température',         unite: '°C',   besoinValeur2: false },
    { value: 'POIDS',                label: 'Poids',               unite: 'kg',   besoinValeur2: false },
    { value: 'FREQUENCE_CARDIAQUE',  label: 'Fréquence cardiaque', unite: 'bpm',  besoinValeur2: false }
  ];

  // ═══════════════════════════════════════════════════════════════
  // ÉTAPE 3 — ORDONNANCE
  // ═══════════════════════════════════════════════════════════════
  ordonnance = {
    notes: '',
    dateExpiration: '',
    medicaments: [] as LigneMedicament[],
  };

  nouveauMedicament: LigneMedicament = {
    nomMedicament: '',
    dosage: '',
    posologie: '',
    dureeJours: 30,
    heuresRappel: ''
  };

  // ═══════════════════════════════════════════════════════════════
  // ÉTAPE 4 — RDV
  // ═══════════════════════════════════════════════════════════════
  rdv = {
    dateHeure: '',
    motif: 'Consultation de suivi mensuelle',
    lieu: '',
  };

  // ═══════════════════════════════════════════════════════════════
  // CONSTRUCTEUR
  // ═══════════════════════════════════════════════════════════════
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService,
    private consultationService: ConsultationService,
    private rendezVousService: RendezVousService,
  ) {}

  // ═══════════════════════════════════════════════════════════════
  // INIT
  // ═══════════════════════════════════════════════════════════════
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.patientId = id ? Number(id) : 0;

    const userId = sessionStorage.getItem('userId');
    this.medecinId = userId ? Number(userId) : 0;

    console.log('📋 Patient ID:', this.patientId);
    console.log('👨‍⚕️ Médecin ID:', this.medecinId);
    console.log('📊 Types de mesures disponibles:', this.typesMesure.length);

    this.chargerPatient();
  }

  chargerPatient(): void {
    this.patientService.getPatientById(this.patientId).subscribe({
      next: (p) => {
        this.patient = p;
        this.loading = false;
      },
      error: () => {
        this.erreur = 'Patient introuvable.';
        this.loading = false;
      },
    });
  }

  // ═══════════════════════════════════════════════════════════════
  // ÉTAPE 1 : Créer la consultation
  // ═══════════════════════════════════════════════════════════════
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
        this.consultationId = Number(c.id);
        console.log('✅ Consultation créée, ID:', this.consultationId);
        this.saving = false;
        this.etape = 2;
      },
      error: (err) => {
        console.error('❌ Erreur:', err);
        this.erreur = 'Erreur lors de la création.';
        this.saving = false;
      },
    });
  }

  // ═══════════════════════════════════════════════════════════════
  // ÉTAPE 2 : Mesures (méthodes)
  // ═══════════════════════════════════════════════════════════════

  onTypeMesureChange(): void {
    const found = this.typesMesure.find(t => t.value === this.nouvelleMesure.type);
    if (found) {
      this.nouvelleMesure.unite = found.unite;
      if (!found.besoinValeur2) {
        this.nouvelleMesure.valeur2 = null;
      }
      console.log('📊 Type changé:', found.label, '| Unité:', found.unite);
    }
  }

  isTension(): boolean {
    return this.nouvelleMesure.type === 'TENSION_ARTERIELLE';
  }

  getMesureLabel(type: string): string {
    const labels: { [key: string]: string } = {
      'GLYCEMIE': 'Glycémie',
      'TENSION_ARTERIELLE': 'Tension',
      'TEMPERATURE': 'Température',
      'POIDS': 'Poids',
      'FREQUENCE_CARDIAQUE': 'Fréq. cardiaque'
    };
    return labels[type] || type;
  }

  getNiveauClass(niveau?: string): string {
    if (niveau === 'CRITIQUE') return 'badge-danger';
    if (niveau === 'ATTENTION') return 'badge-warn';
    return 'badge-ok';
  }

  formatValeur(m: Mesure): string {
    if (m.valeur2) {
      return `${m.valeur} / ${m.valeur2} ${m.unite}`;
    }
    return `${m.valeur} ${m.unite}`;
  }

  ajouterMesure(): void {
    if (!this.nouvelleMesure.valeur) {
      this.erreur = 'Veuillez saisir une valeur.';
      return;
    }

    if (this.isTension() && !this.nouvelleMesure.valeur2) {
      this.erreur = 'Veuillez saisir la valeur diastolique.';
      return;
    }

    this.saving = true;
    this.erreur = '';

    const payload = {
      ...this.nouvelleMesure,
      patientId: this.patientId,
      consultationId: this.consultationId
    };

    console.log('📤 Envoi mesure:', payload);

    this.consultationService.ajouterMesure(this.consultationId, payload).subscribe({
      next: (m) => {
        this.mesures.push(m);
        console.log('✅ Mesure ajoutée:', m);

        // Reset
        this.nouvelleMesure = {
          type: 'GLYCEMIE',
          valeur: null,
          valeur2: null,
          unite: 'g/L',
          notes: ''
        };
        this.saving = false;
      },
      error: (err) => {
        console.error('❌ Erreur:', err);
        this.erreur = err.error?.message || 'Erreur lors de l\'ajout.';
        this.saving = false;
      },
    });
  }

  supprimerMesure(index: number): void {
    if (confirm('Supprimer cette mesure ?')) {
      this.mesures.splice(index, 1);
    }
  }

  // ═══════════════════════════════════════════════════════════════
  // ÉTAPE 3 : Ordonnance
  // ═══════════════════════════════════════════════════════════════
  ajouterMedicament(): void {
    if (!this.nouveauMedicament.nomMedicament.trim()) {
      this.erreur = 'Le nom du médicament est obligatoire.';
      return;
    }
    this.ordonnance.medicaments.push({ ...this.nouveauMedicament });
    this.nouveauMedicament = {
      nomMedicament: '',
      dosage: '',
      posologie: '',
      dureeJours: 30,
      heuresRappel: ''
    };
    this.erreur = '';
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
    this.erreur = '';

    this.consultationService.ajouterOrdonnance(this.consultationId, {
      notes: this.ordonnance.notes,
      dateExpiration: this.ordonnance.dateExpiration,
      medicaments: this.ordonnance.medicaments,
    }).subscribe({
      next: () => {
        this.saving = false;
        this.etape = 4;
      },
      error: () => {
        this.erreur = 'Erreur ordonnance.';
        this.saving = false;
      },
    });
  }

  // ═══════════════════════════════════════════════════════════════
  // ÉTAPE 4 : RDV
  // ═══════════════════════════════════════════════════════════════
  validerRdv(): void {
    if (!this.rdv.dateHeure) {
      this.terminer();
      return;
    }
    this.saving = true;
    this.erreur = '';

    this.rendezVousService.planifier({
      patientId: this.patientId,
      medecinId: this.medecinId,
      dateHeure: this.rdv.dateHeure,
      motif: this.rdv.motif,
      lieu: this.rdv.lieu,
    }).subscribe({
      next: () => {
        this.saving = false;
        this.terminer();
      },
      error: () => {
        this.erreur = 'Erreur RDV.';
        this.saving = false;
      },
    });
  }

  // ═══════════════════════════════════════════════════════════════
  // NAVIGATION
  // ═══════════════════════════════════════════════════════════════
  terminer(): void {
    this.router.navigate(['/medecin/patients']);
  }

  retour(): void {
    this.router.navigate(['/medecin/patients', this.patientId, 'detail']);
  }

  // ═══════════════════════════════════════════════════════════════
  // UTILITAIRES
  // ═══════════════════════════════════════════════════════════════
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