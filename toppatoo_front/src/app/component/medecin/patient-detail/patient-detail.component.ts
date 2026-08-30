import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientService } from '../../../core/services/patient.service';
import { ConsultationService } from '../../../core/services/consultation.service';
import { Patient, MALADIE_LABELS } from '../../../core/models/patient.model';
import { Consultation } from '../../../core/models/consultation.model';

@Component({
  selector: 'app-patient-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './patient-detail.component.html',
  styleUrls: ['./patient-detail.component.scss'],
})
export class PatientDetailComponent implements OnInit {

  patientId = '';
  patient: Patient | null = null;
  consultations: Consultation[] = [];
  consultationSelectionnee: Consultation | null = null;
  loading = true;
  erreur = '';
  maladieLabels = MALADIE_LABELS;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService,
    private consultationService: ConsultationService,
  ) {}

  ngOnInit(): void {
    this.patientId = this.route.snapshot.paramMap.get('id') ?? '';
    this.chargerDonnees();
  }

  chargerDonnees(): void {
    this.patientService.getPatientById(this.patientId).subscribe({
      next: (p) => {
        this.patient = p;
        this.chargerConsultations();
      },
      error: () => { this.erreur = 'Patient introuvable.'; this.loading = false; },
    });
  }

  chargerConsultations(): void {
    this.consultationService.getHistorique(this.patientId).subscribe({
      next: (data) => {
        this.consultations = data;
        this.loading = false;
      },
      error: () => { this.erreur = 'Erreur lors du chargement.'; this.loading = false; },
    });
  }

  ouvrirDetail(c: Consultation): void {
    this.consultationSelectionnee = this.consultationSelectionnee?.id === c.id ? null : c;
  }

  nouvelleConsultation(): void {
    this.router.navigate(['/medecin/patients', this.patientId, 'consultation']);
  }

  retour(): void {
  this.router.navigate(['/medecin/patients']);
}

  calculerAge(dateNaissance: string): number {
    const n = new Date(dateNaissance);
    const a = new Date();
    let age = a.getFullYear() - n.getFullYear();
    const m = a.getMonth() - n.getMonth();
    if (m < 0 || (m === 0 && a.getDate() < n.getDate())) age--;
    return age;
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      day: 'numeric', month: 'long', year: 'numeric'
    });
  }

  formatHeure(date: string): string {
    return new Date(date).toLocaleTimeString('fr-FR', {
      hour: '2-digit', minute: '2-digit'
    });
  }

  typeLabel(type: string): string {
    const labels: any = {
      PREMIERE: 'Première consultation',
      SUIVI: 'Consultation de suivi',
      URGENCE: 'Urgence',
    };
    return labels[type] ?? type;
  }

  typeBadgeClass(type: string): string {
    if (type === 'URGENCE') return 'badge-danger';
    if (type === 'PREMIERE') return 'badge-info';
    return 'badge-ok';
  }

  niveauClass(niveau?: string): string {
    if (niveau === 'CRITIQUE') return 'badge-danger';
    if (niveau === 'ATTENTION') return 'badge-warn';
    return 'badge-ok';
  }

  initiales(): string {
  if (!this.patient?.user) return '?';
  const p = this.patient.user.prenom?.charAt(0) ?? '';
  const n = this.patient.user.nom?.charAt(0) ?? '';
  return (p + n).toUpperCase() || '?';
}
}