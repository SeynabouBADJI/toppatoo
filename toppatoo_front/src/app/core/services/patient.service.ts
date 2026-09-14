import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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

  // ✅ Headers avec token JWT
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('accessToken');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // ─── GET ───────────────────────────────────────────────────────

  getAllPatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(
      this.baseUrl,
      { headers: this.getHeaders() }
    );
  }

  getPatientById(id: string | number): Observable<Patient> {
    return this.http.get<Patient>(
      `${this.baseUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }

  getPatientByUserId(userId: string | number): Observable<Patient> {
    return this.http.get<Patient>(
      `${this.baseUrl}/user/${userId}`,
      { headers: this.getHeaders() }
    );
  }

  getPatientsByMedecin(medecinId: string | number): Observable<Patient[]> {
    return this.http.get<Patient[]>(
      `${this.baseUrl}/medecin/${medecinId}`,
      { headers: this.getHeaders() }
    );
  }

  // ─── POST ──────────────────────────────────────────────────────

  creerPatient(payload: any): Observable<PatientCreeResponse> {
    return this.http.post<PatientCreeResponse>(
      this.baseUrl,
      payload,
      { headers: this.getHeaders() }
    );
  }

  // ─── PUT ───────────────────────────────────────────────────────

  updatePatient(id: string | number, patient: Partial<Patient>): Observable<Patient> {
    return this.http.put<Patient>(
      `${this.baseUrl}/${id}`,
      patient,
      { headers: this.getHeaders() }
    );
  }

  updateProfil(id: string | number, profil: any): Observable<Patient> {
    return this.http.put<Patient>(
      `${this.baseUrl}/${id}/profil`,
      profil,
      { headers: this.getHeaders() }
    );
  }

  updateSeuils(id: string | number, seuils: any): Observable<Patient> {
    return this.http.put<Patient>(
      `${this.baseUrl}/${id}/seuils`,
      seuils,
      { headers: this.getHeaders() }
    );
  }

  updateMedecinTraitant(id: string | number, medecinId: string | number): Observable<Patient> {
    return this.http.put<Patient>(
      `${this.baseUrl}/${id}/medecin?medecinId=${medecinId}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  // ─── DELETE ────────────────────────────────────────────────────

  deletePatient(id: string | number): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }
}