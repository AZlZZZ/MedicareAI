import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbDate, NgbDatepicker, NgbDatepickerMonth, NgbDateStruct, NgbTypeaheadModule, NgbTypeaheadSelectItemEvent } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';
import { debounceTime, distinctUntilChanged, Observable, OperatorFunction, switchMap } from 'rxjs';
import { StorageService } from '../services/storage.service';

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule,
    NgbDatepicker,
    NgbTypeaheadModule,
    NgbDatepickerMonth,],
  templateUrl: './filter.component.html',
  styleUrl: './filter.component.css'
})
export class FilterComponent {
  @Input() type: any;
  @Input() golden : any 
  // selectedStatus: { id: number; value: string } | null = null;

  selectedStatuses: { id: number; value: string }[] = [];
  @Output() formValuesChanged = new EventEmitter<any>();
  statusButtonsVisible: boolean = false;
  selectedGolden!: any;
  form: FormGroup;
  list_stats: any[] = [
    { id: 1, value: 'BLOCKED' },
    { id: 2, value: 'ARCHIVED' },
    { id: 3, value: 'ENABLED' },
  ];

  selectedType: string = 'ref';

  other: string = 'OTHER';
  placeholder = 'LASTFIRSTNAME';
  selectedDateType: 'day' | 'week' | 'month' | 'year' = 'day';
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
  entrepriseId : any 
  constructor(private fb: FormBuilder,    private storageService: StorageService ) {
    this.entrepriseId = this.storageService.getEntrepriseID();
    // Initialisation du formulaire
    this.form = this.fb.group({
      ref: new FormControl(''),
      golden: new FormControl(''),
      // firstName: new FormControl(''),
      lastName: new FormControl(''),
      // userName: new FormControl(''),
      status: new FormControl(''),
      phone: new FormControl(''),
      date: this.fb.group({
        // Nested FormGroup for date
        type: this.fb.control('day'), // FormControl for date type ('day', 'week', 'month')
        startDay: this.fb.control(''), // FormControl for start date
        endDay: this.fb.control(''), // FormControl for end date
        year: this.fb.control(''), // FormControl for end date
        week: this.fb.control(''), // FormControl for end date
        month: this.fb.control(''), // FormControl for end date
        day: this.fb.control(''), // FormControl for end date
      }),
    });
  }
  years: any[] = [];
  selectedYear: number | null = null;

  selectedDate: string = '';
  selectedValue: string = '';

  monthsColumns: any[][] = [];
  selectedMonth: any | null = null;

  startDate!: string;
  endDate!: string;

  selectedWeek: Date[] = [];

  ngOnInit() {
    // console.log(this.type);
    if (this.type === 'DRIVERS') {
      this.list_stats = [
        { id: 1, value: 'ON_HOLD' },
        { id: 2, value: 'IN_SERVICE' },
        { id: 3, value: 'BUSY' },
      ];
    } else if (this.type === 'CLIENTS' || this.type === 'AGENTS') {
      this.list_stats = [
        { id: 1, value: 'BLOCKED' },
        { id: 2, value: 'ARCHIVED' },
        { id: 3, value: 'ENABLED' },
      ];
    } else if (this.type === 'CARS') {
      this.list_stats = [
        { id: 1, value: 'USED_CAR' },
        { id: 2, value: 'UNUSED_CAR' },
      ];
    } else if (this.type === 'PROMO') {
      this.placeholder = 'CODE';
      this.other = 'CODE';
      this.list_stats = [
        { id: 1, value: 'ENABLED' },
        { id: 2, value: 'DISABLED' },
        { id: 3, value: 'EXPIRED' },
      ];
    } else if (this.type === 'TRAGETS') {
      this.placeholder = 'NAMEPATH';
      this.other = 'NAME';
      this.list_stats = [
        { id: 1, value: 'ENABLED' },
        { id: 2, value: 'DISABLED' },
      ];
    } else if (this.type === 'GOLDEN') {
      this.placeholder = 'NAMERESP';
      this.other = 'RESGOLDEN';
    } else if ( this.type=="REGION"){
      this.other = 'REGIONNAME'
      this.placeholder="REGNAME"

    }
    this.generateYears();
  }
  // Méthode appelée lors de la sélection d'une année
  selectYear(event: any): void {
    this.selectedYear = event!.target!.value;
    this.monthsColumns = [];
    this.generateColumns(); // Régénère les colonnes si nécessaire

    this.form.get('date.year')?.setValue(Number(this.selectedYear));
    this.selectedMonth = null;

    if (this.type === 'CHART' && this.form.get('date.type')?.value === 'year') {
      // Emit the updated form value
      this.formValuesChanged.emit(this.form.value);
    }
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

  selectMonth(month: any): void {
    this.selectedMonth = month;
    // console.log(month, this.selectedYear);
    const startOfMonth = new Date(Number(this.selectedYear), month.id - 1, 1);
    const endOfMonth = new Date(Number(this.selectedYear), month.id, 0); // Last day of the month
    // const formatDate = (d: Date) =>
    //   `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} 00:00:00`;

    this.startDate = this.formatDate(startOfMonth);
    this.endDate = this.formatDate(endOfMonth);

    this.selectedValue = `Month: { type: 'month', startDay: ${this.startDate}, endDay: ${this.endDate} }`;
    // console.log(this.selectedValue);
    // Update form controls
    if (this.type === 'CHART') {
      //  console.log( "change month " ,month );

      this.form.get('date.month')?.setValue(month.id);
    } else {
      this.form.get('date.startDay')?.setValue(this.startDate);
      this.form.get('date.endDay')?.setValue(this.endDate);
    }
    // Emit the updated form value
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

    //  console.log( "change month " ,month );

    this.form.get('date.week')?.setValue(weekNumber);
    this.form.get('date.year')?.setValue(year);
    //  } else
    // {
    this.form.get('date.startDay')?.setValue(this.startDate);
    this.form.get('date.endDay')?.setValue(this.endDate);

    console.log(this.form.value);

    // Emit the updated form value
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
    const weekNumber = Math.floor(diff / (7 * 24 * 60 * 60 * 1000)) + 1;

    // Return the ISO year and week number
    return { weekNumber, year: tempDate.getFullYear() };
  }
  isSelectedDate(date: any): boolean {
    const dateObj = date instanceof Date ? date : this.convertToDate(date); // Convertir en Date si nécessaire
    return this.selectedWeek.some(
      (d) => d.toDateString() === dateObj.toDateString()
    );
  }
  // Convertir NgbDate en JavaScript Date
  convertToDate(ngbDate: NgbDate): Date {
    return new Date(ngbDate.year, ngbDate.month - 1, ngbDate.day);
  }
  onDateChange(date: NgbDateStruct | Event | null) {
    if (!date || !(date as NgbDateStruct).year) return;

    const { year, month, day } = date as NgbDateStruct;
    const selectedDate = new Date(year, month - 1, day);

    // console.log (' selectedDate ' , selectedDate )
    // Format for 'YYYY-MM-DD'

    if (this.selectedDateType === 'day') {
      // For 'day', startDay and endDay are the same
      const nextDay = new Date(selectedDate);
      nextDay.setDate(selectedDate.getDate() + 1);
      if (this.type === 'CHART') {
        this.form.get('date.day')?.setValue(this.formatDate1(selectedDate));
        this.form.get('date.year')?.setValue(Number(year));
      } else {
        this.startDate = this.formatDate(selectedDate);
        this.endDate = this.formatDate(nextDay);
        this.form.get('date.startDay')?.setValue(this.startDate);
        this.form.get('date.endDay')?.setValue(this.endDate);
        // this.selectedValue = `Day: { type: 'day', startDay: ${this.startDate}, endDay: ${this.endDate} }`;
      }
    }
     this.formValuesChanged.emit(this.form.value);
  }
  generateColumns(): void {
    const columnCount = 4;
    for (let i = 0; i < this.months.length; i += columnCount) {
      this.monthsColumns.push(this.months.slice(i, i + columnCount));
    }
    // console.log(this.monthsColumns);
  }
  onDateTypeChange(type: 'day' | 'week' | 'month' | 'year'): void {
    this.selectedDateType = type;
  
    this.form.reset();

    this.form.get('date.type')?.setValue(type);
  }

  onStatusChange(item: { id: number; value: string }): void {
     if(this.type =="CARS" ||  this.type === 'DRIVERS' ){
      this.selectedStatuses = [item];
  
    // Met à jour le champ du formulaire avec la nouvelle valeur sélectionnée
    this.form.get('status')?.setValue(item.value);
     } else {
      const index = this.selectedStatuses.findIndex(
        (status) => status.id === item.id
      );
  
      if (index > -1) {
        // If the status is already selected, remove it
        this.selectedStatuses.splice(index, 1);
      } else {
        // Otherwise, add it to the selected statuses
        this.selectedStatuses.push(item);
      }
  
      // Update the form control with the selected statuses' values
      this.form
        .get('status')
        ?.setValue(this.selectedStatuses.map((status) => status.value));
     }
   

    this.formValuesChanged.emit(this.form.value);

    // console.log('Selected Statuses:', this.selectedStatuses);
  }

  generateYears(): void {
    const currentYear = new Date().getFullYear();
  this.years = []; // Ensure the array is empty before filling
  for (let year = currentYear; year >= 2000; year--) {
    this.years.push(year);
  } 
  }
  onTypeChange(type: string) {
    this.selectedType = type;
  }
  getButtonClass(status: string): string {
    const isSelected = this.selectedStatuses.some((s) => s.value === status);

    switch (status) {
      case 'ARCHIVED':
      case 'BUSY':
      case 'EXPIRED':
        return isSelected ? 'btn-warning' : 'btn-outline-warning'; // Active and inactive styles
      case 'BLOCKED':
      case 'ON_HOLD':
      case 'UNUSED_CAR':
      case 'DISABLED':
        return isSelected ? 'btn-danger' : 'btn-outline-danger';
      default:
        return isSelected ? 'btn-success' : 'btn-outline-success';
    }
  }
  toggleStatusButtons(): void {
    this.statusButtonsVisible = !this.statusButtonsVisible;
    this.selectedType = 'state';
  }
  onChange(property: string): void {
    // console.log(`La propriété ${property} de l'élément a été modifiée :`);

    // this.form.get('status')?.setValue(this.selectedStatuses.map((status) => status.value));
    // if(this.form.value &&  this.form.value.lastName?.length >2  ){
    this.formValuesChanged.emit(this.form.value);
    // }
    // Vous pouvez ajouter ici des validations ou des appels à un service pour sauvegarder les modifications.
  }
  cleanDate() {
    // console.log('test clean ');
    
    // this.selectedType=''
   
    if (this.selectedType == 'date') {
      this.selectedYear = null;
      this.selectedMonth = null;
      this.selectedWeek = [];
      this.selectedDate = '';
      this.form.get('date')?.reset();
    } else if (this.selectedType == 'ref') {
      this.form.get('ref')?.reset();
    } else if (this.selectedType == 'phone') {
      this.form.get('phone')?.reset();
    } else if (this.selectedType == 'state') {
      this.selectedStatuses = [];
      this.form.get('status')?.reset();
    } else if (this.selectedType == 'other') {

      this.form.get('lastName')?.reset();
    } else if (this.selectedType == 'golden') {
      this.form.get('golden')?.reset();
    }

    this.formValuesChanged.emit(this.form.value);
  }
  currentIndex: any = 0;
  next(): void {
    this.currentIndex = (this.currentIndex + 1) % this.years.length;
    this.monthsColumns = [];
    this.generateColumns(); // Régénère les colonnes si nécessaire
    this.selectedYear = this.years[this.currentIndex];
    this.form.get('date.year')?.setValue(Number(this.years[this.currentIndex]));
    this.selectedMonth = null;

    if (this.type === 'CHART' && this.form.get('date.type')?.value === 'year') {
      // Emit the updated form value
      this.formValuesChanged.emit(this.form.value);
    }
  }

  previous(): void {
    this.currentIndex =
      (this.currentIndex - 1 + this.years.length) % this.years.length;
    this.monthsColumns = [];
    this.generateColumns();
    this.selectedYear = this.years[this.currentIndex];
    this.form.get('date.year')?.setValue(Number(this.years[this.currentIndex]));
    this.selectedMonth = null;

    if (this.type === 'CHART' && this.form.get('date.type')?.value === 'year') {
      // Emit the updated form value
      this.formValuesChanged.emit(this.form.value);
    }
  }
 

}

