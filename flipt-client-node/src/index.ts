import { Evaluation } from './evaluation';

export class FliptClient {
  public evaluation: Evaluation;

  public constructor(
    url: string = 'http://localhost:8080',
    token: string = '',
    timeout: number = 60
  ) {
    this.evaluation = new Evaluation(url, token, timeout);
  }
}
