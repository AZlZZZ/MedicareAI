import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../shared/services/auth.service';
import { ToasterService } from '../../shared/services/toaster.service';

@Component({
  selector: 'app-forgot-password-form',
  standalone: true,
  imports: [ReactiveFormsModule , RouterModule],
  templateUrl: './forgot-password-form.component.html',
  styleUrl: './forgot-password-form.component.css'
})
export class ForgotPasswordFormComponent {
 AuthForm!: FormGroup;
  constructor(
    private router: Router,
    private authService: AuthService,
    private toastService: ToasterService, 

  ) { }
  ngOnInit(): void {
    this.AuthForm = new FormGroup({
      email: new FormControl('', [
        Validators.required,
        Validators.email
      ])

    });
  }
  get email() {
    return this.AuthForm.get('email');
  }
  onSubmit() {
    if (this.AuthForm.valid) {
      this.authService.forgotPassword(this.AuthForm.get('email')?.value).subscribe(
        (response: any) => {
          if (response.code === 200) {
            // this.router.navigate(['/auth/reset/password/' + response.data]);
            this.toastService.showSuccess(response.msg)
          }
          else {
            this.toastService.showError(response.msg)

          }
        },
        (error: any) => {
           this.toastService.showError('ERRORSERVENU');
          // console.error('Error in password reset request:', error);

        }
      );
    } else {
    Object.keys(this.AuthForm.controls).forEach((field) => {
        const control = this.AuthForm.get(field);
        control?.markAsTouched({ onlySelf: true });

      });
      this.toastService.showError('FORMEMPTY')
    }
  }
}
