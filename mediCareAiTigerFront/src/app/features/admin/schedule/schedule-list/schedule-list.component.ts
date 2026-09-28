import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ScheduleService } from '../../../../shared/services/schedule.service';
import { StorageService } from '../../../../shared/services/storage.service';
import { ToasterService } from '../../../../shared/services/toaster.service';
import { LoaderComponent } from '../../../../shared/loader/loader.component';
import { ScheduleModalComponent } from '../schedule-modal/schedule-modal.component';
import { ConfirmModalComponent } from '../../../../shared/confirm-modal/confirm-modal.component';

@Component({
  selector: 'app-schedule-list',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule, LoaderComponent, ConfirmModalComponent],
  templateUrl: './schedule-list.component.html',
  styleUrl: './schedule-list.component.css',
})
export class ScheduleListComponent implements OnInit {
  private api = inject(ScheduleService);
  private storage = inject(StorageService);
  private modalService = inject(NgbModal);
  private toastr = inject(ToasterService);

  userType: string | null = null;

  patients: any[] = [];
  selectedPatient: any = null;
  medicaments: any[] = [];
  schedules: any[] = [];
  loadingSchedules = false;

  mySchedules: any[] = [];
  alerts: any[] = [];
  unreadCount = 0;
  showAlerts = false;
  loadingAlerts = false;
  loading = true;

  get isDoctor(): boolean { return this.userType === 'DOCTOR' || this.userType === 'AGENT'; }
  get isPatient(): boolean { return this.userType === 'PATIENT'; }

  ngOnInit() {
    this.userType = this.storage.getUserType();
    if (this.isDoctor) this.loadDoctorData();
    else if (this.isPatient) this.loadPatientData();
  }

  loadDoctorData() {
    this.loading = true;
    this.api.getPatients().subscribe({
      next: (res: any) => {
        this.patients = res?.data?.content ?? [];
        this.api.getMedicaments().subscribe({
          next: (r: any) => { this.medicaments = r?.data ?? []; this.loading = false; },
          error: () => { this.loading = false; }
        });
      },
      error: () => { this.loading = false; }
    });
  }

  selectPatient(p: any) {
    this.selectedPatient = p;
    this.loadSchedules();
  }

  loadSchedules() {
    if (!this.selectedPatient) return;
    this.loadingSchedules = true;
    this.api.getSchedulesForPatient(this.selectedPatient.id).subscribe({
      next: (res: any) => { this.schedules = res?.data ?? []; this.loadingSchedules = false; },
      error: () => { this.loadingSchedules = false; }
    });
  }

  openAdd() {
    const ref = this.modalService.open(ScheduleModalComponent, { size: 'lg', centered: true });
    ref.componentInstance.patientId = this.selectedPatient.id;
    ref.componentInstance.medicaments = this.medicaments;
    ref.result.then(() => this.loadSchedules()).catch(() => {});
  }

  openEdit(s: any) {
    const ref = this.modalService.open(ScheduleModalComponent, { size: 'lg', centered: true });
    ref.componentInstance.patientId = this.selectedPatient.id;
    ref.componentInstance.medicaments = this.medicaments;
    ref.componentInstance.schedule = s;
    ref.result.then(() => this.loadSchedules()).catch(() => {});
  }

  deleteSchedule(s: any) {
    const ref = this.modalService.open(ConfirmModalComponent, { centered: true });
    ref.componentInstance.data = s;
    ref.result.then((result) => {
      if (result !== 'confirm') return;
      this.api.deleteSchedule(s.id).subscribe({
        next: () => { this.toastr.showSuccess('SUCCESS'); this.loadSchedules(); },
        error: () => this.toastr.showError('ERROR')
      });
    }).catch(() => {});
  }

  loadPatientData() {
    this.loading = true;
    this.api.getMySchedules().subscribe({
      next: (res: any) => {
        this.mySchedules = res?.data ?? [];
        this.loading = false;
        this.loadAlerts();
      },
      error: () => { this.loading = false; }
    });
  }

  loadAlerts() {
    this.loadingAlerts = true;
    this.api.getUpcomingAlerts(4).subscribe({
      next: (res: any) => {
        this.alerts = res?.data ?? [];
        this.unreadCount = this.alerts.filter((a: any) => a.status === 'UNREAD').length;
        this.loadingAlerts = false;
      },
      error: () => { this.loadingAlerts = false; }
    });
  }

  toggleAlerts() { this.showAlerts = !this.showAlerts; }

  markRead(alert: any) {
    if (alert.status === 'READ') return;
    this.api.markAlertRead(alert.id).subscribe({
      next: () => { alert.status = 'READ'; this.unreadCount = Math.max(0, this.unreadCount - 1); },
      error: () => {}
    });
  }

  formatDate(val: any): string {
    if (!val) return '';
    if (Array.isArray(val)) {
      const [y, m, d] = val;
      return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
    }
    return val;
  }

  formatDays(days: string[]): string {
    return days.map(d => d.slice(0, 3)).join(', ');
  }

  getMedName(schedule: any): string {
    const med = this.medicaments.find((m: any) => m.id === schedule.medicamentId);
    return med?.name ?? `#${schedule.medicamentId}`;
  }
}
