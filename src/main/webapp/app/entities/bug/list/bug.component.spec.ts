import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { BugService } from '../service/bug.service';

import { BugComponent } from './bug.component';

describe('Bug Management Component', () => {
  let comp: BugComponent;
  let fixture: ComponentFixture<BugComponent>;
  let service: BugService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [BugComponent],
    })
      .overrideTemplate(BugComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BugComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(BugService);

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
    expect(comp.bugs?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
