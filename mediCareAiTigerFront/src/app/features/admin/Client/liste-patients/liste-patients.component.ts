import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClientService } from '../../../../shared/services/client.service';
import { MedicamentService } from '../../../../shared/services/medicament.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmModalComponent } from '../../../../shared/confirm-modal/confirm-modal.component';
import { ToastrService } from 'ngx-toastr';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { forkJoin, Observable } from 'rxjs';
import { ExportCSVService } from '../../../../shared/services/export-csv.service';
import { PermissionStore } from '../../../../stores/permission.store';
import { StorageService } from '../../../../shared/services/storage.service';
import { CommonModule } from '@angular/common';
import { BtnExportComponent } from "../../../../shared/btn-export/btn-export.component";
import { FilterTemplateComponent } from "../../../../shared/filter-template/filter-template.component";
import { ReusableTableComponent } from "../../../../shared/reusable-table/reusable-table.component";
import { PatientModalComponent } from '../patient-modal/patient-modal.component';

@Component({
  selector: 'app-liste-patients',
  templateUrl: './liste-patients.component.html',
  styles: ``,
  standalone:true , 
  imports: [TranslateModule, CommonModule, BtnExportComponent, FilterTemplateComponent, ReusableTableComponent]
})
export class ListePatientsComponent {
 filterCollapse = false;
  loadingExport: boolean = false;
   downloadService = inject(ExportCSVService);
toggleCollapse() {
  this.filterCollapse = !this.filterCollapse;
}
entrepriseId: any;
nbFilter: any;
  private modalService = inject(NgbModal);
  Clients: any[] = [];
  numberOfClient!: number;
  totalPage!: number;
  //  Pagination parameters
  page = 1;
  pageSize = 5;
  collectionSize = 0;
  maxSize = 5;
  loading: boolean = false;
  prefixe!: string;
  queryParams: any[] = [];
  liste_stat: any[] = [];
  checkRef: Boolean = true;
  displayedColumns = [
    'TABLE.REF',
    'TABLE.NAME',
    'TABLE.TEL',
    'TABLE.CREATIONDATE',
    'TABLE.STATUS',
  ];
  dataColumns = [
    'id',
    'Client',
    'phoneNumber',
    'createdAt',
    'state',
    'Action',
  ];

actionsPermissions = {
  details: { feature: 'CLIENT_MANAGEMENT', subFeature: 'DETAILS_CLIENT' },
  edit: { feature: 'CLIENT_MANAGEMENT', subFeature: 'EDIT_CLIENT' },
  delete: { feature: 'CLIENT_MANAGEMENT', subFeature: 'DELETE_CLIENT' }
};
  private permissionStore = inject(PermissionStore);
 stoarge = inject(StorageService);
 private patienttService = inject(ClientService);
 private medicamentService = inject(MedicamentService);
  private router = inject(Router);

  get isAgent(): boolean {
    return this.stoarge.getUserType() === 'AGENT';
  }

  private fetchPatients(obj: any): Observable<any> {
    return this.isAgent
      ? this.patienttService.getAllClient(obj)
      : this.medicamentService.getPatients(obj);
  }

  ngOnInit(): void {
    // Patients have no business on this page — redirect to their schedule
    if (this.stoarge.getUserType() === 'PATIENT') {
      this.router.navigateByUrl('/schedules');
      return;
    }
this.entrepriseId =  this.stoarge.getEntrepriseID();
    this.loading = true;

    let obj = {
      pageIndex: this.page - 1,
      numberPerPage: this.pageSize,
      queryValue: JSON.stringify(this.queryParams
        ),
      sortDirection: 'DESC',
      sortProperty: 'id',
    };
    let obj1 = {
      month: (new Date().getMonth() + 1),
      year: new Date().getFullYear()
    };
    forkJoin([
      this.fetchPatients(obj),
    ]).subscribe(
      (rlt: any) => {
        this.loading = false;
        this.Clients = rlt[0].data.content;
        this.collectionSize = this.Clients.length;
        this.loading = false;
        this.totalPage = rlt[0].data.totalElements;
        this.numberOfClient = rlt[0].data.totalElements;
        this.prefixe = this.isAgent ? 'PAT-' : 'DPAT-';
  },
      (err: any) => {
        this.loading = false;
      }
    );
  }
  //Fonction pour récupérer la liste des clients
  getPatientsList() {
    this.loading = true;
    let obj = {
      pageIndex: this.page - 1,
      numberPerPage: this.pageSize,
      queryValue: JSON.stringify(this.queryParams),
      sortDirection: 'DESC',
      sortProperty: 'id',
    };
    if (this.checkRef) {
      this.fetchPatients(obj).subscribe((res: any) => {
        this.Clients = res.data.content;
        this.collectionSize = this.Clients.length;
        this.loading = false;
        this.totalPage = res.data.totalElements;
        this.numberOfClient = res.data.totalElements;
        this.prefixe = this.isAgent ? 'PAT-' : 'DPAT-';
      });
    } else {
      this.Clients = [];
      this.collectionSize = 0;
      this.loading = false;
      this.totalPage = 0;
      this.numberOfClient = 0;

    }

  }
  // affecter les action open modal modifier /supprimer agent 
  edit(item: any) {
    const modalRef = this.modalService.open(PatientModalComponent, {
      size: 'xl',
      centered: true,
    });

    modalRef.componentInstance.data = item;
    modalRef.result
      .then((result) => {
        this.getPatientsList();
      })
      .catch((reason) => { });
  }
  // affecter les action open modal ajouter un client 
  add() {
    const modalRef = this.modalService.open(PatientModalComponent, {
      size: 'xl',
      centered: true,
    });
    modalRef.componentInstance.modaltype = 'add';
    modalRef.result
      .then((result) => {
        this.getPatientsList();
      })
      .catch((reason) => { });
  }

  delete(item: any) {
    const modalRef = this.modalService.open(ConfirmModalComponent, { centered: true });
    modalRef.componentInstance.data = item;
    modalRef.result.then((result) => {
      if (result !== 'confirm') return;
      this.patienttService.changeUserState(item.id, 'DELETED').subscribe({
        next: (res: any) => {
          if (res.code === 200) this.getPatientsList();
        },
        error: () => {}
      });
    }).catch(() => {});
  }

  // redirection vers profile client
  details(item: any) {
    this.router.navigateByUrl('client/profile/' + item.id);
  }
  // Assurer pagination pour tableau  
  chanePageSize(event: any) {
    this.page = event;
    this.getPatientsList();
  }
  changeSize(event: any) {
    this.pageSize = event;
    this.getPatientsList();
  }


  // Assurer la filtrage pour liste des agents 
  chaneFilter(event: any) {
    this.page = 1;
    this.nbFilter = 0;
    this.checkRef = true
    const queryParams: any[] = [];
    // console.log(event)
    let valid: boolean = true;
    // Add   filter by REF if exist
    if (event?.ref?.startsWith('CLI-') && event?.ref.length > 4) {
      queryParams.push({
        key: 'id',
        operation: 'EQUALITY',
        value: event.ref.split('CLI-')[1].trim(),
        // orPredicate: true,
      });
      this.nbFilter++;

    } else if (event?.ref?.length === 0 || !event?.ref) { this.checkRef = true } else {
      this.checkRef = false
    }

    // Add date filters if they exist
    if (event.date?.startDay) {
      queryParams.push({
        key: 'createdAt',
        operation: 'GREATER_THAN',
        value: event.date.startDay,
        criteriaType: 'DEFAULT',
      });
      this.nbFilter++;

    }

    if (event.date?.endDay) {
      queryParams.push({
        key: 'createdAt',
        operation: 'LESS_THAN',
        value: event.date.endDay,
        criteriaType: 'DEFAULT',
      });

    }

    if (event.lastName) {
      this.nbFilter++;

      if (event.lastName.length == 1) {

        queryParams.push({
          key: 'lastName',
          operation: 'CONTAINS',
          value: '*' + event.lastName + '*',
          // orPredicate: true,
        });
        queryParams.push({
          key: 'firstName',
          operation: 'CONTAINS',
          value: '*' + event.lastName + '*',

          orPredicate: true,
        });
        queryParams.push({
          key: 'phoneNumber',
          operation: 'CONTAINS',
          value: '*' + event.lastName + '*',
          orPredicate: true,
        });
      } else {
        queryParams.push({
          key: 'lastName',
          operation: 'CONTAINS',
          value: event.lastName,
          orPredicate: true,
        });
        queryParams.push({
          key: 'firstName',
          operation: 'CONTAINS',
          value: event.lastName,
          orPredicate: true,
        });
        queryParams.push({
          key: 'phoneNumber',
          operation: 'CONTAINS',
          value: event.lastName,
          orPredicate: true,
        });
      }
    }

    if (event.status) {
      this.nbFilter++;
      queryParams.push({
        key: 'state',
        operation: 'EQUALITY',
        value: event.status,
        group: 0,
        criteriaType: 'USER_STATE', 
      });
    }
    this.queryParams = queryParams;
    this.getPatientsList();
  }
  // Exporter Liste des patient dans un fichier CSV
  export() { 
    this.loadingExport = true;
     let obj ={
      queryValue: JSON.stringify([this.queryParams] ),
    } 
    this.patienttService.export(obj).subscribe({
      next: (response) => {
        this.loadingExport = false;
        this.downloadService.downloadCSV(response.body!,"Liste_patients" );
    
      },
      error: (err) => {
        this.loadingExport = false;
      //  this.toastr.showError('ERRORSERVENU');
      }
    });
  }



}
