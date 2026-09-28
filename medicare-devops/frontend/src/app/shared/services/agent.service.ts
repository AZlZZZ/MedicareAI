import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class AgentService {

  constructor(private http: HttpClient) { }
  baseUrl = environment.url;

  getAllAgent(data : any):Observable<any> {
    const params = new HttpParams({ fromObject: data });
    return this.http.get<any>(this.baseUrl + '/agent/manageUser/agent/findAll', { params, withCredentials: true }  )
  } 
  
  RegisterAgent(data: any) :Observable<any>{
  
    return this.http.post<any>(this.baseUrl + '/agent/manageUser/registerAgent',data , { withCredentials: true })
  }
  getAgentInfo(id: number): Observable<any> {
    return this.http.get<any>(this.baseUrl + '/agent/manageUser/agent/findById?idAgent=' + id, { withCredentials: true })
  }  
  getUpdateAgent( id : any , data: any):Observable<any> {

    return this.http.put<any>(this.baseUrl + '/agent/manageUser/updateAgent?id='+id , data , { withCredentials: true })
  } 
  getRoles():Observable<any> {

    return this.http.get<any>(this.baseUrl + '/agent/role/findAll' , { withCredentials: true })
  } 
  
  export(data :any) {
      const params = new HttpParams({ fromObject: data });
    const url = `${this.baseUrl}/agent/manageUser/agent/export` ;
    return this.http.get(url, {
      responseType: 'blob', 
      params,
      observe: 'response'
    });
  }
  
  
}

