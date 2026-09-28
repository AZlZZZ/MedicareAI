import { RouterModule, Routes } from '@angular/router';
import { FullComponent } from './layout/full/full.component';
import { BlankComponent } from './layout/blank/blank.component';
import { authGuard } from './core/guards/auth.guard';
import { noAuthGuard } from './core/guards/no-auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: FullComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadChildren: () =>
          import('./features/features.module').then(m => m.FeaturesModule),
      },
    ],
  },
  {
    path: '',
    component: BlankComponent,
    children: [
      {
        path: 'auth',
        canActivate: [noAuthGuard],
        loadChildren: () =>
          import('./authentification/authentification.module').then(
            m => m.AuthentificationModule
          ),
      },
    ],
  },
];
