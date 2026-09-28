import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root',
})
export class ClientService {
  constructor(
    private http: HttpClient,
    private storageService: StorageService
  ) {}
  baseUrl = environment.url;

  getAllClient(data: any): Observable<any> {
    const params = new HttpParams({ fromObject: data });
    return this.http.get<any>(
      this.baseUrl + '/agent/manageUser/patient/findAll',
      {
        params,  withCredentials: true
      } , 
    );
  }
  getClientInfo(id: number): Observable<any> {
    return this.http.get<any>(
      this.baseUrl + '/agent/manageUser/client/findById?idClient=' + id
    );
  }

  updatePatient(id: any, data: any): Observable<any> {
    return this.http.put<any>(
      this.baseUrl + '/agent/manageUser/updatePatient?id=' + id,
      data
    );
  }
   createPatient( data: any): Observable<any> {
    return this.http.post<any>(
      this.baseUrl + '/agent/manageUser/createPatient',
      data , {withCredentials: true}
    );
  }
  changeUserState(userId: any, state: string): Observable<any> {
    return this.http.put<any>(
      this.baseUrl + `/agent/manageUser/changeUserState?userId=${userId}&state=${state}`,
      null
    );
  }

  getClientById(id: any): Observable<any> {
    return this.http.get<any>(
      this.baseUrl + '/agent/manageUser/client/findById?idClient=' + id
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
