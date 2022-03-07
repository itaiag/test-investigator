import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BugComponent } from './list/bug.component';
import { BugDetailComponent } from './detail/bug-detail.component';
import { BugUpdateComponent } from './update/bug-update.component';
import { BugDeleteDialogComponent } from './delete/bug-delete-dialog.component';
import { BugRoutingModule } from './route/bug-routing.module';

@NgModule({
  imports: [SharedModule, BugRoutingModule],
  declarations: [BugComponent, BugDetailComponent, BugUpdateComponent, BugDeleteDialogComponent],
  entryComponents: [BugDeleteDialogComponent],
})
export class BugModule {}
