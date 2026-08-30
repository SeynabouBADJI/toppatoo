import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RendezVousService } from '../../../core/services/rendez-vous.service';
import { RendezVous } from '../../../core/models/rendez-vous.model';

@Component({
  selector: 'app-rendez-vous',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rendez-vous.component.html',
  styleUrls: ['./rendez-vous.component.scss'],
})
export class RendezVousComponent implements OnInit {

  medecinId = sessionStorage.getItem('userId') ?? '';
  rdvs: RendezVous[] = [];
  loading = true;
  erreur = '';

  // RDV groupés par patient
  rdvParPatient: Map<string, RendezVous[]> = new Map();

  constructor(
    private rendezVousService: RendezVousService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.chargerRdvs();
  }

  chargerRdvs(): void {
    this.rendezVousService.getByMedecin(this.medecinId).subscribe({
      next: (data) => {
        this.rdvs = data;
        this.loading = false;
      },
      error: () => {
        this.erreur = 'Impossible de charger les rendez-vous.';
        this.loading = false;
      },
    });
  }

  ouvrirDossier(patientId: string): void {
    this.router.navigate(['/medecin/patients', patientId, 'detail']);
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
    });
  }

  formatHeure(date: string): string {
    return new Date(date).toLocaleTimeString('fr-FR', {
      hour: '2-digit', minute: '2-digit'
    });
  }

  joursRestants(date: string): number {
    const rdv = new Date(date);
    const now = new Date();
    return Math.ceil((rdv.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
  }

  statutClass(statut?: string): string {
    if (statut === 'ANNULE')  return 'badge-danger';
    if (statut === 'TERMINE') return 'badge-muted';
    return 'badge-ok';
  }

  rdvsAvenir(): RendezVous[] {
    return this.rdvs.filter(r => new Date(r.dateHeure) >= new Date() && r.statut !== 'ANNULE');
  }

  rdvsPasses(): RendezVous[] {
    return this.rdvs.filter(r => new Date(r.dateHeure) < new Date() || r.statut === 'TERMINE');
  }
}