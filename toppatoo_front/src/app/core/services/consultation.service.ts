import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Consultation, Mesure, Ordonnance } from '../models/consultation.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ConsultationService {
  private readonly baseUrl = `${environment.apiUrl}/consultations`;

  constructor(private http: HttpClient) {}

  creer(consultation: Partial<Consultation>): Observable<Consultation> {
    return this.http.post<Consultation>(this.baseUrl, consultation);
  }

  getById(id: string): Observable<Consultation> {
    return this.http.get<Consultation>(`${this.baseUrl}/${id}`);
  }

  getHistorique(patientId: string): Observable<Consultation[]> {
    return this.http.get<Consultation[]>(`${this.baseUrl}/patient/${patientId}`);
  }

  getDerniere(patientId: string): Observable<Consultation> {
    return this.http.get<Consultation>(`${this.baseUrl}/patient/${patientId}/derniere`);
  }

  ajouterMesure(consultationId: string, mesure: Mesure): Observable<Mesure> {
    return this.http.post<Mesure>(`${this.baseUrl}/${consultationId}/mesures`, mesure);
  }

  ajouterOrdonnance(consultationId: string, ordonnance: Ordonnance): Observable<Ordonnance> {
    return this.http.post<Ordonnance>(`${this.baseUrl}/${consultationId}/ordonnance`, ordonnance);
  }
}