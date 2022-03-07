import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ExecutionService } from '../service/execution.service';

import { ExecutionComponent } from './execution.component';

describe('Execution Management Component', () => {
  let comp: ExecutionComponent;
  let fixture: ComponentFixture<ExecutionComponent>;
  let service: ExecutionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ExecutionComponent],
    })
      .overrideTemplate(ExecutionComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExecutionComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ExecutionService);

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
    expect(comp.executions?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
