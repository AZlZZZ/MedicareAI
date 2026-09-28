import { Component, inject, Inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../shared/services/auth.service';
import { StorageService } from '../../shared/services/storage.service';
import { ToasterService } from '../../shared/services/toaster.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-patient-register-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './patient-register-form.component.html',
  styleUrl: './patient-register-form.component.css'
})
export class PatientRegisterFormComponent {

form!: FormGroup;
  today = new Date().toISOString().split('T')[0];
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);
  private storageService = inject(StorageService);
  private toastService = inject(ToasterService);
 
  ngOnInit(): void {
    this.form = this.formBuilder.group({
      username: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      firstName: ['', [Validators.required]],
      lastName:  ['', [Validators.required]],
      phoneNumber: [''],
      gender: [''],
      birthDate: [''],
    });
  }

  get username()    { return this.form.get('username'); }
  get password()    { return this.form.get('password'); }
  get firstName()   { return this.form.get('firstName'); }
  get lastName()    { return this.form.get('lastName'); }
  get phoneNumber() { return this.form.get('phoneNumber'); }
  get gender()      { return this.form.get('gender'); }
  get birthDate()   { return this.form.get('birthDate'); }

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
