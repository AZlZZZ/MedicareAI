import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { CancelButtonComponent } from '../cancel-button/cancel-button.component';

@Component({
  selector: 'app-confirm-modal',
  standalone: true,
  imports: [TranslateModule, CancelButtonComponent],
  templateUrl: './confirm-modal.component.html',
  styleUrl: './confirm-modal.component.css'
})
export class ConfirmModalComponent {
  @Input() type: any; 
  @Input() data: any; 
  @Input() modalType: any; 
  loading: boolean = false 
  constructor(public activeModal: NgbActiveModal , 
    private translate : TranslateService
  ) {
    
    
   }

  closeModal() {
    this.activeModal.close('Modal Closed');
  }

  dismissModal() {
    this.activeModal.dismiss('Modal Dismissed');
  }
  onConfirm(): void {
   // console.log(this.data);
   this.activeModal.close('confirm');
    
  }

  // showSuccess(title: string, content: string) {
  //   this.toastr.success(content, title);
  // }
  // showError(title: string, content: string) {
  //   this.toastr.error(content, title);
  // }
  // showWarning(title: string, content: string) {
  //   this.toastr.warning(content, title);
  // } 
}
