import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root',
})
export class DoctorService {
  constructor(
    private http: HttpClient,
    private storageService: StorageService
  ) {}
  baseUrl = environment.url;

  getAll(data: any): Observable<any> {
    const params = new HttpParams({ fromObject: data });
    return this.http.get<any>(
      this.baseUrl + '/agent/manageUser/doctor/findAll',
      {
        params,  withCredentials: true
      } , 
    );
  }
  getClientInfo(id: number): Observable<any> {
    return this.http.get<any>(
      this.baseUrl + '/agent/manageUser/doctor/findById?idDoctor=' + id
    );
  }

  updateDoctor(id: any, data: any): Observable<any> {
    return this.http.put<any>(
      this.baseUrl + '/agent/manageUser/updateDoctor?idDoctor=' + id,
      data, {withCredentials: true}
    );
  }
   createDoctor( data: any): Observable<any> {
    return this.http.post<any>(
      this.baseUrl + '/agent/manageUser/createDoctor',
      data , {withCredentials: true}
    );
  }
  deleteDoctor(id: any) {
    return this.http.delete(this.baseUrl + '/agent/manageUser/deleteDoctor/' + id, {withCredentials: true});
  }

  getDoctorById(id: any): Observable<any> {
    return this.http.get<any>(
      this.baseUrl + '/agent/manageUser/doctor/findById?idDoctor=' + id
    );
  }
  BlockPatientAccount(data: any): Observable<any> {
    return this.http.put<any>(
      this.baseUrl + '/agent/manageUser/blockPatientAccount?' + data,
      null, {withCredentials: true}
    );
  }
  getAllReasons(data: any): Observable<any> {
   
    return this.http.get<any>(this.baseUrl + '/agent/reason/getAllReasons?queryValue='+ data);
  }
   export(data :any) {
      const params = new HttpParams({ fromObject: data });
    const url = `${this.baseUrl}/agent/manageUser/exportPatient` ;
    return this.http.get(url, {
      responseType: 'blob', 
      params,
      observe: 'response',
      withCredentials: true
    });
  }
    downloadTemplate(): Observable<any> {
      const url = environment.url + '/agent/csv/template' ;
    return this.http.get(url, {
      responseType: 'blob', 
      
      // observe: 'response'
    });
  }
  
   uploadTemplate(file : FormData): Observable<any> {
    return this.http.post<any>(environment.url + '/agent/csv/upload',file)
  }
}
