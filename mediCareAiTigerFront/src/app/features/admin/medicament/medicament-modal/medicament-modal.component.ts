import { Component, inject, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';
import { MedicamentService } from '../../../../shared/services/medicament.service';
import { ToasterService } from '../../../../shared/services/toaster.service';
import { CancelButtonComponent } from '../../../../shared/cancel-button/cancel-button.component';
import { SaveButtonComponent } from '../../../../shared/save-button/save-button.component';

@Component({
  selector: 'app-medicament-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TranslateModule, CancelButtonComponent, SaveButtonComponent],
  templateUrl: './medicament-modal.component.html',
  styleUrl: './medicament-modal.component.css'
})
export class MedicamentModalComponent implements OnInit {
  @Input() data: any;
  @Input() type: 'add' | 'edit' = 'add';

  activeModal = inject(NgbActiveModal);
  private fb = inject(FormBuilder);
  private api = inject(MedicamentService);
  private toastr = inject(ToasterService);

  form!: FormGroup;
  loading = false;

  ngOnInit() {
    this.form = this.fb.group({
      name: [this.data?.name || '', [Validators.required, Validators.maxLength(120)]],
      description: [this.data?.description || '', [Validators.maxLength(500)]],
      stock: [this.data?.stock ?? 0, [Validators.required, Validators.min(0)]]
    });
  }

  get f() { return this.form.controls; }

  save() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.toastr.showError('FORMEMPTY');
      return;
    }

    this.loading = true;
    const call = this.type === 'edit'
      ? this.api.update(this.data.id, this.form.value)
      : this.api.create(this.form.value);

    call.subscribe({
      next: () => {
        this.loading = false;
        this.toastr.showSuccess('SUCCESS');
        this.activeModal.close();
      },
      error: () => {
        this.loading = false;
        this.toastr.showError('ERROR');
      }
    });
  }

  dismiss() { this.activeModal.dismiss(); }
}
