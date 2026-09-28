import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Toast } from '../../shared/services/toaster.service';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.css', 
   standalone:true,
   imports :[TranslateModule ,CommonModule]
})
export class ToastComponent {
@Input() toast!: Toast;
  @Output() closeEvent = new EventEmitter<number>();

  close() {
    this.closeEvent.emit(this.toast.id);
  }

  // Mapping des types aux classes CSS Bootstrap/personnalisées
  get typeClass(): string {
    switch (this.toast.type) {
      case 'success':
        return 'toast-success-border';
      case 'error':
        return 'toast-error-border';
      case 'information':
      default:
        return 'toast-info-border';
    }
  }

  // Mapping des icônes
  get typeIcon(): string {
    switch (this.toast.type) {
      case 'success':
        return 'success-fill.png';
      case 'error':
        return 'error-fill.png';
      case 'information':
      default:
        return 'bi-info-circle-fill';
    }
  }
}
