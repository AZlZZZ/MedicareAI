import { Component, EventEmitter, Input, OnChanges, Output, output, SimpleChanges } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { BtnLoaderComponent } from '../loaders/btn-loader/btn-loader.component';

@Component({
  selector: 'app-save-button',
  standalone: true,
  imports: [BtnLoaderComponent, TranslateModule],
  templateUrl: './save-button.component.html',
  styleUrl: './save-button.component.css'
})
export class SaveButtonComponent {
 
  @Input() loading!: boolean;
  @Output() save = new EventEmitter();

  handleClick() {
    this.save.emit();
  }


}
