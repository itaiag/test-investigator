import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TestResultService } from '../service/test-result.service';

import { TestResultComponent } from './test-result.component';

describe('TestResult Management Component', () => {
  let comp: TestResultComponent;
  let fixture: ComponentFixture<TestResultComponent>;
  let service: TestResultService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TestResultComponent],
    })
      .overrideTemplate(TestResultComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TestResultComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TestResultService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.testResults?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
