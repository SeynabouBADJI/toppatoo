import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notification } from '../models/notification.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly baseUrl = `${environment.apiUrl}/notifications`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('accessToken');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // ✅ patientId en number
  getByPatient(patientId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(
      `${this.baseUrl}/patient/${patientId}`,
      { headers: this.getHeaders() }
    );
  }

  getNonLues(patientId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(
      `${this.baseUrl}/patient/${patientId}/non-lues`,
      { headers: this.getHeaders() }
    );
  }

  compterNonLues(patientId: number): Observable<{ nonLues: number }> {
    return this.http.get<{ nonLues: number }>(
      `${this.baseUrl}/patient/${patientId}/compteur`,
      { headers: this.getHeaders() }
    );
  }

  // ✅ id en number
  marquerLue(id: number): Observable<void> {
    return this.http.put<void>(
      `${this.baseUrl}/${id}/lue`,
      {},
      { headers: this.getHeaders() }
    );
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }
}