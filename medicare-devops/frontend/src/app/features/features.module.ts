import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FeaturesRoutingModule } from './features-routing.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ReusableTableComponent } from '../shared/reusable-table/reusable-table.component';
import { ConfirmModalComponent } from '../shared/confirm-modal/confirm-modal.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { FilterComponent } from '../shared/filter/filter.component';
import { ProfileComponent } from './admin/agent/profile/profile.component';
import { ListeAgentsComponent } from './admin/agent/liste-agents/liste-agents.component';
import { AgentModalComponent } from './admin/agent/agent-modal/agent-modal.component';
import {
  NgLabelTemplateDirective,
  NgOptionTemplateDirective,
  NgSelectComponent,
  NgSelectModule,
} from '@ng-select/ng-select';
// import { NgxIntlTelInputModule } from 'ngx-intl-tel-input';
import { BaseChartDirective } from 'ng2-charts';

import { BarChartComponent } from '../shared/charts/bar-chart/bar-chart.component';
import { UserInfoComponent } from '../shared/user-info/user-info.component';
import { BlockModalComponent } from './admin/Client/block-modal/block-modal.component';
import { AutocompleteLibModule } from 'angular-ng-autocomplete';
import { LoaderComponent } from '../shared/loader/loader.component';
import { NoDataComponent } from '../shared/no-data/no-data.component';
import { SaveButtonComponent } from '../shared/save-button/save-button.component';
import { CopyComponent } from '../shared/copy/copy.component';
import { BtnFlottantComponent } from '../shared/btn-flottant/btn-flottant.component';
import { FilterTemplateComponent } from '../shared/filter-template/filter-template.component';
import { FilterDateTemplateComponent } from '../shared/filter-date-template/filter-date-template.component';
import { BtnExportComponent } from '../shared/btn-export/btn-export.component';
import { CardTemplateComponent } from '../shared/card-template/card-template.component';
import { NgxIntlTelInputModule } from 'ngx-intl-tel-input';
import { GoogleMapsModule } from '@angular/google-maps';
import { InputGeneralSearchComponent } from "../navigation/input-general-search/input-general-search.component";
import { QuillEditorComponent } from "ngx-quill";

@NgModule({
  declarations: [
  /*  NavBarComponent,
    ListeClientsComponent,
    DetailsClientComponent,
    ClientModalComponent,
    FooterComponent,
    ProfileComponent,
    ListeAgentsComponent,
    // AgentModalComponent,
    DashbordComponent,
    BlockModalComponent,
    // SettingsComponent,
    ProfileAgentComponent,
 ProfileClientComponent, 
ClientModalComponent ,*/
 
  ],
  imports: [
    GoogleMapsModule,
    NgSelectModule,
    NgLabelTemplateDirective,
    NgOptionTemplateDirective,
    NgSelectComponent,
    BaseChartDirective,
    NgxIntlTelInputModule,
    AutocompleteLibModule,
    CommonModule,
    FeaturesRoutingModule,
    NgbModule,
    FormsModule,
    ReusableTableComponent,
    ConfirmModalComponent,
    FilterComponent,
    // FunctionCardComponent,
    // InvoiceComponent,
    // ProductCardComponent,
    ReactiveFormsModule,
    // CarouselCardComponent,
    // ItemProductComponent,
    // StatItemComponent,
    BarChartComponent,
    LoaderComponent,
    NoDataComponent,
    // PreInvoicingComponent,
    // HistoricInvoiceComponent,
    // InvoiceItemComponent,
    UserInfoComponent,
    // ProfessionalInfoComponent,
    CopyComponent,
    CardTemplateComponent,
    BtnFlottantComponent,
    SaveButtonComponent,
    FilterTemplateComponent,
    FilterDateTemplateComponent,
    BtnExportComponent,
    LoaderComponent,
    // PromoComponent,
    // TranslateModule.forRoot({
    //     loader: {
    //         provide: TranslateLoader,
    //         useFactory: HttpLoaderFactory,
    //         deps: [HttpClient],
    //     },
    //     defaultLanguage: 'fr',
    // }),
    InputGeneralSearchComponent,
    QuillEditorComponent
],
  // exports: [SideBarComponent],
  // exports: [NavbarComponent,SideBarComponent],
})
export class FeaturesModule {}
