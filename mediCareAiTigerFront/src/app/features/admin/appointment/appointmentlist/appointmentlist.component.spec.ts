import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentlistComponent } from './appointmentlist.component';

describe('AppointmentlistComponent', () => {
  let component: AppointmentlistComponent;
  let fixture: ComponentFixture<AppointmentlistComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppointmentlistComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppointmentlistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
