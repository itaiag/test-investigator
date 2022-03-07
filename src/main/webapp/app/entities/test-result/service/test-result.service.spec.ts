import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { Status } from 'app/entities/enumerations/status.model';
import { FailureClassification } from 'app/entities/enumerations/failure-classification.model';
import { ITestResult, TestResult } from '../test-result.model';

import { TestResultService } from './test-result.service';

describe('TestResult Service', () => {
  let service: TestResultService;
  let httpMock: HttpTestingController;
  let elemDefault: ITestResult;
  let expectedResult: ITestResult | ITestResult[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TestResultService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      creationDate: currentDate,
      testName: 'AAAAAAA',
      method: 'AAAAAAA',
      testProperties: 'AAAAAAA',
      testStatus: Status.SUCCESS,
      failureMessage: 'AAAAAAA',
      failureClassification: FailureClassification.TO_INVESTIGATE,
      fix: 'AAAAAAA',
      comments: 'AAAAAAA',
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

    it('should create a TestResult', () => {
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

      service.create(new TestResult()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TestResult', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          creationDate: currentDate.format(DATE_FORMAT),
          testName: 'BBBBBB',
          method: 'BBBBBB',
          testProperties: 'BBBBBB',
          testStatus: 'BBBBBB',
          failureMessage: 'BBBBBB',
          failureClassification: 'BBBBBB',
          fix: 'BBBBBB',
          comments: 'BBBBBB',
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

    it('should partial update a TestResult', () => {
      const patchObject = Object.assign(
        {
          creationDate: currentDate.format(DATE_FORMAT),
          testName: 'BBBBBB',
          failureMessage: 'BBBBBB',
          fix: 'BBBBBB',
        },
        new TestResult()
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

    it('should return a list of TestResult', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          creationDate: currentDate.format(DATE_FORMAT),
          testName: 'BBBBBB',
          method: 'BBBBBB',
          testProperties: 'BBBBBB',
          testStatus: 'BBBBBB',
          failureMessage: 'BBBBBB',
          failureClassification: 'BBBBBB',
          fix: 'BBBBBB',
          comments: 'BBBBBB',
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

    it('should delete a TestResult', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTestResultToCollectionIfMissing', () => {
      it('should add a TestResult to an empty array', () => {
        const testResult: ITestResult = { id: 123 };
        expectedResult = service.addTestResultToCollectionIfMissing([], testResult);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(testResult);
      });

      it('should not add a TestResult to an array that contains it', () => {
        const testResult: ITestResult = { id: 123 };
        const testResultCollection: ITestResult[] = [
          {
            ...testResult,
          },
          { id: 456 },
        ];
        expectedResult = service.addTestResultToCollectionIfMissing(testResultCollection, testResult);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TestResult to an array that doesn't contain it", () => {
        const testResult: ITestResult = { id: 123 };
        const testResultCollection: ITestResult[] = [{ id: 456 }];
        expectedResult = service.addTestResultToCollectionIfMissing(testResultCollection, testResult);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(testResult);
      });

      it('should add only unique TestResult to an array', () => {
        const testResultArray: ITestResult[] = [{ id: 123 }, { id: 456 }, { id: 52221 }];
        const testResultCollection: ITestResult[] = [{ id: 123 }];
        expectedResult = service.addTestResultToCollectionIfMissing(testResultCollection, ...testResultArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const testResult: ITestResult = { id: 123 };
        const testResult2: ITestResult = { id: 456 };
        expectedResult = service.addTestResultToCollectionIfMissing([], testResult, testResult2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(testResult);
        expect(expectedResult).toContain(testResult2);
      });

      it('should accept null and undefined values', () => {
        const testResult: ITestResult = { id: 123 };
        expectedResult = service.addTestResultToCollectionIfMissing([], null, testResult, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(testResult);
      });

      it('should return initial array if no TestResult is added', () => {
        const testResultCollection: ITestResult[] = [{ id: 123 }];
        expectedResult = service.addTestResultToCollectionIfMissing(testResultCollection, undefined, null);
        expect(expectedResult).toEqual(testResultCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
