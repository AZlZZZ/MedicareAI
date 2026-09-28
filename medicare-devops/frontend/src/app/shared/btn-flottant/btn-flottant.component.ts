import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-btn-flottant',
  templateUrl: './btn-flottant.component.html',
  styleUrl: './btn-flottant.component.css',
  standalone: true
})
export class BtnFlottantComponent {


  @Output() add = new EventEmitter<any>();


  onAdd() {
    this.add.emit();
  }
}
