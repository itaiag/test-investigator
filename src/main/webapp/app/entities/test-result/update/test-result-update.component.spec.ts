import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TestResultService } from '../service/test-result.service';
import { ITestResult, TestResult } from '../test-result.model';
import { IExecution } from 'app/entities/execution/execution.model';
import { ExecutionService } from 'app/entities/execution/service/execution.service';
import { IBug } from 'app/entities/bug/bug.model';
import { BugService } from 'app/entities/bug/service/bug.service';

import { TestResultUpdateComponent } from './test-result-update.component';

describe('TestResult Management Update Component', () => {
  let comp: TestResultUpdateComponent;
  let fixture: ComponentFixture<TestResultUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let testResultService: TestResultService;
  let executionService: ExecutionService;
  let bugService: BugService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TestResultUpdateComponent],
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
      .overrideTemplate(TestResultUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TestResultUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    testResultService = TestBed.inject(TestResultService);
    executionService = TestBed.inject(ExecutionService);
    bugService = TestBed.inject(BugService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Execution query and add missing value', () => {
      const testResult: ITestResult = { id: 456 };
      const execution: IExecution = { id: 90569 };
      testResult.execution = execution;

      const executionCollection: IExecution[] = [{ id: 57137 }];
      jest.spyOn(executionService, 'query').mockReturnValue(of(new HttpResponse({ body: executionCollection })));
      const additionalExecutions = [execution];
      const expectedCollection: IExecution[] = [...additionalExecutions, ...executionCollection];
      jest.spyOn(executionService, 'addExecutionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ testResult });
      comp.ngOnInit();

      expect(executionService.query).toHaveBeenCalled();
      expect(executionService.addExecutionToCollectionIfMissing).toHaveBeenCalledWith(executionCollection, ...additionalExecutions);
      expect(comp.executionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Bug query and add missing value', () => {
      const testResult: ITestResult = { id: 456 };
      const bug: IBug = { id: 75704 };
      testResult.bug = bug;

      const bugCollection: IBug[] = [{ id: 97724 }];
      jest.spyOn(bugService, 'query').mockReturnValue(of(new HttpResponse({ body: bugCollection })));
      const additionalBugs = [bug];
      const expectedCollection: IBug[] = [...additionalBugs, ...bugCollection];
      jest.spyOn(bugService, 'addBugToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ testResult });
      comp.ngOnInit();

      expect(bugService.query).toHaveBeenCalled();
      expect(bugService.addBugToCollectionIfMissing).toHaveBeenCalledWith(bugCollection, ...additionalBugs);
      expect(comp.bugsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const testResult: ITestResult = { id: 456 };
      const execution: IExecution = { id: 34885 };
      testResult.execution = execution;
      const bug: IBug = { id: 50535 };
      testResult.bug = bug;

      activatedRoute.data = of({ testResult });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(testResult));
      expect(comp.executionsSharedCollection).toContain(execution);
      expect(comp.bugsSharedCollection).toContain(bug);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TestResult>>();
      const testResult = { id: 123 };
      jest.spyOn(testResultService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ testResult });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: testResult }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(testResultService.update).toHaveBeenCalledWith(testResult);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TestResult>>();
      const testResult = new TestResult();
      jest.spyOn(testResultService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ testResult });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: testResult }));
      saveSubject.complete();

      // THEN
      expect(testResultService.create).toHaveBeenCalledWith(testResult);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TestResult>>();
      const testResult = { id: 123 };
      jest.spyOn(testResultService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ testResult });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(testResultService.update).toHaveBeenCalledWith(testResult);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackExecutionById', () => {
      it('Should return tracked Execution primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackExecutionById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackBugById', () => {
      it('Should return tracked Bug primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackBugById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
