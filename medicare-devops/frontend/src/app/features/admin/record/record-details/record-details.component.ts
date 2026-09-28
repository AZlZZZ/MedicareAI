import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RecordService } from '../../../../shared/services/record.service';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DiagnosticModalComponent } from '../diagnostic-modal/diagnostic-modal.component';

@Component({
  selector: 'app-record-details',
  standalone: true,
  imports: [CommonModule ],
  templateUrl: './record-details.component.html',
  styleUrl: './record-details.component.css'
})
export class RecordDetailsComponent implements OnInit  {
[x: string]: any;
 
 private route = inject(ActivatedRoute);
 private api = inject(RecordService);
   private modalService = inject(NgbModal);
 
  recordId!: number;
  // Pagination parameters
  page = 1;
  pageSize = 10;
  collectionSize = 0;
  numberOfItems: number = 0;
  totalPage: number = 2;

    queryParams: any[] = [];
  ngOnInit(): void {
   this.route.params.subscribe((params) => {
      this.recordId = +params['id'];
      this.queryParams.push(     {
              key: 'record',
              operation: 'GROUP_JOIN_EQUALITY',
              value: this.recordId,
              criteriaType: 'DEFAULT',
              jointEntity: 'record',
              jointAttribut: 'id',
            });
       let obj = {
      pageIndex: this.page - 1,
      numberPerPage: this.pageSize,
      queryValue: JSON.stringify(this.queryParams),
      sortDirection: 'DESC',
      sortProperty: 'id',
    }; 
this.api.getDiagnostics(obj).subscribe((res:any)=> {
  this.diagnostics = res?.data.content || [];
})
    });
  }

diagnostics: any[] = [
  // { id: 1, description: 'Diagnostic 1', severity: 'HIGH' },
  // { id: 2, description: 'Diagnostic 2', severity: 'MEDIUM' },
  // { id: 3, description: 'Diagnostic 3', severity: 'LOW' }
];
  getSeverityClass(severity: string) {
  return {
    'bg-danger': severity === 'HIGH',
    'bg-warning text-dark': severity === 'MEDIUM',
    'bg-info text-dark': severity === 'LOW'
  };
}
openAddDiagnosticModal(){
    const modalRef = this.modalService.open(DiagnosticModalComponent, {
      size: 'xl',
      scrollable: true,
      centered: true,
    });
    modalRef.componentInstance.recordId = this.recordId; // Passer l'ID du record au modal;
    modalRef.result
      .then(() => {
       
        // this.initData();
      })
      .catch(() => { });
  }
}
