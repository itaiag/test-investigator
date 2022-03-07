import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ExecutionComponent } from '../list/execution.component';
import { ExecutionDetailComponent } from '../detail/execution-detail.component';
import { ExecutionUpdateComponent } from '../update/execution-update.component';
import { ExecutionRoutingResolveService } from './execution-routing-resolve.service';

const executionRoute: Routes = [
  {
    path: '',
    component: ExecutionComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ExecutionDetailComponent,
    resolve: {
      execution: ExecutionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ExecutionUpdateComponent,
    resolve: {
      execution: ExecutionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ExecutionUpdateComponent,
    resolve: {
      execution: ExecutionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(executionRoute)],
  exports: [RouterModule],
})
export class ExecutionRoutingModule {}
