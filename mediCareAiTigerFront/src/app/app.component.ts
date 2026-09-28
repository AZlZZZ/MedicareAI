import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PermissionStore } from './stores/permission.store';
import { AuthService } from './shared/services/auth.service';
import { ToastContainerComponent } from "./toaster/toast-container/toast-container.component";
import { TranslateService } from '@ngx-translate/core';
import { StorageService } from './shared/services/storage.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  standalone: true,
  imports: [ToastContainerComponent , RouterModule],
})
export class AppComponent implements OnInit {
  permissionStore = inject(PermissionStore);
  authService = inject(AuthService);
  router = inject(Router);
  title = 'Mediare AI Tiger Front';
    storage = inject(StorageService);
  ngOnInit(): void {
    const token = this.storage.getToken();
    if (token && this.authService.isTokenValid()) {
      // Restore store from JWT payload — no backend call needed
      const payload = this.authService.decodeToken(token);
      const userType = this.storage.getUserType() ?? '';
      if (payload) {
        this.permissionStore.setData(
          payload.agent_id ?? payload.id ?? Number(this.storage.userId()) ?? 0,
          payload.lastName ?? this.storage.getLastName() ?? '',
          payload.firstName ?? this.storage.getFirstName() ?? '',
          payload.role ?? this.storage.getRole() ?? '',
          userType
        );
      }
    } else if (token) {
      // Token exists but expired
      this.storage.removeAll();
      this.router.navigateByUrl('auth/login');
    }
  }
  constructor(private translate: TranslateService) {
    this.translate.setDefaultLang('en');
    this.translate.use('en');
  } 
}
