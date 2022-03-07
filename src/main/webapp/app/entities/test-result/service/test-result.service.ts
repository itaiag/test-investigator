import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITestResult, getTestResultIdentifier } from '../test-result.model';

export type EntityResponseType = HttpResponse<ITestResult>;
export type EntityArrayResponseType = HttpResponse<ITestResult[]>;

@Injectable({ providedIn: 'root' })
export class TestResultService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/test-results');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(testResult: ITestResult): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(testResult);
    return this.http
      .post<ITestResult>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(testResult: ITestResult): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(testResult);
    return this.http
      .put<ITestResult>(`${this.resourceUrl}/${getTestResultIdentifier(testResult) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(testResult: ITestResult): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(testResult);
    return this.http
      .patch<ITestResult>(`${this.resourceUrl}/${getTestResultIdentifier(testResult) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ITestResult>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITestResult[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTestResultToCollectionIfMissing(
    testResultCollection: ITestResult[],
    ...testResultsToCheck: (ITestResult | null | undefined)[]
  ): ITestResult[] {
    const testResults: ITestResult[] = testResultsToCheck.filter(isPresent);
    if (testResults.length > 0) {
      const testResultCollectionIdentifiers = testResultCollection.map(testResultItem => getTestResultIdentifier(testResultItem)!);
      const testResultsToAdd = testResults.filter(testResultItem => {
        const testResultIdentifier = getTestResultIdentifier(testResultItem);
        if (testResultIdentifier == null || testResultCollectionIdentifiers.includes(testResultIdentifier)) {
          return false;
        }
        testResultCollectionIdentifiers.push(testResultIdentifier);
        return true;
      });
      return [...testResultsToAdd, ...testResultCollection];
    }
    return testResultCollection;
  }

  protected convertDateFromClient(testResult: ITestResult): ITestResult {
    return Object.assign({}, testResult, {
      creationDate: testResult.creationDate?.isValid() ? testResult.creationDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.creationDate = res.body.creationDate ? dayjs(res.body.creationDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((testResult: ITestResult) => {
        testResult.creationDate = testResult.creationDate ? dayjs(testResult.creationDate) : undefined;
      });
    }
    return res;
  }
}
