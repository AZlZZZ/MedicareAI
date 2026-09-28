import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../shared/services/auth.service';
import { ToasterService } from '../../shared/services/toaster.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styles: ``
})
export class ResetPasswordComponent implements OnInit {
  form!: FormGroup;
  token = '';
  loading = false;
  success = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private toastr: ToasterService
  ) {}

  ngOnInit() {
    this.token = this.route.snapshot.paramMap.get('token') ?? '';
    this.form = new FormGroup({
      password: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(20),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/)
      ]),
      confirm: new FormControl('', [Validators.required])
    }, { validators: this.passwordsMatch });
  }

  passwordsMatch(group: AbstractControl) {
    const p = group.get('password')?.value;
    const c = group.get('confirm')?.value;
    return p === c ? null : { mismatch: true };
  }

  get password() { return this.form.get('password'); }
  get confirm() { return this.form.get('confirm'); }

  onSubmit() {
    if (this.form.invalid) return;
    this.loading = true;
    this.authService.resetPassword(this.token, this.form.value.password).subscribe({
      next: (res: any) => {
        this.loading = false;
        if (res.code === 200) {
          this.success = true;
          this.toastr.showSuccess('Password reset successfully!');
          setTimeout(() => this.router.navigateByUrl('/auth/login'), 2000);
        } else {
          this.toastr.showError(res.msg ?? 'Invalid or expired token.');
        }
      },
      error: () => {
        this.loading = false;
        this.toastr.showError('Invalid or expired token.');
      }
    });
  }
}
