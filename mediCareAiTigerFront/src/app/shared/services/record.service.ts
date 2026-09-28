import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class RecordService {
 private baseUrl = environment.url + '/agent/';

  constructor(private http: HttpClient) {}

  // Récupérer tous les records
  getAllRecords(data :any ): Observable<any[]> {
        const params = new HttpParams({ fromObject: data });

    return this.http.get<any[]>(this.baseUrl +  'records', { params , });
  }
  // Récupérer tous les records
  getAllAppointment(data :any ): Observable<any[]> {
        const params = new HttpParams({ fromObject: data });

    return this.http.get<any[]>(this.baseUrl +  'appointment', { params , });
  }

  getDiagnostics(data :any): Observable<any[]> {
       const params = new HttpParams({ fromObject: data });

    return this.http.get<any[]>(this.baseUrl +  'records/diagnostics', { params  });
  }

  // Ajouter un diagnostic à un record
  addDiagnostic( diagnostic: any): Observable<any> {
    return this.http.post(`${this.baseUrl}records/createDiagnostic`, diagnostic , {});
  }
  // Ajouter un diagnostic à un record
  addRecord( record: any): Observable<any> {
    return this.http.post(`${this.baseUrl}records/createRecord`, record , {});
  }
    addAppointment( record: any): Observable<any> {
    return this.http.post(`${this.baseUrl}appointment`, record , {});
  }
    deleteAppointment( id: number ): Observable<any> {
    return this.http.delete(`${this.baseUrl}appointment/` + id);
  }
}
