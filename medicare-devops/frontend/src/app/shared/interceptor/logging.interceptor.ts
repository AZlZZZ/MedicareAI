import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject, Injector } from '@angular/core';
import { catchError, Observable } from 'rxjs';
import { StorageService } from '../../shared/services/storage.service';
import { Router } from '@angular/router';
import { ToasterService } from '../../shared/services/toaster.service';

let redirecting = false;

export function loggingInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
  const storageService = inject(StorageService);
  const token = storageService.getToken();
  const router = inject(Router);
  const injector = inject(Injector);

  if (!token || req.url.includes('/login')) {
    return next(req);
  }

  const newReq = req.clone({
    headers: req.headers.append('Authorization', `Bearer ${token}`)
  });

  return next(newReq).pipe(catchError((error: HttpErrorResponse) => {
    if (error.status === 401 && !redirecting) {
      redirecting = true;
      const toastr = injector.get(ToasterService);
      toastr.showError('SESSIONEXPIRED');
      storageService.removeAll();
      router.navigateByUrl('auth/login').then(() => { redirecting = false; });
    }
    throw error;
  }));
}

