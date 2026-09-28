import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { StorageService } from './storage.service';
import { Router } from '@angular/router';

export type UserRole = 'AGENT' | 'PATIENT' | 'DOCTOR';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private baseUrl = environment.url;

  constructor(
    private http: HttpClient,
    private storageService: StorageService,
    private router: Router
  ) {}

  // ── Sign In ──────────────────────────────────────────────────────────────

  loginAgent(data: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/agent/auth/signIn`, data);
  }

  loginPatient(data: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/client/auth/signIn`, data);
  }

  loginDoctor(data: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/doctor/auth/signIn`, data);
  }

  login(role: UserRole, data: { username: string; password: string }): Observable<any> {
    switch (role) {
      case 'AGENT':   return this.loginAgent(data);
      case 'DOCTOR':  return this.loginDoctor(data);
      case 'PATIENT': return this.loginPatient(data);
    }
  }

  // ── Patient registration ─────────────────────────────────────────────────

  requestAccountPatient(data: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/client/auth/requestAccount`, data);
  }

  // ── Password reset (agent only for now) ──────────────────────────────────

  forgotPassword(email: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/agent/auth/reset-request?email=${email}`, {});
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/agent/auth/reset?token=${token}&newPassword=${newPassword}`, {});
  }

  // ── Check token (validate with backend) ──────────────────────────────────

  checkToken(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/auth/check-token`);
  }

  // ── Token helpers ─────────────────────────────────────────────────────────

  /** Decode JWT payload without verifying signature (client-side only). */
  decodeToken(token: string): any | null {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  }

  isTokenValid(): boolean {
    const token = this.storageService.getToken();
    if (!token) return false;
    const payload = this.decodeToken(token);
    if (!payload) return false;
    return payload.exp * 1000 > Date.now();
  }

  startAutoLogout(): void {
    const token = this.storageService.getToken();
    if (!token) return;
    const payload = this.decodeToken(token);
    if (!payload) return;
    const timeout = payload.exp * 1000 - Date.now();
    if (timeout > 0) {
      setTimeout(() => this.logout(), timeout);
    } else {
      this.logout();
    }
  }

  logout(): void {
    this.storageService.removeAll();
    this.router.navigateByUrl('/auth/login');
  }
}
