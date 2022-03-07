import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IExecution } from '../execution.model';

@Component({
  selector: 'jhi-execution-detail',
  templateUrl: './execution-detail.component.html',
})
export class ExecutionDetailComponent implements OnInit {
  execution: IExecution | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ execution }) => {
      this.execution = execution;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
