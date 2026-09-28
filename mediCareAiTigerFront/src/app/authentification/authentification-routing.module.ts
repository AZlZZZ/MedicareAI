import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { AuthLayoutComponent } from './auth-layout/auth-layout.component';
import { LoginFormComponent } from './login-form/login-form.component';
import { PatientRegisterFormComponent } from './patient-register-form/patient-register-form.component';
import { CodeValidationFormComponent } from './code-validation-form/code-validation-form.component';
import { ForgotPasswordFormComponent } from './forgot-password-form/forgot-password-form.component';


const routes: Routes = [
 {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'login', component: LoginFormComponent },
    { path: 'patient/register', component: PatientRegisterFormComponent },
      { path: 'validation/:code', component: CodeValidationFormComponent },
      
      {
        path: 'forgot/password',
        component: ForgotPasswordFormComponent,
      },
    ],

  },
  {
        path: 'reset/password/:token',
        component: ResetPasswordComponent,
      },
  
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthentificationRoutingModule { }
