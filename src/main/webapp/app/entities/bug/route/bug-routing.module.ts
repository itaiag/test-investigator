import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BugComponent } from '../list/bug.component';
import { BugDetailComponent } from '../detail/bug-detail.component';
import { BugUpdateComponent } from '../update/bug-update.component';
import { BugRoutingResolveService } from './bug-routing-resolve.service';

const bugRoute: Routes = [
  {
    path: '',
    component: BugComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BugDetailComponent,
    resolve: {
      bug: BugRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BugUpdateComponent,
    resolve: {
      bug: BugRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BugUpdateComponent,
    resolve: {
      bug: BugRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(bugRoute)],
  exports: [RouterModule],
})
export class BugRoutingModule {}
