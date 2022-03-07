import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IExecution, getExecutionIdentifier } from '../execution.model';

export type EntityResponseType = HttpResponse<IExecution>;
export type EntityArrayResponseType = HttpResponse<IExecution[]>;

@Injectable({ providedIn: 'root' })
export class ExecutionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/executions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(execution: IExecution): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(execution);
    return this.http
      .post<IExecution>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(execution: IExecution): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(execution);
    return this.http
      .put<IExecution>(`${this.resourceUrl}/${getExecutionIdentifier(execution) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(execution: IExecution): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(execution);
    return this.http
      .patch<IExecution>(`${this.resourceUrl}/${getExecutionIdentifier(execution) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IExecution>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IExecution[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addExecutionToCollectionIfMissing(
    executionCollection: IExecution[],
    ...executionsToCheck: (IExecution | null | undefined)[]
  ): IExecution[] {
    const executions: IExecution[] = executionsToCheck.filter(isPresent);
    if (executions.length > 0) {
      const executionCollectionIdentifiers = executionCollection.map(executionItem => getExecutionIdentifier(executionItem)!);
      const executionsToAdd = executions.filter(executionItem => {
        const executionIdentifier = getExecutionIdentifier(executionItem);
        if (executionIdentifier == null || executionCollectionIdentifiers.includes(executionIdentifier)) {
          return false;
        }
        executionCollectionIdentifiers.push(executionIdentifier);
        return true;
      });
      return [...executionsToAdd, ...executionCollection];
    }
    return executionCollection;
  }

  protected convertDateFromClient(execution: IExecution): IExecution {
    return Object.assign({}, execution, {
      creationDate: execution.creationDate?.isValid() ? execution.creationDate.format(DATE_FORMAT) : undefined,
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
      res.body.forEach((execution: IExecution) => {
        execution.creationDate = execution.creationDate ? dayjs(execution.creationDate) : undefined;
      });
    }
    return res;
  }
}
