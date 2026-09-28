import { Component, EventEmitter, inject, Input, Output, ViewChild } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  NgbActiveModal,
  NgbModalModule,
  NgbModule,
} from '@ng-bootstrap/ng-bootstrap';
import {
  debounceTime,
  distinctUntilChanged,
  Observable,
  OperatorFunction,
  switchMap,
} from 'rxjs';
import { AgentService } from '../../../../shared/services/agent.service';
import { StorageService } from '../../../../shared/services/storage.service';
import { environment } from '../../../../../environments/environment.development';
import { PermissionsService } from '../../../../shared/services/permissions.service';
import {
  CountryISO,
  NgxIntlTelInputComponent,
  NgxIntlTelInputModule,
  SearchCountryField,
} from 'ngx-intl-tel-input';
import { PhoneNumberUtil } from 'google-libphonenumber';
import { ToasterService } from '../../../../shared/services/toaster.service';
import { CancelButtonComponent } from '../../../../shared/cancel-button/cancel-button.component';
import { TranslateModule } from '@ngx-translate/core';
import { NgSelectComponent } from "@ng-select/ng-select";
import { SaveButtonComponent } from "../../../../shared/save-button/save-button.component";
import { AutocompleteLibModule } from 'angular-ng-autocomplete';
import { CommonModule } from '@angular/common';
// import intlTelInput, { CountryISO, SearchCountryField } from 'ngx-intl-tel-input';
@Component({
  selector: 'app-agent-modal',
  templateUrl: './agent-modal.component.html',
  styleUrl: './agent-modal.component.css',
  standalone:true ,
  imports: [CancelButtonComponent, TranslateModule, NgSelectComponent, SaveButtonComponent , NgbModule , ReactiveFormsModule , NgxIntlTelInputModule, AutocompleteLibModule , CommonModule ]
 
})
export class AgentModalComponent {
   // permissionStore = inject(PermissionStore);
    stoarge = inject(StorageService);
  submitted: boolean = false;
  enterPassword: Boolean = false;
  active = 1;
  @Input() data: any;
  @Input() goldenDealers: any;
  @Output() agentUpdated = new EventEmitter<void>();
  CountryISO: typeof CountryISO = CountryISO; // Définit une variable pour CountryISO
  SearchCountryField = SearchCountryField; // Définit une variable pour SearchCountryField
  phoneUtil = PhoneNumberUtil.getInstance();
  @ViewChild(NgxIntlTelInputComponent) telInput!: NgxIntlTelInputComponent;
  selectedCountryISO: CountryISO = CountryISO.Tunisia;
  add: boolean = true;
  AgentForm!: FormGroup;
  regions: any[] = [];
  entrepriseId!: any;
  keyword = 'name';
  url: string = environment.urlFiles;

  loading: boolean = false
  Roles: any[] = [];
  founctions: any[] = [];
  selectedRegions: any[] = [];
  goldenDealersListe = [];
  onlyCountries: any[] = [CountryISO.Tunisia];
  showPassword: boolean = false;
  ngAfterViewInit() {
    if (!this.add) {
      const country = this.telInput.allCountries.find(
        (c) => c.dialCode === this.data.phonePrefix.replace('+', '')
      );
      this.selectedCountryISO = country?.iso2.toUpperCase() as CountryISO;
    }
  }

  constructor(
    public activeModal: NgbActiveModal,
    private fb: FormBuilder,
    private api: AgentService,
     private apis: PermissionsService,
    private toasterService: ToasterService
  ) { }

  // fonction  de formatage pour date sous forme YYYY-MM-DD
  formatDate(dateString: string): string {
    if (!dateString) return ''; // Vérifie si la date existe
    return dateString.split(' ')[0]; // Garde uniquement "YYYY-MM-DD"
  }
 

  ngOnInit() {
    // this.entrepriseId = this.permissionStore.entrepriseId();
    this.entrepriseId = this.stoarge.getEntrepriseID();
    this.getRoles();
    // this.getGoldenDealersListe();
    if (this.data) {
      this.add = false;
      this.selectedRegions = this.data?.regionDTOList;

      this.AgentForm = this.fb.group({
        firstName: new FormControl(this.data.firstName, [
          Validators.required,
          // Validators.minLength(4),
        ]),
        lastName: new FormControl(this.data.lastName, [
          Validators.required,
          // Validators.minLength(4),
        ]),
        phoneNumber: new FormControl(this.data.phoneNumber, [
          Validators.required,
        ]),
        email: new FormControl(this.data.email, [
          Validators.required,
          Validators.email,
        ]),

        birthDate: new FormControl(this.formatDate(this.data.birthDate), [
          Validators.required,
        ]),
        password: new FormControl('', [
          // Validators.required,
          // Validators.pattern(/^\d{8}$/),
          // Validators.pattern(
          //   /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,20}$/
          // ),
        ]),

        roleId: new FormControl(this.data?.role.id, [Validators.required]),
        // regionDTOs: new FormControl(this.data?.regionDTOList, [
        //   Validators.required,
        // ]),
        // goldenId: new FormControl(this.data.golden, [Validators.required]),
        gender: new FormControl(this.data?.gender, [Validators.required]),
      });

      // this.getFounctionsById(this.data?.role.id);
    } else {
      this.AgentForm = this.fb.group({
        firstName: new FormControl('', [
          Validators.required,
          // Validators.minLength(4),
        ]),
        lastName: new FormControl('', [
          Validators.required,
          // Validators.minLength(4),
        ]),
        phoneNumber: new FormControl('', [
          Validators.required,
          // Validators.minLength(4),
        ]),
        email: new FormControl('', [Validators.required, Validators.email]),

        birthDate: new FormControl('', [Validators.required]),
        password: new FormControl('', [
          Validators.required,
          // Validators.pattern(/^\d{8}$/),
          Validators.pattern(
            /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*\?])[A-Za-z\d!@#$%^&*\?]{8,20}$/
          ),
        ]),
        //address: new FormControl('', [Validators.required]),
        gender: new FormControl('', [Validators.required]),
        // goldenId: new FormControl('', [Validators.required]),
        roleId: new FormControl('', [Validators.required]),
        // regionDTOs: new FormControl(this.data?.regionDTOList, [
        //   Validators.required,
        // ]),
        //addressAddDTO : new FormControl('', []),
      });
    }
  }
  //  vider  champ Godlen Dealer
  clearGoldenDealers() {
    this.AgentForm.get('goldenId')?.setValue(null);
  }
  //   Convertir date d'input sous Forme yyyy-MM-dd
  convertToISO(dateStr: string): string {
    const parts = dateStr.split('-');
    if (parts.length === 3) {
      return `${parts[2]}/${parts[1]}/${parts[0]}`; // yyyy-MM-dd
    }
    return dateStr; // Si le format est incorrect, ne pas modifier
  }
 // Fonction pour récupérer la liste des role
  getRoles() {
    let obj = {
      pageIndex: 0,
      numberPerPage: 10,
      sortDirection: 'DESC',
      sortProperty: 'id',
    };
    this.apis.getRoles(obj).subscribe((res: any) => {
      if (res.code == 200) {
        this.Roles = res.data.content;
      } else {
        this.toasterService.showError(res?.msg);
      }
    });
  }
  // Fonction pour récupérer la liste des fonctions par role sélectionnée
  // getFounctionsById(id: any) {
  //   const id_role = this.RoleId?.value;
  //   this.api.getFounctionsById(id_role).subscribe((res: any) => {
  //     if (res.code == 200) {
  //       this.founctions = res.data;
  //     } else {
  //       this.toasterService.showError(res?.msg);
  //     }
  //   });
  // }

  onNext() {
    if (this.active === 1) {
      this.active = 2;
    } else if (this.data) {
      this.UpdateAgent();
    } else {
      this.RegisterAgent();
    }
  }
  backStep() {
    this.active = 1;
  }
  get firstName() {
    return this.AgentForm.get('firstName');
  }
  get gender() {
    return this.AgentForm.get('gender');
  }
  get lastName() {
    return this.AgentForm.get('lastName');
  }
  get address() {
    return this.AgentForm.get('address');
  }

  get email() {
    return this.AgentForm.get('email');
  }
  get RoleId() {
    return this.AgentForm.get('roleId');
  }
  get password() {
    return this.AgentForm.get('password');
  }
  get phoneNumber() {
    return this.AgentForm.get('phoneNumber');
  }
  get birthDate() {
    return this.AgentForm.get('birthDate');
  }
  get goldenId() {
    return this.AgentForm.get('goldenId');
  }
  get regionDTOs() {
    return this.AgentForm.get('regionDTOs');
  }

  // Fonction pour créer un agent
  RegisterAgent() {
    this.submitted = true;

    if (this.AgentForm.valid) {
      let {
        birthDate,
        regionDTOs,
        phoneNumber,
        phonePrefix,
        ...obj
      } = this.AgentForm.value;

      obj = {
        ...obj,
        birthDate: this.convertToISO(this.AgentForm.get('birthDate')?.value),
        phoneNumber: phoneNumber?.number,
        phonePrefix: phoneNumber?.dialCode,
      };
      this.loading = true;
      this.api.RegisterAgent(obj).subscribe((res: any) => {
        this.loading = false;
        if (res.code == 200) {
          this.activeModal.close('Modal Closed');
          this.AgentForm.reset();
          this.toasterService.showSuccess('AGENT.ADDSUCCESS');
        } else {
          this.toasterService.showError(res?.msg);
        }
      },
        (err: any) => {
          this.loading = false;
        });
    } else {
      Object.keys(this.AgentForm.controls).forEach((field) => {
        const control = this.AgentForm.get(field);
        control?.markAsTouched({ onlySelf: true });
      });
      this.toasterService.showError('FORMEMPTY');
    }
  }

  // Fonction pour modifiée un agent
  UpdateAgent() {
    if (this.AgentForm.valid) {
      let {
        birthDate,
        password,
        phoneNumber,
        phonePrefix,
        ...obj
      } = this.AgentForm.value;

      obj = {
        ...obj,

        birthDate: this.convertToISO(this.AgentForm.get('birthDate')?.value),
        phoneNumber: phoneNumber?.number,
        phonePrefix: phoneNumber?.dialCode,
      };
      if (this.AgentForm.value.password) {
        obj = {
          ...obj,
          password: this.AgentForm.value.password,
        };
      }
      this.loading = true;
      this.api.getUpdateAgent(this.data?.id, obj).subscribe((res: any) => {
        this.loading = false;
        if (res.code == 200) {
          this.toasterService.showSuccess('AGENT.EDITSUCCESS');
          this.activeModal.close('Modal Closed');
          this.AgentForm.reset();
          this.agentUpdated.emit();
        } else {
          this.toasterService.showError(res?.data);
        }
      },
        (err: any) => {
          this.loading = false;
        });
    } else {
      Object.keys(this.AgentForm.controls).forEach((field) => {
        const control = this.AgentForm.get(field);
        control?.markAsTouched({ onlySelf: true });
      });
      this.toasterService.showError('FORMEMPTY');
    }
  }
  dismissModal() {
    this.activeModal.dismiss('Modal Dismissed');
  }

  //  Assurer la validation du formulaire
  changeValue(type: string) {
    if (type === 'firstName' && this.firstName?.errors?.['required']) {
      this.toasterService.showError('ALERT.FIRSTNAMEREQUIRE');
    } else if (type === 'lastName' && this.lastName?.errors?.['required']) {
      this.toasterService.showError('ALERT.LASTNAMEREQUIRE');
    } else if (type === 'phone' && this.phoneNumber?.errors?.['required']) {
      this.toasterService.showError('ALERT.PHONEREQUIRE');
    } else if (type === 'email' && this.email?.errors?.['required']) {
      this.toasterService.showError('ALERT.EMAILREQUIRE');
    } else if (type === 'RoleId' && this.RoleId?.errors?.['required']) {
      this.toasterService.showError('ALERT.ROLEREQUIRE');
    } else if (type === 'password' && this.password?.errors?.['required']) {
      this.toasterService.showError('ALERT.PASSWORDREQUIRE');
    } 
    
  }

  passwordStrength = {
    hasUpperLowerCase: false,
    hasNumber: false,
    hasSpecialChar: false,
    isProperLength: false,
  };
  // validation de mot de passe
  checkPasswordConstraints(password: string): void {
    this.passwordStrength.hasUpperLowerCase = /(?=.*[a-z])(?=.*[A-Z])/.test(
      password
    );
    this.passwordStrength.hasNumber = /[0-9]/.test(password);
    this.passwordStrength.hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(
      password
    );
    this.passwordStrength.isProperLength =
      password.length >= 8 && password.length <= 20;
  }
  onPasswordInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.checkPasswordConstraints(value);
    const isValid =
      this.passwordStrength.hasUpperLowerCase &&
      this.passwordStrength.hasNumber &&
      this.passwordStrength.hasSpecialChar &&
      this.passwordStrength.isProperLength;

    this.enterPassword = !isValid;
  }
}
