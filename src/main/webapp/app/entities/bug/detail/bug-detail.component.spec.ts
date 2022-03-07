import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BugDetailComponent } from './bug-detail.component';

describe('Bug Management Detail Component', () => {
  let comp: BugDetailComponent;
  let fixture: ComponentFixture<BugDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BugDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ bug: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BugDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BugDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load bug on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.bug).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
