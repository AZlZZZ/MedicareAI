import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import {
  NgbDate,
  NgbDatepicker,
  NgbDatepickerMonth,
  NgbDateStruct,
  NgbDropdownModule,
  NgbTypeaheadModule,
  NgbTypeaheadSelectItemEvent,
} from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import {
  debounceTime,
  distinctUntilChanged,
  Observable,
  OperatorFunction,
  switchMap,
} from 'rxjs';
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { environment } from '../../../environments/environment.development';
import { ClientService } from '../services/client.service';
import { FilterDateTemplateComponent } from '../filter-date-template/filter-date-template.component';

@Component({
  selector: 'app-filter-template',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule,
    NgbDropdownModule,
    NgbTypeaheadModule,
    FilterDateTemplateComponent,
  ],
  templateUrl: './filter-template.component.html',
  styleUrl: './filter-template.component.css',
  styles: ``,
  animations: [
    trigger('expandCollapse', [
      state(
        'collapsed',
        style({
          width: 'max-content',
        })
      ),
      state(
        'expanded',
        style({
          // width: '330px',
        })
      ),
      transition('collapsed <=> expanded', [animate('300ms ease-in-out')]),
    ]),
  ],
})
export class FilterTemplateComponent implements OnInit {
  @ViewChild('dateFilter') dateFilterComponent!: FilterDateTemplateComponent;
  @Input() type: any;
  hideSearch: boolean = false;
  hideSearchGodlen: boolean = false;
  form!: FormGroup;
  entrepriseId!: any;
  @Output() formValuesChanged = new EventEmitter<any>();
  //permissionStore = inject(PermissionStore);
  typeSearch: any;
  searchTypes: any[] = [];
  url = environment.urlFiles;
  selectedStatus: string = 'STATE';
  selectedLabel: string = 'SUPPORT.LABEL';
  statusListe: string[] = ['BLOCKED', 'ARCHIVED', 'ENABLED'];
  // LabalsListe: string[] = ['FEEDBACK', 'CLAIM', 'HELP'];
  selectedperiod: any = 'DATE';
  selectedGolden!: any;
  selectedDriver!: any;
  selectedClient!: any;

  constructor(
    private translate: TranslateService,
    private fb: FormBuilder,
   // private driverService: DriverService,
    private clientService: ClientService,
  ) {
    // Initialisation du formulaire
    this.form = this.fb.group({
      ref: new FormControl(null),
      golden: new FormControl(null),
      category: new FormControl(null),
      lastName: new FormControl(null),
      client: new FormControl(null),
      status: new FormControl(null),
      phone: new FormControl(null),
      label: new FormControl(null),
      date: this.fb.group({
        type: this.fb.control('day'), // FormControl for date type ('day', 'week', 'month')
        startDay: this.fb.control(null), // FormControl for start date
        endDay: this.fb.control(null), // FormControl for end date
        year: this.fb.control(null), // FormControl for end date
        week: this.fb.control(null), // FormControl for end date
        month: this.fb.control(null), // FormControl for end date
        day: this.fb.control(null), // FormControl for end date
      }),
    });
  }

  ngOnInit(): void {
   // this.entrepriseId = this.permissionStore.entrepriseId();
    // init types seach and
   
    if (this.type === 'AGENTS') {
      this.searchTypes = [
        { prefix: 'REF', value: 'ref', hint: 'REF' },
        { prefix: 'OTHER', value: 'lastName', hint: 'LASTFIRSTNAMEDRIVER' },
      ];
    }  else if (this.type === 'CLIENTS') {
      this.searchTypes = [
        { prefix: 'REF', value: 'ref', hint: 'REF' },
        { prefix: 'OTHER', value: 'lastName', hint: 'LASTFIRSTNAME' },
      ];
    }  
    this.typeSearch = this.searchTypes[0];
  }

  changeStatus(status: string) {
    this.selectedStatus = status;
    this.form.get('status')?.setValue(this.selectedStatus);
    this.formValuesChanged.emit(this.form.value);
  }
  changeLables(status: string) {
    this.selectedLabel = status;
    this.form.get('label')?.setValue(this.selectedLabel);
    this.formValuesChanged.emit(this.form.value);
  }

  hideSearchButton() {
    console.log("search type" , this.searchTypes);
    
    this.hideSearch = !this.hideSearch;
    if (this.hideSearch) {
      this.hideSearchGodlen = false;
    }
  }
  hideSearchButtonGolden() {
    this.hideSearchGodlen = !this.hideSearchGodlen;
    if (this.hideSearchGodlen) {
      this.hideSearch = false;
    }
  }
  onChangeInput(): void {
    if (this.form.value.ref) {
      this.form.get('ref')?.setValue(this.form.value.ref.trim());
    }
    this.formValuesChanged.emit(this.form.value);
  }
  onChange(event: any) {
    console.log(this.form.value);
          this.form.get('ref')?.setValue('');

    const index = event.target.value;
    this.typeSearch = this.searchTypes[index];
  }
  formatterUser = (result: { firstName?: string; lastName?: string } = {}) =>
    `${result.firstName || ''} ${result.lastName || ''}`.trim();
  formatterCategory = (result: { name?: string;} = {}) =>
    `${result.name || ''} `.trim();

  // Fonction de formatage pour afficher le nom dans l'autocomplétion
  formatter = (result: { name: string }) => result.name;

  formatterGolden = (result: { name?: string } = {}) =>
    `${result.name || ''} `.trim();


  searchClient: OperatorFunction<
    string,
    Array<{ firstName: string; lastName: string; id: number }>
  > = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      switchMap((term) => this.fetchClients(term)) // Appel API avec switchMap pour gérer l'autocomplétion
    );
  
  onSelectClient(event: NgbTypeaheadSelectItemEvent) {
    this.form.get('client')?.setValue(event.item?.id || event.item); // Mettre à jour le champ dans le formulaire
    this.formValuesChanged.emit(this.form.value);
  }

  fetchClients(
    keyword: string
  ): Observable<Array<{ firstName: string; lastName: string; id: number }>> {
    return new Observable((observer) => {
      if (keyword.length < 3) {
        observer.next([]);
        observer.complete();
        return;
      }

      let queryValue = [
        {
          key: 'firstName',
          operation: 'CONTAINS',
          value: keyword,
          orPredicate: true,
        },
        {
          key: 'lastName',
          operation: 'CONTAINS',
          value: keyword,
          orPredicate: true,
        },
        {
          key: 'phoneNumber',
          operation: 'CONTAINS',
          value: keyword,
          orPredicate: true,
        },
      ];

      let obj: any = {
        pageIndex: 0,
        numberPerPage: 10,
        queryValue: JSON.stringify(queryValue),
        sortDirection: 'DESC',
        sortProperty: 'id',
      };

      this.clientService.getAllClient(obj).subscribe((result: any) => {
        if (result.code == 200) {
          observer.next(result.data.content);
        } else {
          observer.next([]);
        }
        observer.complete();
      });
    });
  }
  restForm() {
    this.form.reset();
    this.selectedStatus = 'STATE';
    this.selectedperiod = 'DATE';
    if (this.type == 'NOTIFS') {
      this.selectedLabel = 'CATEGORY';
    } else {
      this.selectedLabel = 'SUPPORT.LABEL';
    }

    if (this.dateFilterComponent) {
      this.dateFilterComponent.resetForm();
    }
    this.formValuesChanged.emit(this.form.value);
  }
  changeFilterDate(event: any) {
    this.form.get('date')?.setValue(event);
    this.formValuesChanged.emit(this.form.value);
  }
}
