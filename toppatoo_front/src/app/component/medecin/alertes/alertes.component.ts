import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Patient } from '../../../core/models/patient.model';
import { Mesure } from '../../../core/models/consultation.model';
import { PatientService } from '../../../core/services/patient.service';
import { MesureService } from '../../../core/services/mesure.service';

interface AlertePatient {
  patient: Patient;
  mesures: Mesure[];
}

@Component({
  selector: 'app-alertes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alertes.component.html',
  styleUrls: ['./alertes.component.scss']
})
export class AlertesComponent implements OnInit {

  patients: Patient[] = [];
  alertes: AlertePatient[] = [];
  loading = true;
  erreur = '';

  constructor(
    private patientService: PatientService,
    private mesureService: MesureService
  ) {}

  ngOnInit(): void {
    this.chargerAlertes();
  }

  chargerAlertes(): void {
    this.loading = true;
    this.erreur = '';
    
    // Récupérer tous les patients
    this.patientService.getAllPatients().subscribe({
      next: (patients: Patient[]) => {
        this.patients = patients;
        this.chargerMesuresPourPatients(patients);
      },
      error: (err) => {
        console.error('❌ Erreur chargement patients:', err);
        this.erreur = 'Erreur lors du chargement des patients.';
        this.loading = false;
      }
    });
  }

  private chargerMesuresPourPatients(patients: Patient[]): void {
    if (patients.length === 0) {
      this.loading = false;
      return;
    }

    let compteur = 0;
    const total = patients.length;

    patients.forEach((patient: Patient) => {
      // ✅ Utiliser findByPatient
      this.mesureService.findByPatient(patient.id).subscribe({
        next: (mesures: Mesure[]) => {
          // ✅ Filtrer les mesures en alerte
          const mesuresAlerte = mesures.filter((m: Mesure) => 
            m.niveauAlerte === 'CRITIQUE' || m.niveauAlerte === 'ATTENTION'
          );

          if (mesuresAlerte.length > 0) {
            this.alertes.push({
              patient: patient,
              mesures: mesuresAlerte
            });
          }

          compteur++;
          if (compteur === total) {
            // Trier par priorité (CRITIQUE en premier)
            this.alertes.sort((a, b) => {
              const aCritique = a.mesures.some(m => m.niveauAlerte === 'CRITIQUE');
              const bCritique = b.mesures.some(m => m.niveauAlerte === 'CRITIQUE');
              if (aCritique && !bCritique) return -1;
              if (!aCritique && bCritique) return 1;
              return 0;
            });
            this.loading = false;
          }
        },
        error: (err) => {
          console.error(`❌ Erreur mesures pour patient ${patient.id}:`, err);
          compteur++;
          if (compteur === total) {
            this.loading = false;
          }
        }
      });
    });
  }

  // ✅ Compter les alertes critiques
  getNbCritiques(): number {
    return this.alertes.filter(a => 
      a.mesures.some(m => m.niveauAlerte === 'CRITIQUE')
    ).length;
  }

  // ✅ Compter les alertes attention
  getNbAttentions(): number {
    return this.alertes.filter(a => 
      a.mesures.every(m => m.niveauAlerte === 'ATTENTION') &&
      a.mesures.length > 0
    ).length;
  }

  // ✅ Obtenir la classe CSS selon le niveau
  getNiveauClass(niveau?: string): string {
    if (niveau === 'CRITIQUE') return 'badge-danger';
    if (niveau === 'ATTENTION') return 'badge-warn';
    return 'badge-ok';
  }

  // ✅ Obtenir le libellé du type de mesure
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

  // ✅ Formater la valeur d'une mesure
  formatValeur(mesure: Mesure): string {
    if (mesure.valeur2) {
      return `${mesure.valeur}/${mesure.valeur2} ${mesure.unite}`;
    }
    return `${mesure.valeur} ${mesure.unite}`;
  }
}