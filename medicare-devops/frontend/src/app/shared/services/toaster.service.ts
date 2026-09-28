import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

export type ToastType = 'success' | 'error' | 'information';

export interface Toast {
  type: ToastType;
  message: string;
  id: number;
  duration?: number; // Durée optionnelle en ms
}
@Injectable({
  providedIn: 'root'
})
export class ToasterService {

private toastSubject = new Subject<Toast>();
  private nextId = 0;

  // Observable que le ToastContainer écoutera
  toastEvents$: Observable<Toast> = this.toastSubject.asObservable();

  /**
   * Ajoute une nouvelle notification
   * @param message Le message à afficher
   * @param type Le type de notification (success, error, information)
   * @param duration Durée d'affichage avant auto-fermeture (par défaut 3000ms)
   */
  show(message: string, type: ToastType, duration: number = 3000) {
    const toast: Toast = {
      id: this.nextId++,
      type,
      message,
      duration
    };
    this.toastSubject.next(toast);
  }

  showSuccess(message: string, duration?: number) {
    this.show(message, 'success', duration);
  }

  showError(message: string, duration?: number) {
    this.show(message, 'error', duration);
  }

  information(message: string, duration?: number) {
    this.show(message, 'information', duration);
  }
}
