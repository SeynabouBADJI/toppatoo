import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notification } from '../models/notification.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly baseUrl = `${environment.apiUrl}/notifications`;

  constructor(private http: HttpClient) {}

  getByPatient(patientId: string): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.baseUrl}/patient/${patientId}`);
  }

  getNonLues(patientId: string): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.baseUrl}/patient/${patientId}/non-lues`);
  }

  compterNonLues(patientId: string): Observable<{ nonLues: number }> {
    return this.http.get<{ nonLues: number }>(`${this.baseUrl}/patient/${patientId}/compteur`);
  }

  marquerLue(id: string): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/${id}/lue`, {});
  }
}