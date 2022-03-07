import { ITestResult } from 'app/entities/test-result/test-result.model';
import { BugStatus } from 'app/entities/enumerations/bug-status.model';

export interface IBug {
  id?: number;
  bugName?: string | null;
  description?: string | null;
  status?: BugStatus | null;
  testResults?: ITestResult[] | null;
}

export class Bug implements IBug {
  constructor(
    public id?: number,
    public bugName?: string | null,
    public description?: string | null,
    public status?: BugStatus | null,
    public testResults?: ITestResult[] | null
  ) {}
}

export function getBugIdentifier(bug: IBug): number | undefined {
  return bug.id;
}
