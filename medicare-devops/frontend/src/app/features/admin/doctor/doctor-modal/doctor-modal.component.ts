import { Component, inject, Input, ViewChild } from '@angular/core';
import { CancelButtonComponent } from "../../../../shared/cancel-button/cancel-button.component";
import { SaveButtonComponent } from "../../../../shared/save-button/save-button.component";
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ToasterService } from '../../../../shared/services/toaster.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CountryISO, NgxIntlTelInputComponent, SearchCountryField } from 'ngx-intl-tel-input';
import { PhoneNumberUtil } from 'google-libphonenumber';
import { environment } from '../../../../../environments/environment';
import { DoctorService } from '../../../../shared/services/doctor.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-doctor-modal',
  standalone: true,
  imports: [CancelButtonComponent, SaveButtonComponent , TranslateModule, ReactiveFormsModule ],
  templateUrl: './doctor-modal.component.html',
  styleUrl: './doctor-modal.component.css'
})
export class DoctorModalComponent {
 @Input() data: any; 
  private fb = inject(FormBuilder);
  private  api = inject(DoctorService);
  activeModal = inject(NgbActiveModal);
  private toastr  = inject(ToasterService);
  form!: FormGroup;
  loading :any = false;
  showDropdown :any = false;
CountryISO = CountryISO; // Définit une variable pour CountryISO
  SearchCountryField = SearchCountryField; // Définit une variable pour SearchCountryField
  phoneUtil = PhoneNumberUtil.getInstance();
  selectedCountryISO: CountryISO = this.CountryISO.Tunisia;
  onlyCountries: any[] = [this.CountryISO.Tunisia];
selectedLocation : any 
  @ViewChild(NgxIntlTelInputComponent) telInput!: NgxIntlTelInputComponent;
  
  url = environment.urlFiles ;
 PhotoError = false;
   imageSrc: string = 'assets/icons/logo-vlox.svg'; // Default image
  photoFile!: any;
  ngOnInit() {
    this.initForm();

   

  }
initForm() {
  this.form = this.fb.group({
    firstName: [this.data?.firstName || '', [Validators.required]],
    lastName: [this.data?.lastName || '', [Validators.required]],
    email: [this.data?.email || '', [Validators.required, Validators.email]],
    phoneNumber: [this.data?.phoneNumber || '', [Validators.required]],
    specialization: [this.data?.specialization || '', [Validators.required]],
    homeAddress: [this.data?.homeAddress || '', [Validators.required]],
    // These will be updated via handleAddressChange
    // homeLatitude: [this.data?.homeLatitude || 0],
    // homeLongitude: [this.data?.homeLongitude || 0],
    
  },);

  
}




  get f() { return this.form.controls; }

save() {
  if (this.form.invalid) {
    this.form.markAllAsTouched();
    this.toastr.showError('FORMEMPTY');
    return;
  }

 


  this.loading = true;
  const apiCall = this.data?.id 
    ? this.api.updateDoctor(this.form.value, this.data.id) 
    : this.api.createDoctor(this.form.value);

  apiCall.subscribe({
    next: (res: any) => {
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

dismissModal() {
    this.activeModal.dismiss();
  }

}
