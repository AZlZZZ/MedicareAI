import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { DoctorService } from '../../../../shared/services/doctor.service';
import { ClientService } from '../../../../shared/services/client.service';
import { forkJoin } from 'rxjs';
import { RecordService } from '../../../../shared/services/record.service';
import { ToasterService } from '../../../../shared/services/toaster.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-record-modal',
  standalone: true,
  imports: [TranslateModule, ReactiveFormsModule],
  templateUrl: './record-modal.component.html',
  styleUrl: './record-modal.component.css'
})
export class RecordModalComponent {
recordForm!: FormGroup;
  loading = false;
  private patientService = inject(ClientService);
  private doctorService = inject(DoctorService);
  private recordService = inject(RecordService);
  private toastr = inject(ToasterService);
  private activeModal = inject(NgbActiveModal);

  // Listes à charger depuis tes services
  patients: any[] = []; 
  doctors: any[] = [];

  constructor(private fb: FormBuilder) {}

  ngOnInit() {

    this.recordForm = this.fb.group({
      patientId: [0, [Validators.required, Validators.min(1)]],
      doctorId: [0, [Validators.required, Validators.min(1)]],
      title: ['', [Validators.required, Validators.minLength(3)]]
    });

    // Simule le chargement des données
    this.loadData();
  }

  get f() { return this.recordForm.controls; }

  loadData() {
    
  let obj = {
      pageIndex: 0,
      numberPerPage: 100,
      queryValue: JSON.stringify([]),
      sortDirection: 'DESC',
      sortProperty: 'id',
    };
    let obj2 = {
      pageIndex: 0,
      numberPerPage: 100, 
      queryValue: JSON.stringify([]),
      sortDirection: 'DESC',
      sortProperty: 'id',
    };  
    forkJoin({
      patients: this.patientService.getAllClient(obj),
      doctors: this.doctorService.getAll(obj2)
    }).subscribe(({ patients, doctors }) => {
      this.patients = patients?.data?.content || [];
      this.doctors = doctors?.data?.content || [];
    },( error :any)  => {
      console.error('Erreur lors du chargement des données :', error);
    });
   
  }

  save() {
    if (this.recordForm.valid) {
      this.loading = true;
      
      const finalData = {
        patientId: Number(this.recordForm.value.patientId),
        doctorId: Number(this.recordForm.value.doctorId),
        title: this.recordForm.value.title
      };
this.recordService.addRecord(finalData).subscribe({
  next: (res) => {

this.activeModal.close(true);  },
  error: (err) => {
     this.loading = false;
        this.toastr.showError('ERROR.SAVE_FAILED');
  }
});
      
    }else{
      this.recordForm.markAllAsTouched();
    }
  }

  dismissModal() {
    // Logique pour fermer le modal (NgbActiveModal ou autre)
  }
}
