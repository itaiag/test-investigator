import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IExecution } from '../execution.model';
import { ExecutionService } from '../service/execution.service';
import { ExecutionDeleteDialogComponent } from '../delete/execution-delete-dialog.component';

@Component({
  selector: 'jhi-execution',
  templateUrl: './execution.component.html',
})
export class ExecutionComponent implements OnInit {
  executions?: IExecution[];
  isLoading = false;

  constructor(protected executionService: ExecutionService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.executionService.query().subscribe({
      next: (res: HttpResponse<IExecution[]>) => {
        this.isLoading = false;
        this.executions = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IExecution): number {
    return item.id!;
  }

  delete(execution: IExecution): void {
    const modalRef = this.modalService.open(ExecutionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.execution = execution;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
