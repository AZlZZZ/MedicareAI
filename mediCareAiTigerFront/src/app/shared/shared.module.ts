import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReusableTableComponent } from './reusable-table/reusable-table.component';
import { SharedRoutingModule } from './shared-routing.module';
import { NgbDatepickerModule, NgbModule, NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { ConfirmModalComponent } from './confirm-modal/confirm-modal.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { FilterComponent } from './filter/filter.component';
import { BarChartComponent } from './charts/bar-chart/bar-chart.component';
import { PaginatorComponent } from './paginator/paginator.component';


@NgModule({
  declarations: [

  ],
  imports: [
    CommonModule ,
     SharedRoutingModule,
    NgbModule,
    NgbPaginationModule,
    FormsModule,
    NgbDatepickerModule , 
  //   TranslateModule.forRoot({
  //     loader: {
  //         provide: TranslateLoader,
  //         useFactory: HttpLoaderFactory,
  //         deps: [HttpClient]
  //     },
  //     defaultLanguage: 'fr'
  // }),
  ] , 
   exports : [
    // ReusableTableComponent , 
    // ConfirmModalComponent,
    // ProductCardComponent , 
    // InvoiceComponent, 
    // CarouselCardComponent , 
    // FilterComponent , 
    // FunctionCardComponent ,
    // StatItemComponent , 
    // ItemProductComponent  , 
    // BarChartComponent , 
    // PaginatorComponent
    
   ]
})
export class SharedModule { }
