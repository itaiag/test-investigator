import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITestResult, TestResult } from '../test-result.model';
import { TestResultService } from '../service/test-result.service';

@Injectable({ providedIn: 'root' })
export class TestResultRoutingResolveService implements Resolve<ITestResult> {
  constructor(protected service: TestResultService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITestResult> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((testResult: HttpResponse<TestResult>) => {
          if (testResult.body) {
            return of(testResult.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TestResult());
  }
}
