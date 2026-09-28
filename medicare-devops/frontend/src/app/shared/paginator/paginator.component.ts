import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-paginator',
  standalone: true,
  imports: [NgbPaginationModule, TranslateModule, FormsModule],
  templateUrl: './paginator.component.html',
  styleUrl: './paginator.component.css'
})
export class PaginatorComponent {
  changeSizePage($event: any) {
    this.changeSize.emit($event);
    // console.log(this.pageSize);

  }
  @Output() pageChane = new EventEmitter();
  @Output() changeSize = new EventEmitter();
  @Input() page!: number;
  @Input() pageSize!: number;
  @Input() collectionSize!: number;
  @Input() maxSize!: number;

  changePage($event: any) {
    
    this.pageChane.emit($event)
  }
}
