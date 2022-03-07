import dayjs from 'dayjs/esm';
import { IExecution } from 'app/entities/execution/execution.model';
import { IBug } from 'app/entities/bug/bug.model';
import { Status } from 'app/entities/enumerations/status.model';
import { FailureClassification } from 'app/entities/enumerations/failure-classification.model';

export interface ITestResult {
  id?: number;
  creationDate?: dayjs.Dayjs | null;
  testName?: string;
  method?: string | null;
  testProperties?: string | null;
  testStatus?: Status | null;
  failureMessage?: string | null;
  failureClassification?: FailureClassification | null;
  fix?: string | null;
  comments?: string | null;
  execution?: IExecution | null;
  bug?: IBug | null;
}

export class TestResult implements ITestResult {
  constructor(
    public id?: number,
    public creationDate?: dayjs.Dayjs | null,
    public testName?: string,
    public method?: string | null,
    public testProperties?: string | null,
    public testStatus?: Status | null,
    public failureMessage?: string | null,
    public failureClassification?: FailureClassification | null,
    public fix?: string | null,
    public comments?: string | null,
    public execution?: IExecution | null,
    public bug?: IBug | null
  ) {}
}

export function getTestResultIdentifier(testResult: ITestResult): number | undefined {
  return testResult.id;
}
