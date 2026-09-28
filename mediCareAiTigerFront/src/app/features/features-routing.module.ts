import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ListeAgentsComponent } from './admin/agent/liste-agents/liste-agents.component';
import { ListePatientsComponent } from './admin/Client/liste-patients/liste-patients.component';
import { DoctorListComponent } from './admin/doctor/doctor-list/doctor-list.component';
import { RecordListComponent } from './admin/record/record-list/record-list.component';
import { RecordDetailsComponent } from './admin/record/record-details/record-details.component';
import { AppointmentlistComponent } from './admin/appointment/appointmentlist/appointmentlist.component';
import { MedicamentListComponent } from './admin/medicament/medicament-list/medicament-list.component';
import { ScheduleListComponent } from './admin/schedule/schedule-list/schedule-list.component';

const routes: Routes = [
  {
    path: '',
    component: ListePatientsComponent,
  
  }, 

  {
    path: 'agents',
    component: ListeAgentsComponent,
    
  
  },
  
   {
    path: 'doctors',
    component: DoctorListComponent,
    
  
  },
  
   {
    path: 'records',
    component: RecordListComponent,
    
  
  },
   {
    path: 'appointments',
    component: AppointmentlistComponent,
    
  
  },
    {
  path: 'records/details/:id',
  component: RecordDetailsComponent,

},
  {
    path: 'medicaments',
    component: MedicamentListComponent,
  },
  {
    path: 'schedules',
    component: ScheduleListComponent,
  },
  {
    path: 'clients',
    component: ListePatientsComponent,
   /* resolve: {
      data: clientsResolver
    }*/
  },
//   {
//   path: 'client/profile/:id',
//   component: ProfileClientComponent,

// }, 
 

    
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FeaturesRoutingModule { }
