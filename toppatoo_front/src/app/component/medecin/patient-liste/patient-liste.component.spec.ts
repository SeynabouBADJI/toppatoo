import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientListeComponent } from './patient-liste.component';

describe('PatientListeComponent', () => {
  let component: PatientListeComponent;
  let fixture: ComponentFixture<PatientListeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientListeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientListeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
