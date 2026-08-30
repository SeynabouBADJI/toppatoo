import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mesure } from '../models/consultation.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class MesureService {
  private readonly baseUrl = `${environment.apiUrl}/mesures`;

  constructor(private http: HttpClient) {}

  findByPatient(patientId: string): Observable<Mesure[]> {
    return this.http.get<Mesure[]>(`${this.baseUrl}/patient/${patientId}`);
  }

  findByType(patientId: string, type: string): Observable<Mesure[]> {
    return this.http.get<Mesure[]>(`${this.baseUrl}/patient/${patientId}/type/${type}`);
  }

  derniereMesure(patientId: string, type: string): Observable<Mesure> {
    return this.http.get<Mesure>(`${this.baseUrl}/patient/${patientId}/type/${type}/derniere`);
  }
}