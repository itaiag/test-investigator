import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBug } from '../bug.model';
import { BugService } from '../service/bug.service';
import { BugDeleteDialogComponent } from '../delete/bug-delete-dialog.component';

@Component({
  selector: 'jhi-bug',
  templateUrl: './bug.component.html',
})
export class BugComponent implements OnInit {
  bugs?: IBug[];
  isLoading = false;

  constructor(protected bugService: BugService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.bugService.query().subscribe({
      next: (res: HttpResponse<IBug[]>) => {
        this.isLoading = false;
        this.bugs = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IBug): number {
    return item.id!;
  }

  delete(bug: IBug): void {
    const modalRef = this.modalService.open(BugDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.bug = bug;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
