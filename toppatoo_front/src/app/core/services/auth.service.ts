import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

export interface LoginResponse {
  success: boolean;
  message: string;
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
  actif: boolean;
  medecinId: number;  // ← nouveau
  patientId: number;  // ← nouveau
};
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient, private router: Router) {}

  // ── CONNEXION ─────────────────────────────────────────────────
  login(credentials: { email: string; password: string }): Observable<LoginResponse> {
    console.log('Envoi requete login:', credentials.email);

    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, credentials).pipe(
      tap((res) => {
        if (res.success && res.accessToken) {
          localStorage.setItem('accessToken',  res.accessToken);
          localStorage.setItem('refreshToken', res.refreshToken);
          localStorage.setItem('user',         JSON.stringify(res.user));
          console.log('Token stocke avec succes');
        }
      }),
      catchError((error) => {
        console.error('Erreur connexion:', error.status, error.error);
        return throwError(() => error);
      })
    );
  }

 registerMedecin(payload: any): Observable<any> {
  return this.http.post(`${this.apiUrl}/auth/register`, {
    ...payload,
    role: 'MEDECIN'
  });
}

registerPatient(payload: any): Observable<any> {
  return this.http.post(`${this.apiUrl}/auth/register`, {
    ...payload,
    role: 'PATIENT'
  });
}

  // ── RESET MOT DE PASSE ────────────────────────────────────────
  resetPassword(email: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/reset-password`, { email, newPassword });
  }

  // ── REFRESH TOKEN ─────────────────────────────────────────────
  refreshToken(): Observable<{ accessToken: string; refreshToken: string }> {
    const token = localStorage.getItem('refreshToken');
    if (!token) {
      this.logout();
      return throwError(() => new Error('No refresh token'));
    }
    return this.http.post<{ accessToken: string; refreshToken: string }>(
      `${this.apiUrl}/auth/refresh`, { refreshToken: token }
    ).pipe(
      tap((res) => {
        localStorage.setItem('accessToken',  res.accessToken);
        localStorage.setItem('refreshToken', res.refreshToken);
      }),
      catchError((err) => {
        this.logout();
        return throwError(() => err);
      })
    );
  }

  // ── DÉCONNEXION ───────────────────────────────────────────────
  logout(): void {
    localStorage.clear();
    sessionStorage.clear();
    console.log('Session cleared');
    this.router.navigate(['/login']);
  }

  // ── GETTERS ───────────────────────────────────────────────────
  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getUser(): any {
    const u = localStorage.getItem('user');
    return u ? JSON.parse(u) : null;
  }

  getRole(): string | null {
    return this.getUser()?.role ?? null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  isMedecin(): boolean { return this.getRole() === 'MEDECIN'; }
  isPatient(): boolean { return this.getRole() === 'PATIENT'; }
}