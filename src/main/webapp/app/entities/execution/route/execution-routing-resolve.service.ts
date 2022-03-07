import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IExecution, Execution } from '../execution.model';
import { ExecutionService } from '../service/execution.service';

@Injectable({ providedIn: 'root' })
export class ExecutionRoutingResolveService implements Resolve<IExecution> {
  constructor(protected service: ExecutionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IExecution> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((execution: HttpResponse<Execution>) => {
          if (execution.body) {
            return of(execution.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Execution());
  }
}
