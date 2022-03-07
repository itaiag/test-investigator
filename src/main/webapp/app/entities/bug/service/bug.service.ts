import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBug, getBugIdentifier } from '../bug.model';

export type EntityResponseType = HttpResponse<IBug>;
export type EntityArrayResponseType = HttpResponse<IBug[]>;

@Injectable({ providedIn: 'root' })
export class BugService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/bugs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(bug: IBug): Observable<EntityResponseType> {
    return this.http.post<IBug>(this.resourceUrl, bug, { observe: 'response' });
  }

  update(bug: IBug): Observable<EntityResponseType> {
    return this.http.put<IBug>(`${this.resourceUrl}/${getBugIdentifier(bug) as number}`, bug, { observe: 'response' });
  }

  partialUpdate(bug: IBug): Observable<EntityResponseType> {
    return this.http.patch<IBug>(`${this.resourceUrl}/${getBugIdentifier(bug) as number}`, bug, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBug>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBug[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBugToCollectionIfMissing(bugCollection: IBug[], ...bugsToCheck: (IBug | null | undefined)[]): IBug[] {
    const bugs: IBug[] = bugsToCheck.filter(isPresent);
    if (bugs.length > 0) {
      const bugCollectionIdentifiers = bugCollection.map(bugItem => getBugIdentifier(bugItem)!);
      const bugsToAdd = bugs.filter(bugItem => {
        const bugIdentifier = getBugIdentifier(bugItem);
        if (bugIdentifier == null || bugCollectionIdentifiers.includes(bugIdentifier)) {
          return false;
        }
        bugCollectionIdentifiers.push(bugIdentifier);
        return true;
      });
      return [...bugsToAdd, ...bugCollection];
    }
    return bugCollection;
  }
}
