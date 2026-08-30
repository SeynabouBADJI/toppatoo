import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RendezVous } from '../models/rendez-vous.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RendezVousService {
  private readonly baseUrl = `${environment.apiUrl}/rendez-vous`;

  constructor(private http: HttpClient) {}

  planifier(rdv: RendezVous): Observable<RendezVous> {
    return this.http.post<RendezVous>(this.baseUrl, rdv);
  }

  getByPatient(patientId: string): Observable<RendezVous[]> {
    return this.http.get<RendezVous[]>(`${this.baseUrl}/patient/${patientId}`);
  }

  getByMedecin(medecinId: string): Observable<RendezVous[]> {
    return this.http.get<RendezVous[]>(`${this.baseUrl}/medecin/${medecinId}`);
  }

  getProchain(patientId: string): Observable<RendezVous> {
    return this.http.get<RendezVous>(`${this.baseUrl}/patient/${patientId}/prochain`);
  }
}