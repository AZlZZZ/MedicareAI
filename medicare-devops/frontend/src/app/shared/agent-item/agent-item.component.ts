import { Component, EventEmitter, Input, Output } from '@angular/core';
import { environment } from '../../../environments/environment.development';


@Component({
  selector: 'app-agent-item',
  standalone: true,
  imports: [],
  templateUrl: './agent-item.component.html',
  styleUrl: './agent-item.component.css'
})
export class AgentItemComponent {
 @Input() user: any;
 @Input() type: any;
  url = environment.urlFiles;

  @Output() removeCollaborator = new EventEmitter<any>();

  remove() {
    this.removeCollaborator.emit(this.user);
  }
}
