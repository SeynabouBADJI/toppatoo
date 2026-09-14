import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Patient, MALADIE_LABELS } from '../../../core/models/patient.model';
import { Consultation } from '../../../core/models/consultation.model';
import { PatientService } from '../../../core/services/patient.service';
import { ConsultationService } from '../../../core/services/consultation.service';

@Component({
  selector: 'app-patient-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './patient-detail.component.html',
  styleUrls: ['./patient-detail.component.scss']
})
export class PatientDetailComponent implements OnInit {

  patientId: number = 0;
  patient: Patient | null = null;
  consultations: Consultation[] = [];
  consultationSelectionnee: Consultation | null = null;
  loading = true;
  erreur = '';

  // ✅ Exposer MALADIE_LABELS au template
  maladieLabels = MALADIE_LABELS;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService,
    private consultationService: ConsultationService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.patientId = id ? Number(id) : 0;
    this.chargerPatient();
  }

  chargerPatient(): void {
    this.patientService.getPatientById(this.patientId).subscribe({
      next: (p) => {
        this.patient = p;
        this.chargerHistorique();
      },
      error: () => {
        this.erreur = 'Patient introuvable.';
        this.loading = false;
      }
    });
  }

  chargerHistorique(): void {
    this.consultationService.getHistorique(this.patientId).subscribe({
      next: (data: Consultation[]) => {
        this.consultations = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  // ✅ Initiales de l'avatar
  initiales(): string {
    if (!this.patient) return '';
    const p = this.patient.user.prenom?.charAt(0) ?? '';
    const n = this.patient.user.nom?.charAt(0) ?? '';
    return (p + n).toUpperCase();
  }

  // ✅ Libellé de la maladie
  maladieLabel(): string {
    if (!this.patient) return '';
    return MALADIE_LABELS[this.patient.maladieChronique] ?? this.patient.maladieChronique;
  }

  // ✅ Nouvelle consultation
  nouvelleConsultation(): void {
    this.router.navigate(['/medecin/patients', this.patientId, 'consultation']);
  }

  // ✅ Ouvrir/fermer le détail d'une consultation
  ouvrirDetail(c: Consultation): void {
    if (this.consultationSelectionnee?.id === c.id) {
      this.consultationSelectionnee = null;
    } else {
      this.consultationSelectionnee = c;
    }
  }

  // ✅ Formater l'heure
  formatHeure(date: string): string {
    if (!date) return '';
    return new Date(date).toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  // ✅ Formater la date
  formatDate(date?: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR', {
      day: 'numeric', month: 'long', year: 'numeric'
    });
  }

  // ✅ Classe CSS du badge de type
  typeBadgeClass(type: string): string {
    switch (type) {
      case 'PREMIERE': return 'badge-info';
      case 'SUIVI': return 'badge-ok';
      case 'URGENCE': return 'badge-danger';
      default: return 'badge-ok';
    }
  }

  // ✅ Libellé du type
  typeLabel(type: string): string {
    const labels: { [key: string]: string } = {
      'PREMIERE': 'Première',
      'SUIVI': 'Suivi',
      'URGENCE': 'Urgence'
    };
    return labels[type] || type;
  }

  // ✅ Classe CSS du niveau d'alerte
  niveauClass(niveau?: string): string {
    if (niveau === 'CRITIQUE') return 'badge-danger';
    if (niveau === 'ATTENTION') return 'badge-warn';
    return 'badge-ok';
  }

  // ✅ Libellé de la mesure
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

  retour(): void {
    this.router.navigate(['/medecin/patients']);
  }
}