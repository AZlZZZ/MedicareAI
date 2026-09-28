import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject, Injector } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { StorageService } from '../../shared/services/storage.service';
import { Router } from '@angular/router';
import { ToasterService } from '../../shared/services/toaster.service';

const PUBLIC_PATHS = ['/auth/signIn', '/auth/requestAccount', '/auth/reset', '/auth/reset-request'];

export function authInterceptor(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> {
  const storageService = inject(StorageService);
  const router = inject(Router);
  const injector = inject(Injector);

  // Skip auth header for public endpoints
  const isPublic = PUBLIC_PATHS.some(p => req.url.includes(p));
  const token = storageService.getToken();

  let outReq = req.clone({
    headers: req.headers.append('ngrok-skip-browser-warning', '69420'),
  });

  if (token && !isPublic) {
    outReq = outReq.clone({
      headers: outReq.headers.append('Authorization', `Bearer ${token.trim()}`),
    });
  }

  return next(outReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        const toastr = injector.get(ToasterService);
        toastr.showError('SESSIONEXPIRED');
        storageService.removeAll();
        router.navigateByUrl('/auth/login');
      }
      return throwError(() => error);
    })
  );
}
