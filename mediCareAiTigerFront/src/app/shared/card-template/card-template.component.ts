import { CommonModule } from '@angular/common';
import { Component, inject, Input, TemplateRef } from '@angular/core';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { environment } from '../../../environments/environment.development';
import { LoaderComponent } from "../loaders/loader/loader.component";

@Component({
  selector: 'app-card-template',
  templateUrl: './card-template.component.html',
  styleUrl: './card-template.component.css',
  styles: ``,
  standalone: true,
  imports: [TranslateModule, NgbModule, CommonModule ,
     LoaderComponent],
})
export class CardTemplateComponent {
  @Input() data: any;
  @Input() loading: any;
  url: string = environment.urlFiles;
 private modalService = inject(NgbModal);
  selectedFile!: any; 
  constructor(translate: TranslateService) {}

  openFile(content: TemplateRef<any>, url_data: any) {
    this.selectedFile = null;
    this.selectedFile = url_data
      ? this.url + url_data
      : 'assets/images/doc-default.png';
    this.modalService
      .open(content, {
        ariaLabelledBy: 'modal-basic-title',
        size: 'lg',
        centered: true,
      })
      .result.then(
        (result) => {},
        (reason) => {}
      );
  }
}
