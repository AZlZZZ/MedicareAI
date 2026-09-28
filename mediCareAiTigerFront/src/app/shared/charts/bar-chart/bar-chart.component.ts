import { Component, Input, SimpleChanges, ViewChild } from '@angular/core';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { LangChangeEvent, TranslateModule, TranslateService } from '@ngx-translate/core';
import { ChartConfiguration, ChartType } from 'chart.js';
import { FilterComponent } from '../../filter/filter.component';
import { BaseChartDirective } from 'ng2-charts';
import { ActivatedRoute } from '@angular/router';
import { FilterDateTemplateComponent } from '../../filter-date-template/filter-date-template.component';
import { CommonModule } from '@angular/common';
import { ToasterService } from '../../services/toaster.service';

@Component({
  selector: 'app-bar-chart',
  standalone: true,
  imports: [  CommonModule,
    NgbDropdownModule,
    TranslateModule,
    FilterDateTemplateComponent,
    BaseChartDirective,],
  templateUrl: './bar-chart.component.html',
  styleUrl: './bar-chart.component.css'
})
export class BarChartComponent {
goldenDealerId!: number;
  nbFilter: number = 0;
  columns!: any[];
  data2: any[] = [];
  labels: string[] = [];
  data: any;
  @Input() themeStyle: any;
  @Input() type: any;
  @Input() id: any;
  constructor(
    private route: ActivatedRoute,
    private translate: TranslateService,
    private toastr: ToasterService,
    // private driverService: DriverService
  ) {}

  ngOnInit(): void {
    this.translate.onLangChange.subscribe((event: LangChangeEvent) => {
      this.columns = this.getWeeklyRangesOfMonth(new Date().getFullYear(), 2);

      this.initializeData();
      this.columns = this.getWeeklyRangesOfMonth(new Date().getFullYear(), 2);
      this.createChart(this.data2, this.columns, this.type);

    });
    this.route.params.subscribe((params) => {
      if (this.id == null) {
        this.goldenDealerId = +params['id'];
      } else {
        this.goldenDealerId = this.id;
      }

      let obj = {};
      this.columns = this.getWeeklyRangesOfMonth(new Date().getFullYear(), 2);
      if (this.type != 'drivers') {
        let obj = {
          year: new Date().getFullYear(),
          month: new Date().getMonth() + 1,
          entrepriseId: this.goldenDealerId,
        };

        // this.apis.getPerformance(obj).subscribe(
        //   (res: any) => {
        //     if (res.code === 200) {
        //       this.data2 = res?.data;
        //       this.initializeData();
        //       this.createChart(this.data2, this.columns, 'entreprise');
        //     }  else {
        //       this.toastr.showError(res?.msg);
        //     }
        //   },
        //   (error) => {
        //     this.toastr.showError('ERRORSERVENU');
        //   }
        // );
      } else {
        // obj = {
        //   idDriver: this.goldenDealerId,
        //   year: new Date().getFullYear(),
        //   month: new Date().getMonth() + 1,
        // };

        // this.apis.getPerformanceDriver(obj).subscribe(
        //   (res: any) => {
        //     if (res.code === 200) {
        //       this.data2 = res.data;
        //       this.initializeData();
        //       this.createChart(this.data2, this.columns, 'drivers');
              
        //     }  else {
        //       this.toastr.showError(res?.msg);
        //     }
        //   },
        //   (error) => {
        //     this.toastr.showError('ERRORSERVENU');
        //   }
        // );
      }
    });
  }
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;
  onFilterCleared() {
    this.changeFilter(undefined);
    this.columns = this.getWeeklyRangesOfMonth(new Date().getFullYear(), 2);

    this.initializeData();
    this.createChart(this.data2, this.columns, this.type);
    // this.createChart(this.data2, this.columns, this.themeStyle === 'drivers');
  }

  public barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    animation: false,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
        position: 'top',
        labels: {
          color: '#333',
          padding: 20,
          boxWidth: 10,
          boxHeight: 10,
          borderRadius: 5,
        },
      },
    },
  };

  public barChartType: ChartType = 'bar';

  public barChartData: ChartConfiguration['data'] = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
    datasets: [
      {
        data: [65, 59, 80, 81, 56, 55, 40],
        label: 'AVG 2045,00 dt',
        backgroundColor: '#ED6300', // Valeur par défaut
        borderColor: '#ED6300',
        borderRadius: 6.79,
        barThickness: 34.7,
      },
    ],
  };

  ngOnChanges(changes: SimpleChanges) {
    if (changes['type']) {
     this.updateChartData();
    }
  }

 updateChartData() {
  const type = this.themeStyle; // 'drivers' | 'goldendealers' | 'performance'

  this.barChartData.datasets[0].backgroundColor = (ctx: any) => {
    const chart = ctx.chart;
    const { ctx: chartCtx, chartArea } = chart;

    // fallback si chartArea pas encore dispo
    if (!chartArea) {
      switch (type) {
        case 'goldendealers': return '#ED6300';
        case 'drivers': return '#007bff';
        case 'performance': return 'pink';
        default: return '#999999';
      }
    }

    // Gestion des gradients ou couleurs fixes
    if (type === 'goldendealers') {
      const gradient = chartCtx.createLinearGradient(chartArea.left, 0, chartArea.right, 0);
      gradient.addColorStop(0, '#ED6300');
      gradient.addColorStop(1, '#833700');
      return gradient;
    } else if (type === 'drivers') {
      const gradient = chartCtx.createLinearGradient(chartArea.left, 0, chartArea.right, 0);
      gradient.addColorStop(0, '#007bff');
      gradient.addColorStop(1, '#0056b3');
      return gradient;
    } else if (type === 'performance') {
      return 'pink'; // couleur simple
    }

    return '#999999';
  };

  // Définition borderColor par type
  this.barChartData.datasets[0].borderColor =
    type === 'goldendealers'
      ? '#833700'
      : type === 'drivers'
      ? '#0056b3'
      : type === 'performance'
      ? 'rgba(237, 99, 0, 1)'
      : '#999999';

  // Mise à jour du graphique
  this.chart?.update();
}
 
  changeFilter(event: any) {
    this.nbFilter = 0;
    let obj = {};
    if (event === undefined) {
      obj = {
        year: new Date().getFullYear(),
        month: new Date().getMonth() + 1,
      };
      // params = '&year=' + new Date().getFullYear() + "&month=" + (new Date().getMonth() + 1)
    }
    if (event?.type === 'week') {
      this.nbFilter = 1;
      obj = {
        year: event?.year,
        week: event?.week,
      };
      this.columns = this.getDaysOfWeek(
        new Date(event?.startDay),
        new Date(event?.endDay)
      ); 
      console.log(this.columns);
      console.log(event);
      
    } else if (event?.type === 'month') {
      this.nbFilter = 1;
      this.columns = this.getWeeklyRangesOfMonth(event?.year, event?.month);
      obj = {
        year: event?.year,
        month: event?.month,
      };

    } else if (event?.type === 'day') {
      this.nbFilter = 1;
      this.columns = [event?.day];
      obj = {
        year: event?.year,
        day: event?.day,
      };
    
    } else if (event?.type === 'year') {
      this.nbFilter = 1;
      this.columns = this.getMonthsOfYear(event?.year);

      obj = {
        year: event?.year,
      };
    }
    if (this?.type != 'drivers') {
      obj = { ...obj, entrepriseId: this.goldenDealerId };
      // this.apis.getPerformance(obj).subscribe(
      //   (res: any) => {
      //     if (res.code === 200) {
      //       this.data2 = res.data;
      //       this.initializeData();
      //       this.createChart(this.data2, this.columns, 'drivers');
      //     }  else {
      //       this.toastr.showError(res?.msg);
      //     }
      //   },
      //   (error) => {
      //     this.toastr.showError('ERRORSERVENU');
      //   }
      // );
    } else {
      
      obj = {
        ...obj,
        idDriver: this.goldenDealerId,
      };

      // this.apis.getPerformanceDriver(obj).subscribe(
      //   (res: any) => {
      //     if (res.code === 200) {
      //       this.data2 = res.data;
      //       this.initializeData();
      //       this.createChart(this.data2, this.columns, 'drivers');
      //     }  else {
      //       this.toastr.showError(res?.msg);
      //     }
      //   },
      //   (error) => {
      //     this.toastr.showError('ERRORSERVENU');
      //   }
      // );
    }
  }

  getDaysOfWeek(
    startOfWeek: Date,
    endOfWeek: Date,
    locale: string = 'default' // Paramètre pour spécifier la locale
  ) {
    const days: any[] = [];
    let currentDate = new Date(startOfWeek); // Cloner la date pour éviter de modifier l'original
    while (currentDate <= endOfWeek) {
      days.push(
        currentDate.toLocaleDateString(locale, {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
        })
      );
      currentDate.setDate(currentDate.getDate() + 1); // Passer au jour suivant
    }
    return days;
  }
  getMonthsOfYear(year: number) {
    const months: any[] = [];
    for (let i = 0; i < 12; i++) {
      months.push(
        new Date(year, i).toLocaleDateString('default', {
          month: 'long',
        })
      );
    }
    return months;
  }

  getWeeklyRangesOfMonth(year: number, month: number): string[] {
    const weeks: string[] = [];
    let weekIndex = 1;

    // Début au 1er jour du mois
    let startDate = new Date(year, month - 1, 1);

    while (startDate.getMonth() === month - 1) {
      // Fin de semaine (dimanche)
      const endDate = new Date(startDate);
      endDate.setDate(startDate.getDate() + (6 - startDate.getDay()));

      // Ajuster la fin si elle sort du mois
      if (endDate.getMonth() !== month - 1) {
        endDate.setDate(new Date(year, month, 0).getDate());
      }

      // Ajouter uniquement le nom de la semaine
      // weeks.push(`Semaine ${weekIndex}`);
      this.translate
        .get('WEEKLABEL', { index: weekIndex })
        .subscribe((translatedLabel) => {
          weeks.push(translatedLabel);
        });
      // Passer au lundi suivant
      startDate = new Date(endDate);
      startDate.setDate(endDate.getDate() + 1);

      weekIndex++;
    }

    return weeks;
  }

  initializeData(filteredData?: any[]) {
    const dataValues = filteredData || this.data2;
    this.data = {
      labels: this.columns,
      datasets: [
        {
          label: this.translate.instant('RACE.ENDRIDE'),
          data: dataValues,
          backgroundColor: 'pink',
          hoverBorderRadius: 25,
          hoverBackgroundColor: 'rgba(237, 99, 0, 1)',
          borderRadius: 25,
          barThickness: 15,
        },
      ],
    };
  }

  createChart(
    data: number[],
    labels: string[],
    type: 'entreprise' | 'drivers' | 'performance' = 'entreprise'
  ) {
    if (type === 'performance') {
     
      // Cas spécifique Performance
      this.barChartData = {
        labels: labels,
        
        datasets: [
          {
            label: this.translate.instant('RACE.ENDRIDE'),
            data: data,
            backgroundColor: 'pink',
            // hoverBorderRadius: 25,
            hoverBackgroundColor: 'rgba(237, 99, 0, 1)',
            borderRadius: 25,
            barThickness: 15,
          },
        ],
      };
      // Options pour titre blanc et axes
  this.barChartOptions = {
    responsive: true,
    plugins: {
    
      legend: {
        labels: {
          color: '#000', // légende en blanc
        },
      },
    },
    scales: {
      
      y: {
        display: false, 
      },
    },
  };
    } else {
      // Cas Driver ou Entreprise
      this.barChartData.labels = labels;

      this.barChartData.datasets[0].data = data;
      this.barChartData.datasets[0].backgroundColor =
        type === 'drivers' ? '#007bff' : '#ED6300';
      this.barChartData.datasets[0].borderColor =
        type === 'drivers' ? '#0056b3' : '#833700';
      this.barChartData.datasets[0].label =
        type === 'drivers' ? 'AVG Driver' : 'AVG Entreprise';
    }

    // Forcer le rafraîchissement du graphique
    this.chart?.update();
  }
  
}
