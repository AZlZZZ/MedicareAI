import { Component, inject, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';
import { ScheduleService } from '../../../../shared/services/schedule.service';
import { ToasterService } from '../../../../shared/services/toaster.service';

const DAYS = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];

@Component({
  selector: 'app-schedule-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './schedule-modal.component.html',
})
export class ScheduleModalComponent implements OnInit {
  @Input() patientId!: number;
  @Input() medicaments: any[] = [];
  @Input() schedule: any = null; // null = create, object = edit

  modal = inject(NgbActiveModal);
  private api = inject(ScheduleService);
  private toastr = inject(ToasterService);

  days = DAYS;
  loading = false;
  submitted = false;

  form: any = {
    medicamentId: null,
    dose: '',
    startDate: '',
    endDate: '',
    alertBeforeMinutes: 30,
    daysOfWeek: [] as string[],
    timesOfDay: ['08:00'],
  };

  ngOnInit() {
    if (this.schedule) {
      this.form = {
        medicamentId: this.schedule.medicamentId,
        dose: this.schedule.dose,
        startDate: this.toDateString(this.schedule.startDate),
        endDate: this.schedule.endDate ? this.toDateString(this.schedule.endDate) : '',
        alertBeforeMinutes: this.schedule.alertBeforeMinutes,
        daysOfWeek: [...this.schedule.daysOfWeek],
        timesOfDay: [...this.schedule.timesOfDay],
      };
    }
  }

  /** Handles both "yyyy-MM-dd" strings and [yyyy, M, d] arrays from Java LocalDate */
  toDateString(val: any): string {
    if (!val) return '';
    if (Array.isArray(val)) {
      const [y, m, d] = val;
      return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
    }
    return val;
  }

  toggleDay(day: string) {
    const idx = this.form.daysOfWeek.indexOf(day);
    if (idx >= 0) this.form.daysOfWeek.splice(idx, 1);
    else this.form.daysOfWeek.push(day);
  }

  addTime() { this.form.timesOfDay.push('08:00'); }
  removeTime(i: number) { if (this.form.timesOfDay.length > 1) this.form.timesOfDay.splice(i, 1); }

  today = new Date().toISOString().split('T')[0]; // "yyyy-MM-dd" min for date inputs

  get endDateMin(): string {
    return this.form.startDate || this.today;
  }

  get startDateError(): string | null {
    if (!this.form.startDate) return 'required';
    // Only enforce future date on new schedules, not when editing existing ones
    if (!this.schedule && this.form.startDate < this.today) return 'past';
    return null;
  }

  get endDateError(): string | null {
    if (!this.form.endDate) return 'required';
    if (this.form.endDate < this.form.startDate) return 'before_start';
    return null;
  }

  get isValid(): boolean {
    return !!this.form.medicamentId && !!this.form.dose.trim() &&
      !this.startDateError && !this.endDateError &&
      this.form.daysOfWeek.length > 0 &&
      this.form.timesOfDay.length > 0 && this.form.alertBeforeMinutes >= 0;
  }

  save() {
    this.submitted = true;
    if (!this.isValid) return;
    this.loading = true;
    const dto = {
      medicamentId: this.form.medicamentId,
      dose: this.form.dose,
      startDate: this.form.startDate,
      endDate: this.form.endDate,
      alertBeforeMinutes: this.form.alertBeforeMinutes,
      daysOfWeek: this.form.daysOfWeek,
      timesOfDay: this.form.timesOfDay,
    };

    const call = this.schedule
      ? this.api.editSchedule(this.schedule.id, dto)
      : this.api.createSchedule(this.patientId, dto);

    call.subscribe({
      next: (res: any) => {
        this.loading = false;
        if (res.code === 200) { this.toastr.showSuccess('SUCCESS'); this.modal.close(true); }
      },
      error: () => { this.loading = false; this.toastr.showError('ERROR'); }
    });
  }
}
