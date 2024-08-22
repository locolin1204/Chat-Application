import { IntersectionObserverDirective } from './intersectionObserver.directive';
import { ElementRef } from "@angular/core";

describe('IntersectionObserverDirective', () => {
  it('should create an instance', () => {
    const mockElementRef = new ElementRef(document.createElement('div'));
    const directive = new IntersectionObserverDirective(mockElementRef);
    expect(directive).toBeTruthy();
  });
});
