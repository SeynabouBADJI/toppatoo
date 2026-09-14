import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { PatientService } from '../../../core/services/patient.service';
import { ConsultationService } from '../../../core/services/consultation.service';
import { RendezVousService } from '../../../core/services/rendez-vous.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Patient, MALADIE_LABELS } from '../../../core/models/patient.model';
import { Consultation, Mesure } from '../../../core/models/consultation.model';
import { RendezVous } from '../../../core/models/rendez-vous.model';
import { Notification } from '../../../core/models/notification.model';

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './patient-dashboard.component.html',
  styleUrls: ['./patient-dashboard.component.scss'],
})
export class PatientDashboardComponent implements OnInit {

  // ✅ Utiliser number
  userId: number = 0;
  patientId: number = 0;

  patient: Patient | null = null;
  derniereConsultation: Consultation | null = null;
  prochainRdv: RendezVous | null = null;
  notifications: Notification[] = [];
  mesures: Mesure[] = [];

  loading = true;
  erreur = '';

  constructor(
    private patientService: PatientService,
    private consultationService: ConsultationService,
    private rendezVousService: RendezVousService,
    private notificationService: NotificationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    const id = sessionStorage.getItem('userId');
    this.userId = id ? Number(id) : 0;

    if (!this.userId) {
      this.router.navigate(['/login']);
      return;
    }
    this.chargerDonnees();
  }

  chargerDonnees(): void {
    this.patientService.getPatientById(this.userId).subscribe({
      next: (patient) => {
        this.patient = patient;
        this.patientId = patient.id;  // ✅ number
        this.loading = false;
        this.chargerSuite(patient.id);  // ✅ number
      },
      error: () => {
        this.erreur = 'Impossible de charger votre profil.';
        this.loading = false;
      },
    });
  }

  chargerSuite(patientId: number): void {  // ✅ number
    this.consultationService.getDerniere(patientId).subscribe({  // ✅ number
      next: (c) => {
        this.derniereConsultation = c;
        this.mesures = c?.mesures ?? [];
      },
      error: () => {},
    });

    this.rendezVousService.getProchain(patientId).subscribe({
      next: (rdv) => { this.prochainRdv = rdv; },
      error: () => {},
    });

    this.notificationService.getNonLues(patientId).subscribe({
      next: (notifs) => { this.notifications = notifs; },
      error: () => {},
    });
  }

  // ─── INITIALES ──────────────────────────────────────────────────
  initiales(): string {
    if (!this.patient) return '';
    const p = this.patient.user.prenom?.charAt(0) ?? '';
    const n = this.patient.user.nom?.charAt(0) ?? '';
    return (p + n).toUpperCase();
  }

  // ─── LIBELLÉ MALADIE ──────────────────────────────────────────
  maladieLabel(): string {
    if (!this.patient) return '';
    return MALADIE_LABELS[this.patient.maladieChronique] ?? this.patient.maladieChronique;
  }

  // ─── DATES ────────────────────────────────────────────────────
  formatConsultDate(date?: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR', {
      day: 'numeric', month: 'long', year: 'numeric'
    });
  }

  formatDate(date?: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR', {
      day: 'numeric', month: 'long', year: 'numeric'
    });
  }

  joursAvantRdv(): number {
    if (!this.prochainRdv) return 0;
    const rdv = new Date(this.prochainRdv.dateHeure);
    const diff = rdv.getTime() - new Date().getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }

  // ─── MESURES ──────────────────────────────────────────────────
  getMesure(type: string): Mesure | undefined {
    return this.mesures.find(m => m.type === type);
  }

  getNiveauClass(niveau?: string): string {
    if (niveau === 'CRITIQUE') return 'badge-danger';
    if (niveau === 'ATTENTION') return 'badge-warn';
    return 'badge-ok';
  }

  getNiveauLabel(niveau?: string): string {
    if (niveau === 'CRITIQUE') return 'Élevée';
    if (niveau === 'ATTENTION') return 'À surveiller';
    return 'Normal';
  }

  getValeurClass(niveau?: string): string {
    if (niveau === 'CRITIQUE') return 'valeur-danger';
    if (niveau === 'ATTENTION') return 'valeur-warn';
    return 'valeur-ok';
  }

  nbAlertes(): number {
    return this.mesures.filter(m =>
      m.niveauAlerte === 'CRITIQUE' || m.niveauAlerte === 'ATTENTION'
    ).length;
  }

  // ─── DATES ────────────────────────────────────────────────────
  formatDateRdv(): string {
    if (!this.prochainRdv) return '';
    const date = new Date(this.prochainRdv.dateHeure);
    return date.toLocaleDateString('fr-FR', {
      day: 'numeric', month: 'long', year: 'numeric'
    }) + ' à ' + date.toLocaleTimeString('fr-FR', {
      hour: '2-digit', minute: '2-digit'
    });
  }

  formatDateNotif(date: string): string {
    const d = new Date(date);
    const diff = Math.floor((new Date().getTime() - d.getTime()) / (1000 * 60 * 60 * 24));
    if (diff === 0) return "Aujourd'hui";
    if (diff === 1) return 'Hier';
    return `Il y a ${diff} jours`;
  }

  aujourdhui(): string {
    return new Date().toLocaleDateString('fr-FR', {
      weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
    });
  }

  // ─── ACTIONS ──────────────────────────────────────────────────
  
  

  marquerToutLu(): void {
    if (!this.patient || this.notifications.length === 0) return;
    
    const nonLues = this.notifications.filter(n => !n.lue);
    if (nonLues.length === 0) return;
    
    nonLues.forEach(notif => {
      if (notif.id) {  // ✅ Vérifier que id existe
        this.notificationService.marquerLue(notif.id).subscribe({
          next: () => {
            notif.lue = true;
          },
          error: (err) => {
            console.error('Erreur:', err);
          }
        });
      }
    });
  }
  

  voirHistorique(type: string): void {
    this.router.navigate(['/mesures', type]);
  }

  voirDetailMedicament(medicament: any): void {
    this.router.navigate(['/medicaments', medicament.id]);
  }

  demanderRdv(): void {
    this.router.navigate(['/rendez-vous/nouveau']);
  }

  scrollToNotifications(): void {
    const element = document.getElementById('notifications-section');
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  rechargerPage(): void {
    this.erreur = '';
    this.loading = true;
    this.chargerDonnees();
  }

  // ✅ Modifier marquerLu pour accepter number | undefined
marquerLu(id: number | undefined): void {
  if (id === undefined) return;
  
  this.notificationService.marquerLue(id).subscribe({
    next: () => {
      const notif = this.notifications.find(n => n.id === id);
      if (notif) notif.lue = true;
    },
    error: (err) => console.error('Erreur:', err)
  });
}

// ✅ Modifier executerAction pour accepter number | undefined
executerAction(id: number | undefined, action: any): void {
  if (id === undefined) return;
  
  switch(action.type) {
    case 'VOIR_RDV':
      this.router.navigate(['/rendez-vous']);
      break;
    case 'PRENDRE_RDV':
      this.demanderRdv();
      break;
    case 'VOIR_MESURE':
      this.router.navigate(['/mesures']);
      break;
  }
  this.marquerLu(id);
}

// ✅ Modifier supprimerNotification pour accepter number | undefined
supprimerNotification(id: number | undefined): void {
  if (id === undefined) return;
  this.notifications = this.notifications.filter(n => n.id !== id);
}

  retourConnexion(): void {
    if (confirm('Voulez-vous vraiment retourner à la page de connexion ?')) {
      this.router.navigate(['/login']);
    }
  }

  deconnexion(): void {
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }
}