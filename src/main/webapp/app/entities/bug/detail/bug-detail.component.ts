import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBug } from '../bug.model';

@Component({
  selector: 'jhi-bug-detail',
  templateUrl: './bug-detail.component.html',
})
export class BugDetailComponent implements OnInit {
  bug: IBug | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bug }) => {
      this.bug = bug;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
