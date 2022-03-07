import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBug } from '../bug.model';
import { BugService } from '../service/bug.service';

@Component({
  templateUrl: './bug-delete-dialog.component.html',
})
export class BugDeleteDialogComponent {
  bug?: IBug;

  constructor(protected bugService: BugService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bugService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
