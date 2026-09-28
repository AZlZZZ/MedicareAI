import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import {
  NgbDate,
  NgbDatepicker,
  NgbDatepickerMonth,
  NgbDateStruct,
  NgbTypeaheadModule,
} from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-filter-date-template',
  templateUrl: './filter-date-template.component.html',
  styleUrl: './filter-date-template.component.css',
  styles: `
 `,
  standalone: true,
  imports: [
    NgbDatepicker,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgbTypeaheadModule,
    NgbDatepickerMonth,
    TranslateModule,
  ],
})
export class FilterDateTemplateComponent {
  containerWidth: string | null = null;
  @Input() type: any;
  @Output() formValuesChanged = new EventEmitter<any>();
  form: FormGroup;
  years: any[] = [];
  currentIndex = 0;
  monthsColumns: any[][] = [];
  selectedMonth: any | null = null;
  selectedDateType: 'day' | 'week' | 'month' | 'year' = 'day';
  selectedDate: any = '';
  selectedValue: string = '';
  selectedWeek: Date[] = [];
  startDate!: string;
  endDate!: string;
  months: any[] = [
    { id: 1, value: 'January' },
    { id: 2, value: 'February' },
    { id: 3, value: 'March' },
    { id: 4, value: 'April' },
    { id: 5, value: 'May' },
    { id: 6, value: 'June' },
    { id: 7, value: 'July' },
    { id: 8, value: 'August' },
    { id: 9, value: 'September' },
    { id: 10, value: 'October' },
    { id: 11, value: 'November' },
    { id: 12, value: 'December' },
  ];
  selectedYear: number | null = null;
  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      type: this.fb.control('day'), // FormControl for date type ('day', 'week', 'month')
      startDay: this.fb.control(''), // FormControl for start date
      endDay: this.fb.control(''), // FormControl for end date
      year: this.fb.control(''), // FormControl for end date
      week: this.fb.control(''), // FormControl for end date
      month: this.fb.control(''), // FormControl for end date
      day: this.fb.control(''), // FormControl for end date
    });
  }
  ngOnInit(): void {
    if (window.innerWidth <= 768) {
      this.containerWidth = `${window.innerWidth - 40}px`;
    }
  }
  onDateChange(date: NgbDateStruct | Event | null) {
    if (!date || !(date as NgbDateStruct).year) return;

    const { year, month, day } = date as NgbDateStruct;
    const selectedDate = new Date(year, month - 1, day);

    if (this.selectedDateType === 'day') {
      //init default
      this.selectedWeek = [];
      // For 'day', startDay and endDay are the same
      const nextDay = new Date(selectedDate);

      nextDay.setDate(selectedDate.getDate() + 1);
      if (this.type === 'CHART') {
        this.form.get('day')?.setValue(this.formatDate1(selectedDate));
        this.form.get('year')?.setValue(Number(year));
      } else {
        this.startDate = this.formatDate(selectedDate);
        this.endDate = this.formatDate(nextDay);
        this.form.get('startDay')?.setValue(this.startDate);
        this.form.get('endDay')?.setValue(this.endDate);
      }
    }
    this.formValuesChanged.emit(this.form.value);
  }
  // Méthode pour déterminer la semaine complète
  onWeekSelect(ngbDate: NgbDate): void {
    const selectedDate = this.convertToDate(ngbDate);
    const dayOfWeek = selectedDate.getDay();

    // Trouver le lundi de la semaine
    const diffToMonday =
      selectedDate.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1);
    const monday = new Date(selectedDate);
    monday.setDate(diffToMonday); // Utilisez une nouvelle instance pour modifier la date

    // Remplir la semaine (lundi à dimanche)
    this.selectedWeek = [];
    for (let i = 0; i < 7; i++) {
      const day = new Date(monday);
      day.setDate(monday.getDate() + i);
      this.selectedWeek.push(new Date(day)); // Crée une nouvelle date pour chaque jour
    }

    const startOfWeek = this.selectedWeek[0];

    // startOfWeek.setDate(selectedDate.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1)); // Monday as the start of the week
    const endOfWeek = this.selectedWeek[6];
    // endOfWeek.setDate(startOfWeek.getDate() + 6);

    this.startDate = this.formatDate(startOfWeek);

    this.endDate = this.formatDate(endOfWeek);
    const { weekNumber, year } = this.getISOWeekNumberAndYear(startOfWeek);
    // this.selectedValue = `Week: { type: 'week', startDay: ${this.startDate}, endDay: ${this.endDate} }`;

    if (this.type === 'CHART') {
      this.form.get('week')?.setValue(weekNumber);
      this.form.get('year')?.setValue(year);
   
    } 
   
      this.form.get('startDay')?.setValue(this.startDate);
      this.form.get('endDay')?.setValue(this.endDate);
    

    this.formValuesChanged.emit(this.form.value);
  }
  getISOWeekNumberAndYear(date: Date): { weekNumber: number; year: number } {
    const tempDate = new Date(date);
    tempDate.setHours(0, 0, 0, 0);

    // Set to Thursday in the current week to get the correct ISO year
    tempDate.setDate(tempDate.getDate() + 3 - (tempDate.getDay() || 7));

    // Get the first Thursday of the year
    const firstThursday = new Date(tempDate.getFullYear(), 0, 4);
    const diff = tempDate.getTime() - firstThursday.getTime();

    // Calculate the week number
    const weekNumber = Math.floor(diff / (7 * 24 * 60 * 60 * 1000)) + 2;

    // Return the ISO year and week number
    return { weekNumber, year: tempDate.getFullYear() };
  }
  isSelectedDate(date: any): boolean {
    const dateObj = date instanceof Date ? date : this.convertToDate(date); // Convertir en Date si nécessaire
    return this.selectedWeek.some(
      (d) => d.toDateString() === dateObj.toDateString()
    );
  }
  convertToDate(ngbDate: NgbDate): Date {
    return new Date(ngbDate.year, ngbDate.month - 1, ngbDate.day);
  }
  generateColumns(): void {
    const columnCount = 4;
    this.monthsColumns = [];
    for (let i = 0; i < this.months.length; i += columnCount) {
      this.monthsColumns.push(this.months.slice(i, i + columnCount));
    }
  }

  selectMonth(month: any): void {
    this.selectedMonth = month;
    const startOfMonth = new Date(Number(this.selectedYear), month.id - 1, 1);
    const endOfMonth = new Date(Number(this.selectedYear), month.id, 0); // Last day of the month

    this.startDate = this.formatDate(startOfMonth);
    this.endDate = this.formatDate(endOfMonth);

    // this.selectedValue = `Month: { type: 'month', startDay: ${this.startDate}, endDay: ${this.endDate} }`;

    if (this.type === 'CHART') {
      this.form.get('month')?.setValue(month.id);
    } else {
      this.form.get('startDay')?.setValue(this.startDate);
      this.form.get('endDay')?.setValue(this.endDate);
      //  this.formValuesChanged.emit(this.form.value);
    }

    // Emit the updated form value
     this.formValuesChanged.emit(this.form.value);
  }

  // Génère une liste des années, par exemple de 2000 à l'année courante
  generateYears(): void {
    this.selectedYear = new Date().getFullYear();
    this.form.get('year')?.setValue(this.selectedYear);
    this.years = []; // Ensure the array is empty before filling
    for (let year = this.selectedYear; year >= 2000; year--) {
      this.years.push(year);
    }
  }
  next(): void {
    this.currentIndex = (this.currentIndex + 1) % this.years.length;
    this.selectedYear = this.years[this.currentIndex];
    this.form.get('year')?.setValue(Number(this.years[this.currentIndex]));
    this.formValuesChanged.emit(this.form.value);
  }

  previous(): void {
    this.currentIndex =
      (this.currentIndex - 1 + this.years.length) % this.years.length;
    this.selectedYear = this.years[this.currentIndex];
    this.form.get('year')?.setValue(Number(this.years[this.currentIndex]));
    this.formValuesChanged.emit(this.form.value);
  }

  onDateTypeChange(type: 'day' | 'week' | 'month' | 'year'): void {
    this.selectedDateType = type;
    // this.form.reset();
    this.form.get('type')?.setValue(type);

    if (type === 'month') {
      this.generateYears();
      this.generateColumns();
      this.selectMonth({ id: 1, value: 'January' });
     
    } else if (type === 'year') {
      this.generateYears();
      this.formValuesChanged.emit(this.form.value);
    }

    // if (type === 'year' || type === 'month') {
    //   this.generateYears();
    //   // this.selectedDate = '';
    //   this.selectedWeek = [];
    //   if (type === 'month') {
    //     this.generateColumns();
    //     this.selectMonth({ id: 1, value: 'January' });
    //   }
    //   this.formValuesChanged.emit(this.form.value);
    // }
  }

  formatDate(d: Date) {
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(
      2,
      '0'
    )}-${String(d.getDate()).padStart(2, '0')} 00:00:00`;
  }
  formatDate1(d: Date) {
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(
      2,
      '0'
    )}-${String(d.getDate()).padStart(2, '0')}`;
  }
  resetForm() {
    this.form.reset();
    //  this.selectedDate = '';
    this.selectedWeek = [];
  }
}
