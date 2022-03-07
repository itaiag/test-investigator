import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBug, Bug } from '../bug.model';
import { BugService } from '../service/bug.service';

@Injectable({ providedIn: 'root' })
export class BugRoutingResolveService implements Resolve<IBug> {
  constructor(protected service: BugService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBug> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((bug: HttpResponse<Bug>) => {
          if (bug.body) {
            return of(bug.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Bug());
  }
}
