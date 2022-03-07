import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITestResult } from '../test-result.model';
import { TestResultService } from '../service/test-result.service';
import { TestResultDeleteDialogComponent } from '../delete/test-result-delete-dialog.component';

@Component({
  selector: 'jhi-test-result',
  templateUrl: './test-result.component.html',
})
export class TestResultComponent implements OnInit {
  testResults?: ITestResult[];
  isLoading = false;

  constructor(protected testResultService: TestResultService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.testResultService.query().subscribe({
      next: (res: HttpResponse<ITestResult[]>) => {
        this.isLoading = false;
        this.testResults = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ITestResult): number {
    return item.id!;
  }

  delete(testResult: ITestResult): void {
    const modalRef = this.modalService.open(TestResultDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.testResult = testResult;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
