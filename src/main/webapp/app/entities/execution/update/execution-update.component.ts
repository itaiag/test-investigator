import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IExecution, Execution } from '../execution.model';
import { ExecutionService } from '../service/execution.service';

@Component({
  selector: 'jhi-execution-update',
  templateUrl: './execution-update.component.html',
})
export class ExecutionUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    creationDate: [],
    executionDescription: [],
    executionProperties: [],
  });

  constructor(protected executionService: ExecutionService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ execution }) => {
      this.updateForm(execution);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const execution = this.createFromForm();
    if (execution.id !== undefined) {
      this.subscribeToSaveResponse(this.executionService.update(execution));
    } else {
      this.subscribeToSaveResponse(this.executionService.create(execution));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExecution>>): void {
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

  protected updateForm(execution: IExecution): void {
    this.editForm.patchValue({
      id: execution.id,
      creationDate: execution.creationDate,
      executionDescription: execution.executionDescription,
      executionProperties: execution.executionProperties,
    });
  }

  protected createFromForm(): IExecution {
    return {
      ...new Execution(),
      id: this.editForm.get(['id'])!.value,
      creationDate: this.editForm.get(['creationDate'])!.value,
      executionDescription: this.editForm.get(['executionDescription'])!.value,
      executionProperties: this.editForm.get(['executionProperties'])!.value,
    };
  }
}
