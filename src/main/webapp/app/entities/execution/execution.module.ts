import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ExecutionComponent } from './list/execution.component';
import { ExecutionDetailComponent } from './detail/execution-detail.component';
import { ExecutionUpdateComponent } from './update/execution-update.component';
import { ExecutionDeleteDialogComponent } from './delete/execution-delete-dialog.component';
import { ExecutionRoutingModule } from './route/execution-routing.module';

@NgModule({
  imports: [SharedModule, ExecutionRoutingModule],
  declarations: [ExecutionComponent, ExecutionDetailComponent, ExecutionUpdateComponent, ExecutionDeleteDialogComponent],
  entryComponents: [ExecutionDeleteDialogComponent],
})
export class ExecutionModule {}
