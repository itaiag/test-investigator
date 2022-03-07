import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITestResult, TestResult } from '../test-result.model';
import { TestResultService } from '../service/test-result.service';
import { IExecution } from 'app/entities/execution/execution.model';
import { ExecutionService } from 'app/entities/execution/service/execution.service';
import { IBug } from 'app/entities/bug/bug.model';
import { BugService } from 'app/entities/bug/service/bug.service';
import { Status } from 'app/entities/enumerations/status.model';
import { FailureClassification } from 'app/entities/enumerations/failure-classification.model';

@Component({
  selector: 'jhi-test-result-update',
  templateUrl: './test-result-update.component.html',
})
export class TestResultUpdateComponent implements OnInit {
  isSaving = false;
  statusValues = Object.keys(Status);
  failureClassificationValues = Object.keys(FailureClassification);

  executionsSharedCollection: IExecution[] = [];
  bugsSharedCollection: IBug[] = [];

  editForm = this.fb.group({
    id: [],
    creationDate: [],
    testName: [null, [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
    method: [],
    testProperties: [],
    testStatus: [],
    failureMessage: [],
    failureClassification: [],
    fix: [],
    comments: [],
    execution: [],
    bug: [],
  });

  constructor(
    protected testResultService: TestResultService,
    protected executionService: ExecutionService,
    protected bugService: BugService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ testResult }) => {
      this.updateForm(testResult);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const testResult = this.createFromForm();
    if (testResult.id !== undefined) {
      this.subscribeToSaveResponse(this.testResultService.update(testResult));
    } else {
      this.subscribeToSaveResponse(this.testResultService.create(testResult));
    }
  }

  trackExecutionById(index: number, item: IExecution): number {
    return item.id!;
  }

  trackBugById(index: number, item: IBug): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITestResult>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(testResult: ITestResult): void {
    this.editForm.patchValue({
      id: testResult.id,
      creationDate: testResult.creationDate,
      testName: testResult.testName,
      method: testResult.method,
      testProperties: testResult.testProperties,
      testStatus: testResult.testStatus,
      failureMessage: testResult.failureMessage,
      failureClassification: testResult.failureClassification,
      fix: testResult.fix,
      comments: testResult.comments,
      execution: testResult.execution,
      bug: testResult.bug,
    });

    this.executionsSharedCollection = this.executionService.addExecutionToCollectionIfMissing(
      this.executionsSharedCollection,
      testResult.execution
    );
    this.bugsSharedCollection = this.bugService.addBugToCollectionIfMissing(this.bugsSharedCollection, testResult.bug);
  }

  protected loadRelationshipsOptions(): void {
    this.executionService
      .query()
      .pipe(map((res: HttpResponse<IExecution[]>) => res.body ?? []))
      .pipe(
        map((executions: IExecution[]) =>
          this.executionService.addExecutionToCollectionIfMissing(executions, this.editForm.get('execution')!.value)
        )
      )
      .subscribe((executions: IExecution[]) => (this.executionsSharedCollection = executions));

    this.bugService
      .query()
      .pipe(map((res: HttpResponse<IBug[]>) => res.body ?? []))
      .pipe(map((bugs: IBug[]) => this.bugService.addBugToCollectionIfMissing(bugs, this.editForm.get('bug')!.value)))
      .subscribe((bugs: IBug[]) => (this.bugsSharedCollection = bugs));
  }

  protected createFromForm(): ITestResult {
    return {
      ...new TestResult(),
      id: this.editForm.get(['id'])!.value,
      creationDate: this.editForm.get(['creationDate'])!.value,
      testName: this.editForm.get(['testName'])!.value,
      method: this.editForm.get(['method'])!.value,
      testProperties: this.editForm.get(['testProperties'])!.value,
      testStatus: this.editForm.get(['testStatus'])!.value,
      failureMessage: this.editForm.get(['failureMessage'])!.value,
      failureClassification: this.editForm.get(['failureClassification'])!.value,
      fix: this.editForm.get(['fix'])!.value,
      comments: this.editForm.get(['comments'])!.value,
      execution: this.editForm.get(['execution'])!.value,
      bug: this.editForm.get(['bug'])!.value,
    };
  }
}
