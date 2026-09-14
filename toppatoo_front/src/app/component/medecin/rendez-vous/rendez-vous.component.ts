import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RendezVous } from '../../../core/models/rendez-vous.model';
import { RendezVousService } from '../../../core/services/rendez-vous.service';

@Component({
  selector: 'app-rendez-vous',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rendez-vous.component.html',
  styleUrls: ['./rendez-vous.component.scss']
})
export class RendezVousComponent implements OnInit {

  // ─── Données ───────────────────────────────────────────────────
  medecinId: number = 0;
  rdvs: RendezVous[] = [];         // ✅ Renommé (était rendezVous)
  loading = true;
  erreur = '';                     // ✅ AJOUTÉ

  // ─── Constructeur ──────────────────────────────────────────────
  constructor(
    private rendezVousService: RendezVousService,
    private router: Router
  ) {}

  // ─── Init ──────────────────────────────────────────────────────
  ngOnInit(): void {
    const id = sessionStorage.getItem('userId');
    this.medecinId = id ? Number(id) : 0;
    this.chargerRendezVous();
  }

  chargerRendezVous(): void {
    this.loading = true;
    this.erreur = '';

    this.rendezVousService.getByMedecin(this.medecinId).subscribe({
      next: (data: RendezVous[]) => {
        this.rdvs = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Erreur chargement RDV:', err);
        this.erreur = 'Erreur lors du chargement des rendez-vous.';
        this.loading = false;
      }
    });
  }

  // ─── Filtres ──────────────────────────────────────────────────
  
  // ✅ RDV à venir (date >= aujourd'hui)
  rdvsAvenir(): RendezVous[] {
    const now = new Date();
    return this.rdvs
      .filter(r => new Date(r.dateHeure) >= now)
      .sort((a, b) => new Date(a.dateHeure).getTime() - new Date(b.dateHeure).getTime());
  }

  // ✅ RDV passés (date < aujourd'hui)
  rdvsPasses(): RendezVous[] {
    const now = new Date();
    return this.rdvs
      .filter(r => new Date(r.dateHeure) < now)
      .sort((a, b) => new Date(b.dateHeure).getTime() - new Date(a.dateHeure).getTime());
  }

  // ─── Formatage ────────────────────────────────────────────────

  // ✅ Format date : "15 janvier 2026"
  formatDate(date: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  }

  // ✅ Format heure : "14:30"
  formatHeure(date: string): string {
    if (!date) return '';
    return new Date(date).toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  // ✅ Jours restants avant le RDV
  joursRestants(date: string): number {
    if (!date) return 0;
    const rdv = new Date(date);
    const now = new Date();
    const diff = rdv.getTime() - now.getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }

  // ─── Styles ───────────────────────────────────────────────────

  // ✅ Classe CSS du badge selon le statut
  statutClass(statut?: string): string {
    switch (statut) {
      case 'CONFIRME':  return 'badge-ok';
      case 'PLANIFIE':  return 'badge-info';
      case 'ANNULE':    return 'badge-danger';
      case 'TERMINE':   return 'badge-muted';
      default:          return 'badge-info';
    }
  }

  // ✅ Libellé du statut
  statutLabel(statut?: string): string {
    const labels: { [key: string]: string } = {
      'CONFIRME': 'Confirmé',
      'PLANIFIE': 'Planifié',
      'ANNULE':   'Annulé',
      'TERMINE':  'Terminé'
    };
    return labels[statut || ''] || statut || '';
  }

  // ─── Navigation ───────────────────────────────────────────────

  // ✅ Ouvrir le dossier d'un patient
  ouvrirDossier(patientId: number): void {
    this.router.navigate(['/medecin/patients', patientId, 'detail']);
  }
}