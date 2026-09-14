// import { Injectable } from '@angular/core';
// import { HttpClient, HttpHeaders } from '@angular/common/http';
// import { Observable } from 'rxjs';
// import { environment } from '../../../environments/environment';
// import { Consultation, Mesure, Ordonnance } from '../models/consultation.model';

// @Injectable({ providedIn: 'root' })
// export class ConsultationService {
//   private readonly apiUrl = `${environment.apiUrl}/consultations`;

//   constructor(private http: HttpClient) {}

//   private getHeaders(): HttpHeaders {
//     const token = localStorage.getItem('accessToken');
//     return new HttpHeaders({
//       'Authorization': `Bearer ${token}`,
//       'Content-Type': 'application/json'
//     });
//   }

//   // ✅ Créer une consultation
//   creer(consultation: Partial<Consultation>): Observable<Consultation> {
//     return this.http.post<Consultation>(
//       this.apiUrl,
//       consultation,
//       { headers: this.getHeaders() }
//     );
//   }

//   // ✅ Ajouter une mesure
//   ajouterMesure(consultationId: number, mesure: Partial<Mesure>): Observable<Mesure> {
//     return this.http.post<Mesure>(
//       `${this.apiUrl}/${consultationId}/mesures`,
//       mesure,
//       { headers: this.getHeaders() }
//     );
//   }

//   // ✅ Ajouter une ordonnance
//   ajouterOrdonnance(consultationId: number, ordonnance: Partial<Ordonnance>): Observable<Ordonnance> {
//     return this.http.post<Ordonnance>(
//       `${this.apiUrl}/${consultationId}/ordonnance`,
//       ordonnance,
//       { headers: this.getHeaders() }
//     );
//   }

//   // ✅ Récupérer une consultation
//   getById(id: number): Observable<Consultation> {
//     return this.http.get<Consultation>(
//       `${this.apiUrl}/${id}`,
//       { headers: this.getHeaders() }
//     );
//   }

//   // ✅ Récupérer les consultations d'un patient
//   getByPatient(patientId: number): Observable<Consultation[]> {
//     return this.http.get<Consultation[]>(
//       `${this.apiUrl}/patient/${patientId}`,
//       { headers: this.getHeaders() }
//     );
//   }

//   // ✅ Dernière consultation
//   getDerniere(patientId: number): Observable<Consultation> {
//     return this.http.get<Consultation>(
//       `${this.apiUrl}/patient/${patientId}/derniere`,
//       { headers: this.getHeaders() }
//     );
//   }

//   // ✅ Mettre à jour une consultation
//   update(id: number, consultation: Partial<Consultation>): Observable<Consultation> {
//     return this.http.put<Consultation>(
//       `${this.apiUrl}/${id}`,
//       consultation,
//       { headers: this.getHeaders() }
//     );
//   }

//   getHistorique(patientId: number): Observable<Consultation[]> {
//     return this.http.get<Consultation[]>(
//       `${this.apiUrl}/patient/${patientId}`,
//       { headers: this.getHeaders() }
//     );
//   }


//   // ✅ Supprimer une consultation
//   delete(id: number): Observable<void> {
//     return this.http.delete<void>(
//       `${this.apiUrl}/${id}`,
//       { headers: this.getHeaders() }
//     );
//   }
// }
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Consultation, Mesure, Ordonnance } from '../models/consultation.model';

@Injectable({ providedIn: 'root' })
export class ConsultationService {
  private readonly apiUrl = `${environment.apiUrl}/consultations`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('accessToken');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // ✅ AJOUTER CETTE MÉTHODE
  getHistorique(patientId: number): Observable<Consultation[]> {
    return this.http.get<Consultation[]>(
      `${this.apiUrl}/patient/${patientId}`,
      { headers: this.getHeaders() }
    );
  }

  creer(consultation: Partial<Consultation>): Observable<Consultation> {
    return this.http.post<Consultation>(
      this.apiUrl,
      consultation,
      { headers: this.getHeaders() }
    );
  }

  ajouterMesure(consultationId: number, mesure: Partial<Mesure>): Observable<Mesure> {
    return this.http.post<Mesure>(
      `${this.apiUrl}/${consultationId}/mesures`,
      mesure,
      { headers: this.getHeaders() }
    );
  }

  ajouterOrdonnance(consultationId: number, ordonnance: Partial<Ordonnance>): Observable<Ordonnance> {
    return this.http.post<Ordonnance>(
      `${this.apiUrl}/${consultationId}/ordonnance`,
      ordonnance,
      { headers: this.getHeaders() }
    );
  }

  getById(id: number): Observable<Consultation> {
    return this.http.get<Consultation>(
      `${this.apiUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }

  getByPatient(patientId: number): Observable<Consultation[]> {
    return this.http.get<Consultation[]>(
      `${this.apiUrl}/patient/${patientId}`,
      { headers: this.getHeaders() }
    );
  }

  getDerniere(patientId: number): Observable<Consultation> {
    return this.http.get<Consultation>(
      `${this.apiUrl}/patient/${patientId}/derniere`,
      { headers: this.getHeaders() }
    );
  }

  update(id: number, consultation: Partial<Consultation>): Observable<Consultation> {
    return this.http.put<Consultation>(
      `${this.apiUrl}/${id}`,
      consultation,
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