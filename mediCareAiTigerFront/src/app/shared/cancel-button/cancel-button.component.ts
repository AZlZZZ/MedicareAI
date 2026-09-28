import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-cancel-button',
  standalone: true,
  imports: [],
  templateUrl: './cancel-button.component.html',
  styleUrl: './cancel-button.component.css'
})
export class CancelButtonComponent {
  @Input() loading!: boolean;
  @Output() cancel = new EventEmitter();

  handleClick() {
    this.cancel.emit();
  }
}
