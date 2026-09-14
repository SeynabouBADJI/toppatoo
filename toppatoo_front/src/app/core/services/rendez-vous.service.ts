import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RendezVous } from '../models/rendez-vous.model';

@Injectable({ providedIn: 'root' })
export class RendezVousService {
  private readonly apiUrl = `${environment.apiUrl}/rendez-vous`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('accessToken');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // ✅ AJOUTER CETTE MÉTHODE
  getProchain(patientId: number): Observable<RendezVous> {
    return this.http.get<RendezVous>(
      `${this.apiUrl}/patient/${patientId}/prochain`,
      { headers: this.getHeaders() }
    );
  }

  planifier(rdv: Partial<RendezVous>): Observable<RendezVous> {
    return this.http.post<RendezVous>(
      this.apiUrl,
      rdv,
      { headers: this.getHeaders() }
    );
  }

  getAll(statut?: string, medecinId?: number): Observable<RendezVous[]> {
    let params = '';
    if (statut) params += `?statut=${statut}`;
    if (medecinId) params += `${params ? '&' : '?'}medecinId=${medecinId}`;
    
    return this.http.get<RendezVous[]>(
      `${this.apiUrl}${params}`,
      { headers: this.getHeaders() }
    );
  }

  // ✅ Ajouter cette méthode dans rendez-vous.service.ts
  getByMedecin(medecinId: number): Observable<RendezVous[]> {
    return this.http.get<RendezVous[]>(
      `${this.apiUrl}/medecin/${medecinId}`,
      { headers: this.getHeaders() }
    );
  }

  getById(id: number): Observable<RendezVous> {
    return this.http.get<RendezVous>(
      `${this.apiUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }

  confirmer(id: number): Observable<RendezVous> {
    return this.http.put<RendezVous>(
      `${this.apiUrl}/${id}/confirmer`,
      {},
      { headers: this.getHeaders() }
    );
  }

  annuler(id: number): Observable<RendezVous> {
    return this.http.put<RendezVous>(
      `${this.apiUrl}/${id}/annuler`,
      {},
      { headers: this.getHeaders() }
    );
  }

  update(id: number, rdv: Partial<RendezVous>): Observable<RendezVous> {
    return this.http.put<RendezVous>(
      `${this.apiUrl}/${id}`,
      rdv,
      { headers: this.getHeaders() }
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }
}