import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClientService } from '../../../../shared/services/client.service';
import { DoctorService } from '../../../../shared/services/doctor.service';
import { RecordService } from '../../../../shared/services/record.service';
import { ToasterService } from '../../../../shared/services/toaster.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-appointment-modal',
  standalone: true,
  imports: [ReactiveFormsModule , TranslateModule],
  templateUrl: './appointment-modal.component.html',
  styleUrl: './appointment-modal.component.css'
})
export class AppointmentModalComponent {
form!: FormGroup;
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

    this.form = this.fb.group({
      patientId: [0, [Validators.required, Validators.min(1)]],
      doctorId: [0, [Validators.required, Validators.min(1)]],
      createdAt: ['', [Validators.required, ]]
    });

    // Simule le chargement des données
    this.loadData();
  }

  get f() { return this.form.controls; }

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
    if (this.form.valid) {
      this.loading = true;
      
      const finalData = {
        patientId: Number(this.form.value.patientId),
        doctorId: Number(this.form.value.doctorId),
        createdAt: this.form.value.createdAt
      };
this.recordService.addAppointment(finalData).subscribe({
  next: (res) => {

this.activeModal.close(true);  },
  error: (err) => {
     this.loading = false;
        this.toastr.showError('ERROR.SAVE_FAILED');
  }
});
      
    }else{
      this.form.markAllAsTouched();
    }
  }

  dismissModal() {
    // Logique pour fermer le modal (NgbActiveModal ou autre)
  }
}
