import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { BtnLoaderComponent } from '../loaders/btn-loader/btn-loader.component';

@Component({
  selector: 'app-btn-export',
  templateUrl: './btn-export.component.html',
  styles: ``,
  standalone: true,
  styleUrl: './btn-export.component.css',
  imports: [TranslateModule , BtnLoaderComponent],
})
export class BtnExportComponent {
  @Output() export = new EventEmitter<any>();
  @Input() loading : any ;

  onExport() {
    this.export.emit();
  }
}
