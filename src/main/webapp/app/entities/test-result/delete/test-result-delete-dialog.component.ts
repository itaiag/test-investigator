import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITestResult } from '../test-result.model';
import { TestResultService } from '../service/test-result.service';

@Component({
  templateUrl: './test-result-delete-dialog.component.html',
})
export class TestResultDeleteDialogComponent {
  testResult?: ITestResult;

  constructor(protected testResultService: TestResultService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.testResultService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
