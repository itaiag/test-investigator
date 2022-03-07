import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ExecutionDetailComponent } from './execution-detail.component';

describe('Execution Management Detail Component', () => {
  let comp: ExecutionDetailComponent;
  let fixture: ComponentFixture<ExecutionDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ExecutionDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ execution: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ExecutionDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ExecutionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load execution on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.execution).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
