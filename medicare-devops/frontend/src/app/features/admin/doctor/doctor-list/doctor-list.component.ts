import { Component, inject } from '@angular/core';
import { FilterTemplateComponent } from "../../../../shared/filter-template/filter-template.component";
import { BtnExportComponent } from "../../../../shared/btn-export/btn-export.component";
import { ReusableTableComponent } from "../../../../shared/reusable-table/reusable-table.component";
import { BtnFlottantComponent } from "../../../../shared/btn-flottant/btn-flottant.component";
import { DoctorModalComponent } from '../doctor-modal/doctor-modal.component';
import { subscribe } from 'diagnostics_channel';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToasterService } from '../../../../shared/services/toaster.service';
import { Router } from '@angular/router';
import { DoctorService } from '../../../../shared/services/doctor.service';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-doctor-list',
  standalone: true,
  imports: [  ReusableTableComponent,
    TranslateModule,
    FilterTemplateComponent,
    CommonModule,
    TranslateModule,
    BtnFlottantComponent],
  templateUrl: './doctor-list.component.html',
  styleUrl: './doctor-list.component.css'
})
export class DoctorListComponent {
  // parametres du filtre
  nbFilter: number = 0;
  isCollapseOpen: boolean = false;
  queryParams: any[] = [];
  objStatistic: any;
  // Pagination parameters
  page = 1;
  pageSize = 10;
  collectionSize = 0;
  numberOfItems: number = 0;
  totalPage: number = 2;

  selectedItem: number = 1;
hasPermissionAdd = true 
  //parametres du tableau
  data: any[] = [];
 displayedColumns = [
    'TABLE.REF',
    'TABLE.NAME',
    'TABLE.TEL',
    'TABLE.SPECIALIZATION',
    'TABLE.STATUS',
  ];
  dataColumns = [
    'id',
    'Client',
    'phoneNumber',
    'specialization',
    'state',
    'Action',
  ];
  prefixe  = 'DOC-'
  private modalService = inject(NgbModal);
  private toastr = inject(ToasterService);
  private doctorService = inject(DoctorService);
  private router = inject(Router)
  loading: boolean = true;
  listeStat: any[] = [];
role ?: string ; 
  ngOnInit() {

    this.initData();
  }

  initData() {
    this.loading = true;
    let objList = {
      pageIndex: this.page - 1,
      numberPerPage: this.pageSize,
      queryValue: JSON.stringify(this.queryParams),
      sortDirection: 'DESC',
      sortProperty: 'id',
    }; 
  this.doctorService.getAll(objList).subscribe({
      next: (res: any) => {
        this.loading = false;
        if (res.code === 200) {
          this.data = res.data.content;
          this.numberOfItems = res.data.totalElements || 0;
          this.totalPage = res.data.totalElements || 0;
          this.collectionSize = res?.data.totalElements || 0;
        }
      },
      error: (err : any) => {
        this.toastr.showError('ERRORSERVENU');
      }
    });
  }

 
  add() {
    const modalRef = this.modalService.open(DoctorModalComponent, {
      size: 'xl',
      scrollable: true,
      centered: true,
    });
    modalRef.componentInstance.type = 'add';
    modalRef.result
      .then(() => {
       
        this.initData();
      })
      .catch(() => { });
  }

  details(item: any) {

    this.router.navigateByUrl(`/backoffice/entreprise/profile/` + item.id);
  }

  //changer le numero de page
  changePage(event: number) {
    this.page = event;
    this.initData();
  }

  //modifier le nombre d'élément par page
  changeSize(event: number) {
    this.pageSize = event;
    this.initData();
  }
  changeFilter(event: any) {
    this.page = 1;
    this.nbFilter = 0;
    this.refreshList(event);
  }
  refreshList(filterCriteria: any) {

    this.loading = true;
  
    let searchParams: any[] = [];
    this.queryParams = this.queryParams.filter(q => q.key === 'state');
 
    // Format client et site
    if (filterCriteria.client?.id)
      filterCriteria.client = filterCriteria.client.id;
    if (filterCriteria.site?.id) filterCriteria.site = filterCriteria.site.id;
    // Client
    if (filterCriteria.client != null) {
      this.nbFilter++;
      this.queryParams.unshift({
        // key: 'client_id',
        operation: 'GROUP_JOIN_EQUALITY',
        value: filterCriteria.client,
        criteriaType: 'DEFAULT',
        jointEntity: 'clients',
        jointAttribut: 'id',
        group: 0,
      });
      {
        // "operation": "JOIN_EQUALITY",
        // "value": "ID_DU_CLIENT_ICI",
        // "criteriaType": "CLIENT_ID",
        // "jointEntity": "clients",
        // "jointAttribut": "id",
        // "group": 0
      }
    }
    // site
    if (filterCriteria.site != null) {
      this.nbFilter++;
      this.queryParams.unshift({
        key: 'site',
        operation: 'GROUP_JOIN_EQUALITY',
        value: filterCriteria.driver,
        criteriaType: 'DEFAULT',
        jointEntity: 'site',
        jointAttribut: 'id',
        group: 0,
      });
    }
    // Dates
    if (filterCriteria.date) {
      

      if (filterCriteria.date?.startDay) {
        this.nbFilter++;
       this. queryParams.unshift({
          key: 'createdAt',
          operation: 'GREATER_THAN',
          value: filterCriteria.date.startDay,
          criteriaType: 'DEFAULT',
          group: 0,
        });
      }
      if (filterCriteria.date?.endDay) {
        this.queryParams.unshift({
          key: 'createdAt',
          operation: 'LESS_THAN',
          value: filterCriteria.date.endDay,
          criteriaType: 'DEFAULT',
          group: 0,
        });
      }
    }
   
    if (filterCriteria.activity) {
      this.nbFilter++;
     this. queryParams.unshift({
        key: 'type',
        operation: 'EQUALITY',
        value: filterCriteria.activity,
        criteriaType: 'PLANIFICATION_TYPE',
        group: 0,
      });
    }

    if (filterCriteria.lastName) {
      searchParams.push({ "key": "name", "operation": "CONTAINS", "value": '*' + filterCriteria.lastName + '*', "criteriaType": "DEFAULT", "group": 1 });
      searchParams.push({ "key": "phoneNumber", "operation": "CONTAINS", "value": '*' + filterCriteria.lastName +'*', "criteriaType": "DEFAULT", "group": 1 });
    }
   
this.queryParams = [...searchParams, ...this.queryParams]; 
    this.initData();
 
  }


  

  edit(item: any) {
    const modalRef = this.modalService.open(DoctorModalComponent, {
      size: 'xl',
      centered: true,
      scrollable: true,
    });
    modalRef.componentInstance.data = item;
    modalRef.componentInstance.type = 'edit';
    modalRef.result
      .then(() => {
        this.initData();
      }) 
      .catch((reason: any) => {
        console.log(reason);
      });
  }
  export() { }
 

  


}
