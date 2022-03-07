import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { BugStatus } from 'app/entities/enumerations/bug-status.model';
import { IBug, Bug } from '../bug.model';

import { BugService } from './bug.service';

describe('Bug Service', () => {
  let service: BugService;
  let httpMock: HttpTestingController;
  let elemDefault: IBug;
  let expectedResult: IBug | IBug[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BugService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      bugName: 'AAAAAAA',
      description: 'AAAAAAA',
      status: BugStatus.OPEN,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Bug', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Bug()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Bug', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          bugName: 'BBBBBB',
          description: 'BBBBBB',
          status: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Bug', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
        },
        new Bug()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Bug', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          bugName: 'BBBBBB',
          description: 'BBBBBB',
          status: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Bug', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addBugToCollectionIfMissing', () => {
      it('should add a Bug to an empty array', () => {
        const bug: IBug = { id: 123 };
        expectedResult = service.addBugToCollectionIfMissing([], bug);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bug);
      });

      it('should not add a Bug to an array that contains it', () => {
        const bug: IBug = { id: 123 };
        const bugCollection: IBug[] = [
          {
            ...bug,
          },
          { id: 456 },
        ];
        expectedResult = service.addBugToCollectionIfMissing(bugCollection, bug);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Bug to an array that doesn't contain it", () => {
        const bug: IBug = { id: 123 };
        const bugCollection: IBug[] = [{ id: 456 }];
        expectedResult = service.addBugToCollectionIfMissing(bugCollection, bug);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bug);
      });

      it('should add only unique Bug to an array', () => {
        const bugArray: IBug[] = [{ id: 123 }, { id: 456 }, { id: 11256 }];
        const bugCollection: IBug[] = [{ id: 123 }];
        expectedResult = service.addBugToCollectionIfMissing(bugCollection, ...bugArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const bug: IBug = { id: 123 };
        const bug2: IBug = { id: 456 };
        expectedResult = service.addBugToCollectionIfMissing([], bug, bug2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bug);
        expect(expectedResult).toContain(bug2);
      });

      it('should accept null and undefined values', () => {
        const bug: IBug = { id: 123 };
        expectedResult = service.addBugToCollectionIfMissing([], null, bug, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bug);
      });

      it('should return initial array if no Bug is added', () => {
        const bugCollection: IBug[] = [{ id: 123 }];
        expectedResult = service.addBugToCollectionIfMissing(bugCollection, undefined, null);
        expect(expectedResult).toEqual(bugCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
