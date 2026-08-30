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

  userId = sessionStorage.getItem('userId') ?? '';

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
        this.loading = false;
        this.chargerSuite(patient.id);
      },
      error: () => {
        this.erreur = 'Impossible de charger votre profil.';
        this.loading = false;
      },
    });
  }

  chargerSuite(patientId: string): void {
    this.consultationService.getDerniere(patientId).subscribe({
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

  // ─── Initiales ──────────────────────────────────────────────────
  initiales(): string {
    if (!this.patient) return '';
    const p = this.patient.user.prenom?.charAt(0) ?? '';
    const n = this.patient.user.nom?.charAt(0) ?? '';
    return (p + n).toUpperCase();
  }

  // ─── Libellé de la maladie ────────────────────────────────────
  maladieLabel(): string {
    if (!this.patient) return '';
    return MALADIE_LABELS[this.patient.maladieChronique] ?? this.patient.maladieChronique;
  }

  // ─── Date consultation ────────────────────────────────────────
  formatConsultDate(date?: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR', {
      day: 'numeric', month: 'long', year: 'numeric'
    });
  }

  // ─── Date générique ───────────────────────────────────────────
  formatDate(date?: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR', {
      day: 'numeric', month: 'long', year: 'numeric'
    });
  }

  // ─── Jours avant RDV ─────────────────────────────────────────
  joursAvantRdv(): number {
    if (!this.prochainRdv) return 0;
    const rdv = new Date(this.prochainRdv.dateHeure);
    const diff = rdv.getTime() - new Date().getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }

  // ─── Mesures ──────────────────────────────────────────────────
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

  // ─── Dates ────────────────────────────────────────────────────
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

  // ─── Actions ──────────────────────────────────────────────────
  
  /**
   * Marquer une notification comme lue
   */
  marquerLu(id: string): void {
    this.notificationService.marquerLue(id).subscribe({
      next: () => {
        const notif = this.notifications.find(n => n.id === id);
        if (notif) {
          notif.lue = true;
        }
      },
      error: (err) => {
        console.error('Erreur lors du marquage de la notification:', err);
      }
    });
  }

  /**
   * Marquer toutes les notifications comme lues
   */
  marquerToutLu(): void {
    if (!this.patient || this.notifications.length === 0) return;
    
    const nonLues = this.notifications.filter(n => !n.lue);
    if (nonLues.length === 0) return;
    
    nonLues.forEach(notif => {
      this.notificationService.marquerLue(notif.id).subscribe({
        next: () => {
          notif.lue = true;
        },
        error: (err) => {
          console.error('Erreur lors du marquage:', err);
        }
      });
    });
  }

  /**
   * Supprimer une notification (suppression locale)
   */
  supprimerNotification(id: string): void {
    this.notifications = this.notifications.filter(n => n.id !== id);
  }

  /**
   * Voir l'historique d'une mesure
   */
  voirHistorique(type: string): void {
    this.router.navigate(['/mesures', type]);
  }

  /**
   * Voir les détails d'un médicament
   */
  voirDetailMedicament(medicament: any): void {
    this.router.navigate(['/medicaments', medicament.id]);
  }

  /**
   * Demander un rendez-vous
   */
  demanderRdv(): void {
    this.router.navigate(['/rendez-vous/nouveau']);
  }

  /**
   * Scroll vers les notifications
   */
  scrollToNotifications(): void {
    const element = document.getElementById('notifications-section');
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  /**
   * Recharger la page en cas d'erreur
   */
  rechargerPage(): void {
    this.erreur = '';
    this.loading = true;
    this.chargerDonnees();
  }

  /**
   * Exécuter une action depuis une notification
   */
  executerAction(id: string, action: any): void {
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
      default:
        console.log('Action non reconnue:', action);
    }
    
    // Marquer la notification comme lue après action
    this.marquerLu(id);
  }

  /**
   * Retourner à la page de connexion
   */
  retourConnexion(): void {
    // Option avec confirmation pour éviter les clics accidentels
    if (confirm('Voulez-vous vraiment retourner à la page de connexion ?')) {
      // Nettoyer la session (optionnel)
      // sessionStorage.clear();
      this.router.navigate(['/login']);
    }
  }

  // ─── Déconnexion ──────────────────────────────────────────────
  deconnexion(): void {
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }
}