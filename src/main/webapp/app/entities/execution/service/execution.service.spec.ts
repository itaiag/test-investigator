import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IExecution, Execution } from '../execution.model';

import { ExecutionService } from './execution.service';

describe('Execution Service', () => {
  let service: ExecutionService;
  let httpMock: HttpTestingController;
  let elemDefault: IExecution;
  let expectedResult: IExecution | IExecution[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ExecutionService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      creationDate: currentDate,
      executionDescription: 'AAAAAAA',
      executionProperties: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          creationDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Execution', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          creationDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          creationDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Execution()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Execution', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          creationDate: currentDate.format(DATE_FORMAT),
          executionDescription: 'BBBBBB',
          executionProperties: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          creationDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Execution', () => {
      const patchObject = Object.assign(
        {
          creationDate: currentDate.format(DATE_FORMAT),
          executionDescription: 'BBBBBB',
        },
        new Execution()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          creationDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Execution', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          creationDate: currentDate.format(DATE_FORMAT),
          executionDescription: 'BBBBBB',
          executionProperties: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          creationDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Execution', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addExecutionToCollectionIfMissing', () => {
      it('should add a Execution to an empty array', () => {
        const execution: IExecution = { id: 123 };
        expectedResult = service.addExecutionToCollectionIfMissing([], execution);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(execution);
      });

      it('should not add a Execution to an array that contains it', () => {
        const execution: IExecution = { id: 123 };
        const executionCollection: IExecution[] = [
          {
            ...execution,
          },
          { id: 456 },
        ];
        expectedResult = service.addExecutionToCollectionIfMissing(executionCollection, execution);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Execution to an array that doesn't contain it", () => {
        const execution: IExecution = { id: 123 };
        const executionCollection: IExecution[] = [{ id: 456 }];
        expectedResult = service.addExecutionToCollectionIfMissing(executionCollection, execution);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(execution);
      });

      it('should add only unique Execution to an array', () => {
        const executionArray: IExecution[] = [{ id: 123 }, { id: 456 }, { id: 28972 }];
        const executionCollection: IExecution[] = [{ id: 123 }];
        expectedResult = service.addExecutionToCollectionIfMissing(executionCollection, ...executionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const execution: IExecution = { id: 123 };
        const execution2: IExecution = { id: 456 };
        expectedResult = service.addExecutionToCollectionIfMissing([], execution, execution2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(execution);
        expect(expectedResult).toContain(execution2);
      });

      it('should accept null and undefined values', () => {
        const execution: IExecution = { id: 123 };
        expectedResult = service.addExecutionToCollectionIfMissing([], null, execution, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(execution);
      });

      it('should return initial array if no Execution is added', () => {
        const executionCollection: IExecution[] = [{ id: 123 }];
        expectedResult = service.addExecutionToCollectionIfMissing(executionCollection, undefined, null);
        expect(expectedResult).toEqual(executionCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
