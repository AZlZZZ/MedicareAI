import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';
import { FormsModule } from '@angular/forms';
import { MedicamentService } from '../../../../shared/services/medicament.service';
import { ToasterService } from '../../../../shared/services/toaster.service';
import { StorageService } from '../../../../shared/services/storage.service';
import { MedicamentModalComponent } from '../medicament-modal/medicament-modal.component';
import { LoaderComponent } from '../../../../shared/loader/loader.component';
import { BtnFlottantComponent } from '../../../../shared/btn-flottant/btn-flottant.component';
import { ConfirmModalComponent } from '../../../../shared/confirm-modal/confirm-modal.component';

@Component({
  selector: 'app-medicament-list',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    FormsModule,
    LoaderComponent,
    BtnFlottantComponent
  ],
  templateUrl: './medicament-list.component.html',
  styleUrl: './medicament-list.component.css'
})
export class MedicamentListComponent implements OnInit {
  private api = inject(MedicamentService);
  private modalService = inject(NgbModal);
  private toastr = inject(ToasterService);
  private storage = inject(StorageService);

  data: any[] = [];
  filtered: any[] = [];
  loading = true;
  searchQuery = '';

  // inline stock editing
  stockLoadingId: number | null = null;

  get isAgent(): boolean {
    return this.storage.getUserType() === 'AGENT';
  }

  ngOnInit() { this.loadData(); }

  loadData() {
    this.loading = true;
    this.api.getAll().subscribe({
      next: (res: any) => {
        this.loading = false;
        if (res.code === 200) {
          this.data = res.data || [];
          this.applyFilter();
        }
      },
      error: () => { this.loading = false; this.toastr.showError('ERRORSERVENU'); }
    });
  }

  applyFilter() {
    const q = this.searchQuery.toLowerCase().trim();
    this.filtered = q
      ? this.data.filter(m => m.name?.toLowerCase().includes(q) || m.description?.toLowerCase().includes(q))
      : [...this.data];
  }

  add() {
    if (!this.isAgent) return;
    const ref = this.modalService.open(MedicamentModalComponent, { size: 'lg', centered: true });
    ref.componentInstance.type = 'add';
    ref.result.then(() => this.loadData()).catch(() => {});
  }

  edit(item: any) {
    if (!this.isAgent) return;
    const ref = this.modalService.open(MedicamentModalComponent, { size: 'lg', centered: true });
    ref.componentInstance.type = 'edit';
    ref.componentInstance.data = item;
    ref.result.then(() => this.loadData()).catch(() => {});
  }

  delete(item: any) {
    if (!this.isAgent) return;
    const ref = this.modalService.open(ConfirmModalComponent, { centered: true });
    ref.result.then(() => {
      this.api.delete(item.id).subscribe({
        next: () => { this.toastr.showSuccess('SUCCESS'); this.loadData(); },
        error: () => this.toastr.showError('ERROR')
      });
    }).catch(() => {});
  }

  changeStock(item: any, delta: number) {
    const newStock = (item.stock ?? 0) + delta;
    if (newStock < 0) return;
    this.stockLoadingId = item.id;
    this.api.updateStock(item.id, newStock).subscribe({
      next: (res: any) => {
        this.stockLoadingId = null;
        if (res.code === 200) {
          item.stock = res.data.stock;
        }
      },
      error: () => { this.stockLoadingId = null; this.toastr.showError('ERROR'); }
    });
  }
}
