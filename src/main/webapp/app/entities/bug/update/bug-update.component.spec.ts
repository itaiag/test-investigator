import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BugService } from '../service/bug.service';
import { IBug, Bug } from '../bug.model';

import { BugUpdateComponent } from './bug-update.component';

describe('Bug Management Update Component', () => {
  let comp: BugUpdateComponent;
  let fixture: ComponentFixture<BugUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bugService: BugService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BugUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(BugUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BugUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bugService = TestBed.inject(BugService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const bug: IBug = { id: 456 };

      activatedRoute.data = of({ bug });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(bug));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Bug>>();
      const bug = { id: 123 };
      jest.spyOn(bugService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bug });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bug }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(bugService.update).toHaveBeenCalledWith(bug);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Bug>>();
      const bug = new Bug();
      jest.spyOn(bugService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bug });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bug }));
      saveSubject.complete();

      // THEN
      expect(bugService.create).toHaveBeenCalledWith(bug);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Bug>>();
      const bug = { id: 123 };
      jest.spyOn(bugService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bug });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bugService.update).toHaveBeenCalledWith(bug);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
