import { AfterViewInit, Directive, ElementRef, EventEmitter, OnDestroy, Output } from '@angular/core';

@Directive({
  selector: '[appIntersectionObserver]'
})
export class IntersectionObserverDirective implements AfterViewInit, OnDestroy{
  @Output()
  inView: EventEmitter<void> = new EventEmitter<void>()

  private observer!: IntersectionObserver

  constructor(private element: ElementRef) { }

  ngAfterViewInit(): void {
    this.observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) this.inView.emit()
      })
    })

    this.observer.observe(this.element.nativeElement)
  }

  ngOnDestroy(): void {
    if (this.observer){
      this.observer.disconnect()
    }
  }

}
