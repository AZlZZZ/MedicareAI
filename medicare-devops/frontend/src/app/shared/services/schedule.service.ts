import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class ScheduleService {
  private http = inject(HttpClient);
  private base = environment.url;

  // ── Doctor endpoints ──────────────────────────────────────────────────────

  getPatients(pageIndex = 0, numberPerPage = 100): Observable<any> {
    const params = new HttpParams()
      .set('pageIndex', pageIndex)
      .set('numberPerPage', numberPerPage)
      .set('queryValue', '[]')
      .set('sortDirection', 'ASC')
      .set('sortProperty', 'id');
    return this.http.get(`${this.base}/doctor/medicaments/patients`, { params });
  }

  getMedicaments(): Observable<any> {
    return this.http.get(`${this.base}/doctor/medicaments/getAll`);
  }

  getSchedulesForPatient(patientId: number): Observable<any> {
    return this.http.get(`${this.base}/doctor/medicaments/patients/${patientId}/medicament-schedules`);
  }

  createSchedule(patientId: number, dto: any): Observable<any> {
    return this.http.post(`${this.base}/doctor/medicaments/patients/${patientId}/medicament-schedules`, dto);
  }

  editSchedule(scheduleId: number, dto: any): Observable<any> {
    return this.http.put(`${this.base}/doctor/medicaments/medicament-schedules/${scheduleId}`, dto);
  }

  deleteSchedule(scheduleId: number): Observable<any> {
    return this.http.delete(`${this.base}/doctor/medicaments/medicament-schedules/${scheduleId}`);
  }

  // ── Patient endpoints ─────────────────────────────────────────────────────

  getMySchedules(): Observable<any> {
    return this.http.get(`${this.base}/client/medicaments/alerts/schedules`);
  }

  getUpcomingAlerts(weeks = 4): Observable<any> {
    const params = new HttpParams().set('weeks', weeks);
    return this.http.get(`${this.base}/client/medicaments/alerts/upcoming`, { params });
  }

  markAlertRead(alertId: number): Observable<any> {
    return this.http.post(`${this.base}/client/medicaments/alerts/${alertId}/read`, {});
  }
}
