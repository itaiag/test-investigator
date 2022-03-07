import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TestResultComponent } from './list/test-result.component';
import { TestResultDetailComponent } from './detail/test-result-detail.component';
import { TestResultUpdateComponent } from './update/test-result-update.component';
import { TestResultDeleteDialogComponent } from './delete/test-result-delete-dialog.component';
import { TestResultRoutingModule } from './route/test-result-routing.module';

@NgModule({
  imports: [SharedModule, TestResultRoutingModule],
  declarations: [TestResultComponent, TestResultDetailComponent, TestResultUpdateComponent, TestResultDeleteDialogComponent],
  entryComponents: [TestResultDeleteDialogComponent],
})
export class TestResultModule {}
