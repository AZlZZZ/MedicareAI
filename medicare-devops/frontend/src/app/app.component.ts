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
    if (this.storage.getToken() != null) {
      this.authService.checkToken().subscribe((res: any) => {

        if (res.code === 200) {

          this.permissionStore.setData(
             res.data['profil']['id'],
              res.data['profil']['lastName'],
              res.data['profil']['firstName'],
              res.data['profil']['role'] ?? '',
              res.data['userType']
             ) 

        }
        else {
          this.router.navigateByUrl('auth/login');
        }
      }, (error: any) => {
        console.log("err:", error);

        this.router.navigate(['auth/login']);
      });
    }
  }
  constructor(private translate: TranslateService) {
    this.translate.setDefaultLang('en');
    this.translate.use('en');
  } 
}
