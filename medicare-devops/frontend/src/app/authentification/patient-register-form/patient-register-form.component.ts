import { Component, inject, Inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../shared/services/auth.service';
import { StorageService } from '../../shared/services/storage.service';
import { ToasterService } from '../../shared/services/toaster.service';

@Component({
  selector: 'app-patient-register-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './patient-register-form.component.html',
  styleUrl: './patient-register-form.component.css'
})
export class PatientRegisterFormComponent {

form!: FormGroup;
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);
  private storageService = inject(StorageService);
  private toastService = inject(ToasterService);
 
  ngOnInit(): void {
 
    this.form = this.formBuilder.group({
      username: ['',[Validators.required , Validators.email]],
      password: ['',[Validators.required]],
    });
    //  console.log( this.formUtilities.getControl(this.form, 'username'));
  }
  get username() {
    return this.form.get('username');
  }
  get password() {
    return this.form.get('password');
  }

  save() {
    if (this.form.valid) {
      this.authService
        .requestAccountPatient(this.form.value)
        .subscribe((res) => {
          if (res.code == 200) {
         this.toastService.showSuccess("Account request sent successfully! Please wait for approval.");
         
          } else {
            this.toastService.showError(res.msg);
          }
        });
    } else {
      Object.keys(this.form.controls).forEach((field) => {
        const control = this.form.get(field);
        control?.markAsTouched({ onlySelf: true });
      });
      this.toastService.showError('FORMEMPTY');
    }
  }
}
