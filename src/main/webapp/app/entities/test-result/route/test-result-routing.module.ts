import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TestResultComponent } from '../list/test-result.component';
import { TestResultDetailComponent } from '../detail/test-result-detail.component';
import { TestResultUpdateComponent } from '../update/test-result-update.component';
import { TestResultRoutingResolveService } from './test-result-routing-resolve.service';

const testResultRoute: Routes = [
  {
    path: '',
    component: TestResultComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TestResultDetailComponent,
    resolve: {
      testResult: TestResultRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TestResultUpdateComponent,
    resolve: {
      testResult: TestResultRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TestResultUpdateComponent,
    resolve: {
      testResult: TestResultRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(testResultRoute)],
  exports: [RouterModule],
})
export class TestResultRoutingModule {}
