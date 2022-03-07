import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TestResultDetailComponent } from './test-result-detail.component';

describe('TestResult Management Detail Component', () => {
  let comp: TestResultDetailComponent;
  let fixture: ComponentFixture<TestResultDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TestResultDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ testResult: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TestResultDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TestResultDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load testResult on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.testResult).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
