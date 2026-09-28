import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BtnFlottantComponent } from './btn-flottant.component';

describe('BtnFlottantComponent', () => {
  let component: BtnFlottantComponent;
  let fixture: ComponentFixture<BtnFlottantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BtnFlottantComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BtnFlottantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
