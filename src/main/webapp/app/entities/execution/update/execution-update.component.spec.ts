import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ExecutionService } from '../service/execution.service';
import { IExecution, Execution } from '../execution.model';

import { ExecutionUpdateComponent } from './execution-update.component';

describe('Execution Management Update Component', () => {
  let comp: ExecutionUpdateComponent;
  let fixture: ComponentFixture<ExecutionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let executionService: ExecutionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ExecutionUpdateComponent],
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
      .overrideTemplate(ExecutionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExecutionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    executionService = TestBed.inject(ExecutionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const execution: IExecution = { id: 456 };

      activatedRoute.data = of({ execution });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(execution));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Execution>>();
      const execution = { id: 123 };
      jest.spyOn(executionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ execution });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: execution }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(executionService.update).toHaveBeenCalledWith(execution);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Execution>>();
      const execution = new Execution();
      jest.spyOn(executionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ execution });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: execution }));
      saveSubject.complete();

      // THEN
      expect(executionService.create).toHaveBeenCalledWith(execution);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Execution>>();
      const execution = { id: 123 };
      jest.spyOn(executionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ execution });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(executionService.update).toHaveBeenCalledWith(execution);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
