import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { StorageService } from './storage.service';
import { environment } from '../../../environments/environment.development';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PermissionsService {

  constructor(private http: HttpClient,
    private router: Router,
    private storageService: StorageService,
  ) { }
  baseUrl = environment.url + '/agent/';

  getRoles(data :any ): Observable<any> {
    const params = new HttpParams({ fromObject: data });

    return this.http.get<any>(this.baseUrl + 'role/findAll', { withCredentials: true, params })
  }
  ListRoles(data: any): Observable<any> {
    const params = new HttpParams({ fromObject: data });
    return this.http.get<any>(this.baseUrl + 'role/list', { params , withCredentials: true })
  }
  // createRole(data: any): Observable<any> {
  //   return this.http.post<any>(this.baseUrl + 'role/add', data)
  // }
  // editRole(data: any): Observable<any> {
  //    const params = new HttpParams({ fromObject: data });
  //   return this.http.put<any>(this.baseUrl + 'role/rename', null ,  { params})
  // }
  // getFounctionsById(id: any): Observable<any> {

  //   return this.http.get<any>(this.baseUrl + 'role/functionByRoleId?roleId=' + id)
  // }
  // getPermissionsByID(id: any): Observable<any> {

  //   return this.http.get<any>(this.baseUrl + '/role/function/findRoleById?roleId=' + id)
  // }
  // assignFunction(idRole:any , data: any): Observable<any> {
  //   return this.http.post<any>(this.baseUrl + 'role/function/assign?roleId='+idRole, data)
  // }
  // deleteRole(id: any): Observable<any> {
  //   return this.http.delete<any>(this.baseUrl + 'role/delete?roleId='+id)
  // }
  // assignSubFunction(data: any): Observable<any> {
  //   return this.http.post<any>(this.baseUrl + 'role/subFunction/assign', data)
  // }
  // unassignFunction(data: any): Observable<any> {
  //   return this.http.post<any>(this.baseUrl + 'role/function/unassign', data)
  // }
  // unassignSubFunction(data: any): Observable<any> {
  //   return this.http.post<any>(this.baseUrl + 'role/subFunction/unassign', data)
  // }
}
