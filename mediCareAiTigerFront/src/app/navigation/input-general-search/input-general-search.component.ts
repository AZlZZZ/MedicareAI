import { Component, inject, Input } from '@angular/core';
import { ClientService } from '../../shared/services/client.service';
import { debounceTime, distinctUntilChanged, Observable, OperatorFunction, switchMap } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-input-general-search',
  standalone: true,
  imports: [CommonModule, FormsModule, NgbTypeaheadModule, TranslateModule],
  templateUrl: './input-general-search.component.html',
  styleUrl: './input-general-search.component.css'
})
export class InputGeneralSearchComponent {
private router = inject(Router);
  private clientsService = inject(ClientService);

   searchTypes: any[] = [
    { item: 'FORM.AGENT', value: 'Agent' },
    { item: 'FORM.CLIENT', value: 'Client' },
  ];

  typeSearch: string = 'Reference';
  prefixe: string = '';

  // Formatteur pour l'affichage dans l'input
  formatterUser = (result: any) => 
    `${this.prefixe}${result.id} ${result.firstName || ''} ${result.lastName || ''}`.trim();

  // Logique de recherche
  search: OperatorFunction<string, readonly any[]> = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      switchMap((term) => this.fetchItems(term))
    );

  fetchItems(keyword: string): Observable<any[]> {
    return new Observable((observer) => {
      if (keyword.length < 4) { // Réduit à 4 pour plus de réactivité
        observer.next([]);
        observer.complete();
        return;
      }

      // Exemple de logique simplifiée pour Client
      if (this.typeSearch === 'Client' || keyword.startsWith('CLI-')) {
        this.prefixe = 'CLI-';
        const id = keyword.includes('-') ? keyword.split('-')[1] : keyword;
        
        const queryValue = [{ key: 'id', operation: 'EQUALITY', value: id }];
        const params = {
          pageIndex: 0,
          numberPerPage: 10,
          queryValue: JSON.stringify(queryValue),
          sortDirection: 'DESC',
          sortProperty: 'id',
        };

        this.clientsService.getAllClient(params).subscribe((result: any) => {
          observer.next(result.code === 200 ? result.data.content : []);
          observer.complete();
        });
      }  else if (this.typeSearch === 'Client' || keyword.startsWith('CLI-')){
        
      } {
        // Ajouter ici les autres types (Agent, Course, etc.)
        observer.next([]);
        observer.complete();
      }
    });
  }

  onSelectItem(event: any) {
    const id = event.item.id;
    if (this.prefixe === 'CLI-') {
      this.router.navigateByUrl(`/client/profile/${id}`);
    } else if (this.prefixe === 'AGE-') {
      this.router.navigateByUrl(`/agent/profile/${id}`);
    }
    // Ajoutez vos autres redirections ici
  }

  onChangeType(event: any) {
    this.typeSearch = event.target.value;
  }
}
