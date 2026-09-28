import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class MedicamentService {
  private http = inject(HttpClient);
  private baseUrl = environment.url;

  getAll(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/doctor/medicaments/getAll`, { withCredentials: true });
  }

  getPatients(data: any): Observable<any> {
    const params = new HttpParams({ fromObject: data });
    return this.http.get<any>(`${this.baseUrl}/doctor/medicaments/patients`, { params, withCredentials: true });
  }

  create(data: { name: string; description: string }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/doctor/medicaments/create`, data, { withCredentials: true });
  }

  update(id: number, data: { name: string; description: string; stock?: number }): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/doctor/medicaments/${id}`, data, { withCredentials: true });
  }

  updateStock(id: number, quantity: number): Observable<any> {
    const params = new HttpParams().set('quantity', quantity);
    return this.http.patch<any>(`${this.baseUrl}/doctor/medicaments/${id}/stock`, null, {
      params,
      withCredentials: true
    });
  }

  delete(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/doctor/medicaments/${id}`, { withCredentials: true });
  }
}
