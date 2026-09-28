import { CommonModule } from '@angular/common';
import { Component, ElementRef, Input, SimpleChanges, ViewChild } from '@angular/core';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartType, ChartData } from 'chart.js';
@Component({
  selector: 'app-donut-chart',
  standalone: true,
  imports: [CommonModule, NgbDropdownModule, TranslateModule,  BaseChartDirective],
  templateUrl: './donut-chart.component.html',
  styleUrl: './donut-chart.component.css'
})
export class DonutChartComponent {
@Input() title: string = '';
  @Input() labels: string[] = [];
  @Input() data: number[] = [];
  @Input() colors: string[] = [];

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  public donutChartType: ChartType = 'doughnut';
  public donutChartData: ChartData<'doughnut'> = { labels: [], datasets: [] };

  public donutChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    //cutout: '40%', // Adjust to control the thickness of the rings
    plugins: {
      legend: { display: false } // We use a custom legend to match your design
    }

    
  };
   public donutChartConfig: ChartConfiguration<'doughnut'> = {
    type: 'doughnut',
    data: {
      // labels: ['ONLINE', 'OFFLINE', 'TOTAL'],
      datasets: []
    },
    options: {
      animation: false,
      responsive: true,
      maintainAspectRatio: false,
      cutout: '40%',
      plugins: {
        legend: {
          display: true,
          position: 'top'
        },
        tooltip: {
          enabled: false,
          // callbacks: {
          //   label: (tooltipItem) => {
          //     const label = tooltipItem.label || '';
          //     const value = tooltipItem.raw;
          //     return `${label}: ${value} voitures`;
          //   }
          // },
          // filter: (tooltipItem) => {
          //   // Affiche uniquement les datasets qui ont de vraies données
          //   return tooltipItem.raw !== 0 && tooltipItem.raw !== 3;
          // }
        }
      }
    }
  };

  ngOnChanges(changes: SimpleChanges): void {
    if (this.data && this.labels && this.colors) {
      this.generateMultiRingChart();
    }
  }

  generateMultiRingChart() {
    // We calculate a 'Total' for the background gray ring (empty part)
    const maxVal = Math.max(...this.data) * 1.2 || 100; 

    this.donutChartData = {
      labels: this.labels,
      datasets: this.data.map((val, index) => ({
        data: [val, maxVal - val],
        backgroundColor: [this.colors[index], '#f3f3f3'], // Color vs Gray background
        borderWidth: 3,
        weight: 0.3 // Controls ring thickness
      }))
    };
    this.chart?.update();
  }
}