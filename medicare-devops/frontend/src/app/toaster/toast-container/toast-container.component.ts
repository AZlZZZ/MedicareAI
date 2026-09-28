import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Toast, ToasterService } from '../../shared/services/toaster.service';
import { ToastComponent } from '../toast/toast.component';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule, ToastComponent],
  template: `
    <div class="toast-container position-fixed top-0 end-0 p-3">
  @for( toast of activeToasts; track toast.id){
    <app-toast 
    [toast]="toast" 
        (closeEvent)="removeToast($event)">
      </app-toast>
   }   
    </div>
  `,
  styleUrl: './toast-container.component.css'
})
export class ToastContainerComponent {
activeToasts: Toast[] = [];

  constructor(private toasterService: ToasterService) {}

  ngOnInit() {
    this.toasterService.toastEvents$.subscribe(toast => {
      this.activeToasts.push(toast);

      // Déclenche l'auto-fermeture
      setTimeout(() => this.removeToast(toast.id), toast.duration);
    });
  }

  removeToast(id: number) {
    this.activeToasts = this.activeToasts.filter(toast => toast.id !== id);
  }
}
