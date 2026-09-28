import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-no-data',
  standalone: true,
  imports: [CommonModule, NgbModule, TranslateModule],
  templateUrl: './no-data.component.html',
  styles: ``,
})
export class NoDataComponent {
  @Input() message: any;
  @Input() type: any;
  constructor() {}
}
