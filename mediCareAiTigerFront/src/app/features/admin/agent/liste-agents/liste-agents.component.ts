import {
  Component,
  ElementRef,
  HostListener,
  inject,
  Injector,
  Input,
  OnInit,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmModalComponent } from '../../../../shared/confirm-modal/confirm-modal.component';
import { AgentService } from '../../../../shared/services/agent.service';
import { ProfileComponent } from '../profile/profile.component';
import { AgentModalComponent } from '../agent-modal/agent-modal.component';
import { StorageService } from '../../../../shared/services/storage.service';
import { ExportCSVService } from '../../../../shared/services/export-csv.service';
import { ToasterService } from '../../../../shared/services/toaster.service';
import { CommonModule } from '@angular/common';
import { BtnExportComponent } from "../../../../shared/btn-export/btn-export.component";
import { FilterTemplateComponent } from "../../../../shared/filter-template/filter-template.component";
import { ReusableTableComponent } from "../../../../shared/reusable-table/reusable-table.component";
import { BtnFlottantComponent } from "../../../../shared/btn-flottant/btn-flottant.component";
@Component({
  selector: 'app-liste-agents',
  templateUrl: './liste-agents.component.html',
  styles: `` ,
  standalone :true , 
  imports: [TranslateModule, CommonModule, BtnExportComponent, FilterTemplateComponent, ReusableTableComponent, BtnFlottantComponent]
})
export class ListeAgentsComponent {
  loadingExport: boolean = false;
   downloadService = inject(ExportCSVService);
  checkRef: Boolean = false;
  filterCollapse = false;

  toggleCollapse() {
    this.filterCollapse = !this.filterCollapse;
  }
  queryParams: any[] = [
    // {
    //   key: 'state',
    //   operation: 'NEGATION',
    //   value: 'DELETED',
    //   criteriaType: 'USER_STATE',
    // },
  ];
  //permissionStore = inject(PermissionStore);
  private modalService = inject(NgbModal);
  goldenDealersListe = [];
  displayedColumns: string[] = [];
  dataColumns: string[] = [];
  nbFilter: any = 0;
  dataSource = [];
  prefixe: string ='AGE-';

  // Pagination parameters
  page = 1;
  pageSize = 10;
  collectionSize = 0;
  maxSize = 5;
  loading: boolean = false;
  Agents: any[] = [];
  numberOfAgent!: number;
  totalPage!: number;
  entrepriseId!: any;
    stoarge = inject(StorageService);
  private injector = inject(Injector);
  hasPermissionAdd: boolean = false;
  constructor(private api: AgentService, private toastr: ToasterService) {
    // this.entrepriseId = 1;
     this.entrepriseId = this.stoarge.getEntrepriseID();
  }

  ngOnInit(): void {
    this.initColumns();
    this.initData();
    // runInInjectionContext(this.injector, () => {
    //   effect(() => {
    //     if (this.permissionStore.permissions().length > 0) {
    //       this.hasPermissionAdd = this.permissionStore.checkPermission(
    //         'AGENT_MANAGEMENT',
    //         'CREATE_AGENT'
    //       );

    //     }
    //   });
    // });
  }

  initData() {
    // if (this.entrepriseId != 1) {
    //   const queryParams = [
        
    //   ];
    //   this.queryParams = queryParams;
    // }

    this.getagentList(this.page, this.pageSize, this.queryParams);

  }
  initColumns() {
    if (this.entrepriseId === '1') {
      this.displayedColumns = [
        'TABLE.REF',
        'TABLE.NAME',
        'TABLE.GOLDEN',
        'TABLE.REGION',
        'TABLE.ROLE',
        'TABLE.ACTION'
      ];
      this.dataColumns = ['id', 'Agent', 'partner', 'role', 'Action'];
    } else {
      this.displayedColumns = [
        'TABLE.REF',
        'TABLE.NAME',
        'TABLE.ROLE','TABLE.ACTION'
      ];
      this.dataColumns = ['id', 'Agent', 'role', 'Action'];
    }
  }
  //Fonction pour récupérer la liste des agent
  getagentList(pageIndex: any, numberPerPage: any, queryvalue: any) {
    this.loading = true;

    let obj = {
      pageIndex: pageIndex - 1,
      numberPerPage: numberPerPage,
      queryValue: JSON.stringify(this.queryParams),
      sortDirection: 'DESC',
      sortProperty: 'id',
    };
    if (!this.checkRef) {
      this.api.getAllAgent(obj).subscribe((res: any) => {
        this.updateAgentsData(res);
        this.prefixe = 'AGE-';
      });
    } else {
      this.updateAgentsData(null);
    }
  }

  updateAgentsData(res: any) {
    this.loading = false;
    this.Agents = res.data.content;    
  
    // this.Agents = agents.filter((u : any) => u.id != this.permissionStore.idAgent());
    this.dataSource = res?.data?.content || [];
    this.collectionSize = res?.data?.length || 0;
    this.numberOfAgent = res.data.totalElements || 0;
    this.totalPage = res?.data?.totalElements || 0;
  }

  // redirection vers modal  profile agent
  details(item: any) {
    const modalRef = this.modalService.open(ProfileComponent, {
      size: 'xl',
      centered: true,
      scrollable: true
    });
    modalRef.componentInstance.data = item;
    modalRef.componentInstance.onSaveChanges.subscribe(() => {
      this.initData();
    });
    modalRef.result.then((result) => { }).catch((reason) => { });
  }

  changePageSize(event: any) {
    this.page = event;
    this.initData();
  }
  changeSize(event: any) {
    this.pageSize = event;
    this.initData();
  }
  /**
   * Ouvre le formulaire d’ajout d’un nouvel agent.
   */
  add() {
    const modalRef = this.modalService.open(AgentModalComponent, {
      size: 'xl',
      centered: true,
      scrollable: true,
    });
    modalRef.result
      .then((result) => {
        this.initData();
      })
      .catch((reason) => { });
  }

  //Ouvre le formulaire pour  modifier un agent
  edit(item: any) {
    const modalRef = this.modalService.open(AgentModalComponent, {
      size: 'xl',
      centered: true,
      scrollable: true,
    });
    modalRef.componentInstance.data = item;
    modalRef.result
      .then((result) => {
        this.initData();
      })
      .catch((reason) => { });
  }

  //supprimer un agent
  delete(item: any) {
    // const modalRef = this.modalService.open(ConfirmModalComponent, {
    // });
    // modalRef.componentInstance.modalType = 'agent';
    // modalRef.result
    //   .then((result) => {
    //     this.api.delete(item.id).subscribe(
    //       (resp: any) => {
    //         if (resp.code === 200) {
    //           this.initData();
    //         } else {
    //           this.toastr.showError(resp.msg);
    //         }
    //       },
    //       (err: any) => {
    //         this.toastr.showError('ERROR');
    //       }
    //     );
    //   })
    //   .catch((reason) => { });
  }
  /**
   * Met à jour les filtres appliqués sur la liste des agents à partir du formulaire de filtrage.
   * */
  changeFilter(event: any) {
    this.page = 1;
    this.nbFilter = 0;
    this.checkRef = false;
    const queryParams: any[] = [
      {
        key: 'state',
        operation: 'NEGATION',
        value: 'DELETED',
        criteriaType: 'USER_STATE',
      },
    ];
    if (this.entrepriseId != 1) {
      queryParams.push({
        key: 'entreprise_id',
        operation: 'GROUP_JOIN_EQUALITY',
        value: this.entrepriseId,
        criteriaType: 'DEFAULT',
        jointEntity: 'entreprise',
        jointAttribut: 'id',
      });
    }
    if (event.golden?.id != undefined) {
      event.golden = event.golden?.id;
    }
    0;
    if (event?.golden) {
      queryParams.push({
        key: 'entreprise_id',
        operation: 'GROUP_JOIN_EQUALITY',
        value: event?.golden,
        criteriaType: 'DEFAULT',
        jointEntity: 'entreprise',
        jointAttribut: 'id',
      });
      this.nbFilter++;
    }
    // Add   filter par  REF si s exist
    if (event?.ref?.startsWith('AGE-') && event?.ref.length > 4) {
      queryParams.push({
        key: 'id',
        operation: 'EQUALITY',
        value: event.ref.split('AGE-')[1].trim(),
        // orPredicate: true,
      });
      this.nbFilter++;
    } else if (event?.ref?.length === 0 || !event?.ref) {
      this.checkRef = false;
    } else {
      this.checkRef = true;
    }
    // Add  phone filters if they exist
    if (event.phone) {
      if (event.phone.length == 1) {
        queryParams.push({
          key: 'phoneNumber',
          operation: 'CONTAINS',
          value: '*' + event.phone + '*',
          // orPredicate: true,
        });
      } else {
        queryParams.push({
          key: 'phoneNumber',
          operation: 'CONTAINS',
          value: event.phone,
          // orPredicate: true,
        });
      }
      this.nbFilter++;
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
      } else {
        queryParams.push({
          key: 'lastName',
          operation: 'CONTAINS',
          value: event.lastName,
          // orPredicate: true,
        });
        queryParams.push({
          key: 'firstName',
          operation: 'CONTAINS',
          value: event.lastName,
          orPredicate: true,
        });
      }
    }
    //  console.log(stat);
    if (event.status) {
      queryParams.push({
        key: 'state',
        operation: 'EQUALITY',
        value: event.status,
        criteriaType: 'USER_STATE',
        // orPredicate: true,
      });
      this.nbFilter++;
    }
    this.queryParams = queryParams;
    this.loading = true;
    this.getagentList(this.page, this.pageSize, this.queryParams);
  }
  // Exporter Liste des agents en CSV
  export() { 
    this.loadingExport = true;
     let obj ={
      queryValue: JSON.stringify([] ),
      // queryValue: JSON.stringify(this.queryParams ),
    } 
    this.api.export(obj).subscribe({
      next: (response) => {
        this.loadingExport = false;
        this.downloadService.downloadCSV(response.body!,"Liste_agents" );
    
      },
      error: (err) => {
        this.loadingExport = false;
      //  this.toastr.showError('ERRORSERVENU');
      }
    });
  }
  actionsPermissions = {
    details: { feature: 'AGENT_MANAGEMENT', subFeature: 'DETAILS_AGENT' },
    edit: { feature: 'AGENT_MANAGEMENT', subFeature: 'UPDATE_AGENT' },
    delete: { feature: 'AGENT_MANAGEMENT', subFeature: 'DELETE_AGENT' }
  };



}
