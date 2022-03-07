import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'execution',
        data: { pageTitle: 'Executions' },
        loadChildren: () => import('./execution/execution.module').then(m => m.ExecutionModule),
      },
      {
        path: 'test-result',
        data: { pageTitle: 'TestResults' },
        loadChildren: () => import('./test-result/test-result.module').then(m => m.TestResultModule),
      },
      {
        path: 'bug',
        data: { pageTitle: 'Bugs' },
        loadChildren: () => import('./bug/bug.module').then(m => m.BugModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
