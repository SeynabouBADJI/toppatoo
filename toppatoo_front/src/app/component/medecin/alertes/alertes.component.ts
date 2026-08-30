import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MesureService } from '../../../core/services/mesure.service';
import { PatientService } from '../../../core/services/patient.service';
import { Patient } from '../../../core/models/patient.model';

export interface AlertePatient {
  patient: Patient;
  type: string;
  valeur: string;
  niveau: string;
  date: string;
}

@Component({
  selector: 'app-alertes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alertes.component.html',
  styleUrls: ['./alertes.component.scss'],
})
export class AlertesComponent implements OnInit {

  medecinId = sessionStorage.getItem('userId') ?? '';
  patients: Patient[] = [];
  alertes: AlertePatient[] = [];
  loading = true;
  erreur = '';

  constructor(
    private patientService: PatientService,
    private mesureService: MesureService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.chargerAlertes();
  }

  chargerAlertes(): void {
    this.patientService.getPatientsByMedecin(this.medecinId).subscribe({
      next: (patients) => {
        this.patients = patients;
        this.chargerMesuresCritiques(patients);
      },
      error: () => { this.erreur = 'Erreur de chargement.'; this.loading = false; },
    });
  }

  chargerMesuresCritiques(patients: Patient[]): void {
    let compteur = 0;
    if (patients.length === 0) { this.loading = false; return; }

    patients.forEach(patient => {
      this.mesureService.findByPatient(patient.id).subscribe({
        next: (mesures) => {
          mesures
            .filter(m => m.niveauAlerte === 'CRITIQUE' || m.niveauAlerte === 'ATTENTION')
            .forEach(m => {
              this.alertes.push({
                patient,
                type: m.type,
                valeur: `${m.valeur}${m.valeur2 ? '/' + m.valeur2 : ''} ${m.unite}`,
                niveau: m.niveauAlerte ?? 'ATTENTION',
                date: m.dateMesure ?? '',
              });
            });
          compteur++;
          if (compteur === patients.length) {
            // Trier par niveau (CRITIQUE d'abord) puis par date
            this.alertes.sort((a, b) => {
              if (a.niveau === 'CRITIQUE' && b.niveau !== 'CRITIQUE') return -1;
              if (b.niveau === 'CRITIQUE' && a.niveau !== 'CRITIQUE') return 1;
              return new Date(b.date).getTime() - new Date(a.date).getTime();
            });
            this.loading = false;
          }
        },
        error: () => {
          compteur++;
          if (compteur === patients.length) this.loading = false;
        },
      });
    });
  }

  ouvrirDossier(patientId: string): void {
    this.router.navigate(['/medecin/patients', patientId, 'detail']);
  }

  formatDate(date: string): string {
    if (!date) return '—';
    return new Date(date).toLocaleDateString('fr-FR', {
      day: 'numeric', month: 'long', hour: '2-digit', minute: '2-digit'
    });
  }

  niveauClass(niveau: string): string {
    return niveau === 'CRITIQUE' ? 'badge-danger' : 'badge-warn';
  }

  niveauLabel(niveau: string): string {
    return niveau === 'CRITIQUE' ? '🔴 Critique' : '🟠 Attention';
  }

  alertesCritiques(): AlertePatient[] {
    return this.alertes.filter(a => a.niveau === 'CRITIQUE');
  }

  alertesAttention(): AlertePatient[] {
    return this.alertes.filter(a => a.niveau === 'ATTENTION');
  }
}