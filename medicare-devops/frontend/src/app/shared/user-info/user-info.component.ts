import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ClientService } from '../services/client.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { environment } from '../../../environments/environment.development';
import { CopyComponent } from '../copy/copy.component';
import { BlockModalComponent } from '../../features/admin/Client/block-modal/block-modal.component';
import { ToasterService } from '../services/toaster.service';
import { PatientModalComponent } from '../../features/admin/Client/patient-modal/patient-modal.component';

type UserType = 'CLIENT' | 'PARTNER' | 'AGENT' | 'INVOICE' | 'INVOICE_PARTNER'; 
@Component({
  selector: 'app-user-info',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    NgbModule,
    FormsModule,
    CopyComponent,
  ],
  templateUrl: './user-info.component.html',
  styleUrl: './user-info.component.css',
})

export class UserInfoComponent implements OnChanges {
@Input() data: any;
  @Input() type: UserType = 'CLIENT';
  @Input() historicValue: number = 0;
  @Input() rate_user: number | null = null;
  @Output() saveChanges = new EventEmitter<string>();

  private modalService = inject(NgbModal);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private translate = inject(TranslateService);
  private toastr = inject(ToasterService);
  
  // Services
  private clientService = inject(ClientService);

  id!: number;
  url = environment.urlFiles;
  selectedColor: string = '#9B9B9B';
  tooltip: string = '';

  // Configuration centralisée
  private readonly TYPE_CONFIG: Record<string, any> = {
    CLIENT: { prefix: 'CLI', route: '/backoffice/clients', modal: PatientModalComponent },
    AGENT:   { prefix: 'AGT', route: '/backoffice/agents',  modal: null },
  };

  ngOnInit(): void {
    this.tooltip = this.translate.instant('COPYID');
    this.route.params.subscribe(params => this.id = +params['id']);
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.updateStatusColor();
  }

  private updateStatusColor(): void {
    const colors: Record<string, string> = {
      'BLOCKED': '#E4626F',
      'DISACTIVE': '#9B9B9B',
      'ENABLED': 'var(--Success-Success200, rgba(21, 176, 151, 1))'
    };
    this.selectedColor = colors[this.data?.state] || '#9B9B9B';
  }

  onEditUser() {
    const config = this.TYPE_CONFIG[this.type];
    if (config?.modal) {
      this.openGenericModal(config.modal, { type: 'edit', data: this.data, size: 'xl' });
    }
  }

  openGenericModal(component: any, options: any) {
    const modalRef = this.modalService.open(component, {
      size: options.size || 'lg',
      centered: true,
      scrollable: true,
    });

    modalRef.componentInstance.data = options.data;
    if (options.type) modalRef.componentInstance.modalType = options.type;

    modalRef.result.then(() => this.saveChanges.emit('saved')).catch(() => {});
  }

  goBack() {
    const route = this.TYPE_CONFIG[this.type]?.route || '/preinvoices';
    this.router.navigateByUrl(route);
  }

  onBlockToggle() {
    if (this.data?.state === 'BLOCKED') {
      this.executeAction({ id: this.id, state: 'ENABLED' }, 'UNBLOCK');
    } else {
      const modalRef = this.modalService.open(BlockModalComponent, { size: 'lg', centered: true });
      modalRef.componentInstance.data = this.data;
      modalRef.result.then((result) => {
        if (result.state === 'Valid') {
          const params = {
            id: this.id,
            nbrJour: result.data?.duration,
            state: 'BLOCKED',
            blockCause: result.data.cause
          };
          this.executeAction(params, 'BLOCK');
        }
      }).catch(() => {});
    }
  }

  private executeAction(params: any, action: 'BLOCK' | 'UNBLOCK') {
    let serviceCall$;

    if (this.type === 'CLIENT') serviceCall$ = this.clientService.BlockPatientAccount(params);
    else return;

    serviceCall$.subscribe({
      next: (res: any) => {
        if (res.code === 200) {
          this.toastr.showSuccess(this.translate.instant(`${this.type}_${action}_SUCCESS`));
          this.saveChanges.emit('success');
          if (action === 'UNBLOCK') this.updateStatusColor();
        }
      },
      error: () => this.toastr.showError('ERRORSERVENU')
    });
  }

  get idPrefix(): string {
    return this.TYPE_CONFIG[this.type]?.prefix || 'ID';
  }

  formatPhone(phone: string): string {
    if (!phone) return '';
    const digits = phone.replace(/\D/g, '');
    return digits.replace(/^(\d{2})(\d{3})(\d{3})$/, '$1 $2 $3');
  }
} 