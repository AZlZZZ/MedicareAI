import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
 public userId() {
    return localStorage.getItem('userId');
  }
  public saveuserId(token: any): void {
    localStorage.removeItem('userId');
    localStorage.setItem('userId', token);
  }
 
  public saveToken(token: any): void {
    localStorage.removeItem('token');
    localStorage.setItem('token', token);
  }

  public getToken(): any | null {
    return localStorage.getItem('token');
  }

  public saveEntrepriseID(id: any): void {
    localStorage.removeItem('entrepriseID');
    localStorage.setItem('entrepriseID', id);
  }

  public getEntrepriseID(): any | null {
    console.log(localStorage.getItem('entrepriseID'));
    
    return localStorage.getItem('entrepriseID');
  }
  public saveEntrepriseName(name: any): void {
    localStorage.removeItem('EntrepriseName');
    localStorage.setItem('EntrepriseName', name);
  }

  public getEntrepriseName(): any | null {
    return localStorage.getItem('EntrepriseName');
  }
  
  public removeToken() {
    localStorage.removeItem('token');
  }

  public getRole() {
    return localStorage.getItem('role');
  }
  public saveRole(role: string) {
    localStorage.removeItem('role');
    localStorage.setItem('role', role);
  }

  public getFirstName() {
    return localStorage.getItem('firstName');
  }
  public saveFirstName(firstName: string) {
    localStorage.removeItem('firstName');
   localStorage.setItem('firstName', firstName);
  }
  public getLastName() {
    return localStorage.getItem('lastName');
  }
  public saveLastName(firstName: string) {
    localStorage.removeItem('lastName');
    localStorage.setItem('lastName', firstName);
  }
  public getgender() {
    return localStorage.getItem('gender');
  }
  public saveGender(gender: string) {
   localStorage.removeItem('gender');
   localStorage.setItem('gender', gender);
  }
  public getphoto() {
    return localStorage.getItem('photo');
  }
  public savehoto(photo: string) {
   localStorage.removeItem('photo');
    localStorage.setItem('photo', photo);
  }
  public getFunctions() {
    return localStorage.getItem('functions');
  }
  public saveFunctions(role: string) {
    localStorage.removeItem('functions');
    localStorage.setItem('functions', role);
  }
  public removeRole() {
    localStorage.removeItem('role');
  }
  public removeAll() {
    localStorage.clear();
  }

  public clearSession(): void {
    Object.keys(localStorage).forEach((key) => {
        localStorage.removeItem(key);
    });
  }

  public getUserType(): string | null {
    return localStorage.getItem('userType');
  }
  public saveUserType(userType: string): void {
    localStorage.removeItem('userType');
    localStorage.setItem('userType', userType);
  }

}
