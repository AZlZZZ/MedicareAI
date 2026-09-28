import { Component, Input } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ToasterService } from '../../../../shared/services/toaster.service';
import { RecordService } from '../../../../shared/services/record.service';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-diagnostic-modal',
  standalone: true,
  imports: [TranslateModule , ReactiveFormsModule],
  templateUrl: './diagnostic-modal.component.html',
  styleUrl: './diagnostic-modal.component.css'
})
export class DiagnosticModalComponent {
@Input() recordId!: number; // Reçu du composant "Timeline"
  
  DiagForm!: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private activeModal: NgbActiveModal,
    private diagnosticService: RecordService,
    private toastr: ToasterService
  ) {}

  ngOnInit() {
    this.initForm();
  }

  initForm() {
    this.DiagForm = this.fb.group({
      recordId: [this.recordId], // Fixé dès le départ
      diseaseName: ['', [Validators.required]],
      description: ['', [Validators.required]],
      symptoms: [''],
      prescription: ['', [Validators.required]],
      severity: ['MEDIUM', [Validators.required]]
    });
  }

  get f() { return this.DiagForm.controls; }

  save() {
    if (this.DiagForm.invalid) {
      this.DiagForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    const payload = this.DiagForm.getRawValue();

    this.diagnosticService.addDiagnostic(payload).subscribe({
      next: (res) => {
        this.toastr.showSuccess('DIAGNOSTIC.SUCCESS_ADD');
        this.activeModal.close(true); // On renvoie true pour rafraîchir la timeline
      },
      error: (err) => {
        this.loading = false;
        this.toastr.showError('ERROR.SAVE_FAILED');
      }
    });
  }

  dismissModal() {
    this.activeModal.dismiss();
  }
}
