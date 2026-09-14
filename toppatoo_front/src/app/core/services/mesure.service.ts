import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Mesure } from '../models/consultation.model';

@Injectable({ providedIn: 'root' })
export class MesureService {
  private readonly apiUrl = `${environment.apiUrl}/mesures`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('accessToken');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // ✅ Récupérer les mesures d'un patient
  findByPatient(patientId: string | number): Observable<Mesure[]> {
    return this.http.get<Mesure[]>(
      `${this.apiUrl}/patient/${patientId}`,
      { headers: this.getHeaders() }
    );
  }

  // ✅ Récupérer les mesures par type
  findByPatientAndType(patientId: string | number, type: string): Observable<Mesure[]> {
    return this.http.get<Mesure[]>(
      `${this.apiUrl}/patient/${patientId}?type=${type}`,
      { headers: this.getHeaders() }
    );
  }

  // ✅ Récupérer les alertes d'un patient
  findAlertes(patientId: string | number): Observable<Mesure[]> {
    return this.http.get<Mesure[]>(
      `${this.apiUrl}/patient/${patientId}/alertes`,
      { headers: this.getHeaders() }
    );
  }

  // ✅ Récupérer les dernières mesures
  findDernieres(patientId: string | number, limit: number = 10): Observable<Mesure[]> {
    return this.http.get<Mesure[]>(
      `${this.apiUrl}/patient/${patientId}/dernieres?limit=${limit}`,
      { headers: this.getHeaders() }
    );
  }

  // ✅ Ajouter une mesure à une consultation
  ajouterMesure(consultationId: string | number, mesure: any): Observable<Mesure> {
    return this.http.post<Mesure>(
      `${environment.apiUrl}/consultations/${consultationId}/mesures`,
      mesure,
      { headers: this.getHeaders() }
    );
  }

  // ✅ Récupérer les mesures d'une consultation
  getMesuresByConsultation(consultationId: string | number): Observable<Mesure[]> {
    return this.http.get<Mesure[]>(
      `${environment.apiUrl}/consultations/${consultationId}/mesures`,
      { headers: this.getHeaders() }
    );
  }
}