import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { AuthService, UserRole } from '../../shared/services/auth.service';
import { StorageService } from '../../shared/services/storage.service';
import { ToasterService } from '../../shared/services/toaster.service';
import { PermissionStore } from '../../stores/permission.store';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [CommonModule, TranslateModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.css',
})
export class LoginFormComponent {
  permissionStore = inject(PermissionStore);
  form!: FormGroup;
  loading = false;

  roles: { label: string; value: UserRole }[] = [
    { label: 'Agent', value: 'AGENT' },
    { label: 'Médecin', value: 'DOCTOR' },
    { label: 'Patient', value: 'PATIENT' },
  ];

  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);
  private storageService = inject(StorageService);
  private toastService = inject(ToasterService);

  constructor() {
    if (this.authService.isTokenValid()) {
      this.router.navigateByUrl('');
    } else {
      this.storageService.removeAll();
    }
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      role: ['AGENT', Validators.required],
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  get f() {
    return this.form.controls;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      Object.values(this.form.controls).forEach(c => c.markAsTouched());
      this.toastService.showError('FORMEMPTY');
      return;
    }

    this.loading = true;
    const { role, username, password } = this.form.value;

    this.authService.login(role as UserRole, { username, password }).subscribe({
      next: (res: any) => {
        this.loading = false;
        if (res.code === 200) {
          const tokens = res.data;
          this.storageService.saveToken(tokens.accessToken);
          this.storageService.saveUserType(role);

          // Decode JWT to get user info
          const payload = this.authService.decodeToken(tokens.accessToken);
          if (payload) {
            this.permissionStore.setData(
              payload.id ?? 0,
              payload.lastName ?? '',
              payload.firstName ?? '',
              payload.role ?? '',
              role
            );
          }

          this.toastService.showSuccess('Successfully logged in!');
          this.authService.startAutoLogout();
          this.router.navigateByUrl('/');
        } else {
          this.toastService.showError(res.msg ?? 'AUTH.ALERTLOGINERROR');
        }
      },
      error: () => {
        this.loading = false;
        this.toastService.showError('AUTH.ALERTLOGINERROR');
      },
    });
  }
}
