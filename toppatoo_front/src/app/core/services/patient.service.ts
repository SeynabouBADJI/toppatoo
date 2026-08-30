import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Patient } from '../models/patient.model';
import { environment } from '../../../environments/environment';

export interface PatientCreeResponse {
  patientId: string;
  email: string;
  motDePasseTemporaire: string;
  message: string;
}

@Injectable({ providedIn: 'root' })
export class PatientService {
  private readonly baseUrl = `${environment.apiUrl}/patients`;

  constructor(private http: HttpClient) {}

  getPatientsByMedecin(medecinId: string): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.baseUrl}/medecin/${medecinId}`);
  }

  getPatientById(id: string): Observable<Patient> {
    return this.http.get<Patient>(`${this.baseUrl}/${id}`);
  }

  getPatientByUserId(userId: string): Observable<Patient> {
    return this.http.get<Patient>(`${this.baseUrl}/user/${userId}`);
  }

  updateSeuils(id: string, seuils: any): Observable<Patient> {
    return this.http.put<Patient>(`${this.baseUrl}/${id}/seuils`, seuils);
  }

  creerPatient(payload: any): Observable<PatientCreeResponse> {
    return this.http.post<PatientCreeResponse>(this.baseUrl, payload);
  }
}