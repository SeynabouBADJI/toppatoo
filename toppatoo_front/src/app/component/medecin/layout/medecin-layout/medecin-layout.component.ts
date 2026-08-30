import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-medecin-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './medecin-layout.component.html',
  styleUrls: ['./medecin-layout.component.scss'],
})
export class MedecinLayoutComponent implements OnInit {

  nom = sessionStorage.getItem('nom') ?? '';
  prenom = sessionStorage.getItem('prenom') ?? '';

  initiales(): string {
    return ((this.prenom?.charAt(0) ?? '') + (this.nom?.charAt(0) ?? '')).toUpperCase();
  }

  constructor(private router: Router) {}

  ngOnInit(): void {
    if (!sessionStorage.getItem('userId')) {
      this.router.navigate(['/login']);
    }
  }

  deconnexion(): void {
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }
}