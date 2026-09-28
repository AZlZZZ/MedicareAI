import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ClientService } from '../../../../shared/services/client.service';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-block-modal',
  templateUrl: './block-modal.component.html',
  styleUrl: './block-modal.component.css',
  styles: ``,
  standalone: true ,
  imports: [TranslateModule , CommonModule , ReactiveFormsModule]
})
export class BlockModalComponent {
  active = 1;
  @Input() data: any;
  @Output() modalClosed = new EventEmitter<any>();
  @Input() type: any;

  loading: boolean = false;
  durationOptions = [
    { label: 'DAY1', value: 1 },
    { label: 'DAY3', value: 3 },
    { label: 'PERMANENT', value: -1 }
  ]
  causes_blockage: any[] = []
  currentLang: string = "FR"
  activeIndex: number | null = null;
  form!: FormGroup;
  constructor(
    public activeModal: NgbActiveModal,
    private fb: FormBuilder,
    private apis: ClientService,
    private translate: TranslateService


  ) {
    this.form = this.fb.group({
      cause: [null, Validators.required],
      duration: [null],
      message: [null]
    });
  }

  ngOnInit(): void {
    this.currentLang = this.translate.currentLang || this.translate.getDefaultLang();
    // console.log('Current language:', this.currentLang);
    if (this.type != 'RACE') {
      this.form.get('duration')?.setValidators(Validators.required);
    }
    this.getReasons();
  }
  // 
  getReasons() {
    const queryParams: any[] = [];

      queryParams.push({
        key: 'reasonType',
        operation: 'EQUALITY',
        value: 'BLOCK_USER',
        criteriaType: 'REASON_TYPE',
      });
      queryParams.push({
        key: 'language',
        operation: 'EQUALITY',
        value: this.currentLang.toUpperCase(),
        criteriaType: 'LANGUAGE',
      });
    

this.loading = true;

    const encodedQuery = encodeURIComponent(JSON.stringify(queryParams));
    this.apis
      .getAllReasons(encodedQuery)
      .subscribe(
        (res: any) => {
          this.loading=false;
          if( res.code == 200 ) {
          this.causes_blockage = res.data
          }
        },
        (error: any) => {
          console.error('Error fetching filtered historical data:', error);
        }
      );
  }


  setActive(index: number): void {
    this.activeIndex = index;
    this.form.patchValue({ cause: this.causes_blockage[index].reason });
  }

  getMaxLenght() {
    if (this.form.value.cause) return (240 - this.form.value.cause.length)
    return 240;
  }
  save() {
    // console.log(' blocked send ')
    if (this.form.valid) {
      this.modalClosed.emit('close');
      this.activeModal.close({ state: 'Valid', data: this.form.value });

    } else {
      console.log('Form is invalid');
    }
  }

  dismissModal() {
    this.modalClosed.emit('dismiss');
    this.activeModal.dismiss('Modal Dismissed');
  }
  get message() {
    return this.form.get('message');
  }
}
