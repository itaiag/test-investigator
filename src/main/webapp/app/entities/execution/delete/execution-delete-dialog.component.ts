import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IExecution } from '../execution.model';
import { ExecutionService } from '../service/execution.service';

@Component({
  templateUrl: './execution-delete-dialog.component.html',
})
export class ExecutionDeleteDialogComponent {
  execution?: IExecution;

  constructor(protected executionService: ExecutionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.executionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
