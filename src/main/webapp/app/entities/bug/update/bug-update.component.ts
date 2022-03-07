import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IBug, Bug } from '../bug.model';
import { BugService } from '../service/bug.service';
import { BugStatus } from 'app/entities/enumerations/bug-status.model';

@Component({
  selector: 'jhi-bug-update',
  templateUrl: './bug-update.component.html',
})
export class BugUpdateComponent implements OnInit {
  isSaving = false;
  bugStatusValues = Object.keys(BugStatus);

  editForm = this.fb.group({
    id: [],
    bugName: [],
    description: [],
    status: [],
  });

  constructor(protected bugService: BugService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bug }) => {
      this.updateForm(bug);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bug = this.createFromForm();
    if (bug.id !== undefined) {
      this.subscribeToSaveResponse(this.bugService.update(bug));
    } else {
      this.subscribeToSaveResponse(this.bugService.create(bug));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBug>>): void {
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

  protected updateForm(bug: IBug): void {
    this.editForm.patchValue({
      id: bug.id,
      bugName: bug.bugName,
      description: bug.description,
      status: bug.status,
    });
  }

  protected createFromForm(): IBug {
    return {
      ...new Bug(),
      id: this.editForm.get(['id'])!.value,
      bugName: this.editForm.get(['bugName'])!.value,
      description: this.editForm.get(['description'])!.value,
      status: this.editForm.get(['status'])!.value,
    };
  }
}
