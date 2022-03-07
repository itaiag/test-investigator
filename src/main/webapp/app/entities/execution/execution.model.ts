import dayjs from 'dayjs/esm';
import { ITestResult } from 'app/entities/test-result/test-result.model';

export interface IExecution {
  id?: number;
  creationDate?: dayjs.Dayjs | null;
  executionDescription?: string | null;
  executionProperties?: string | null;
  testResults?: ITestResult[] | null;
}

export class Execution implements IExecution {
  constructor(
    public id?: number,
    public creationDate?: dayjs.Dayjs | null,
    public executionDescription?: string | null,
    public executionProperties?: string | null,
    public testResults?: ITestResult[] | null
  ) {}
}

export function getExecutionIdentifier(execution: IExecution): number | undefined {
  return execution.id;
}
