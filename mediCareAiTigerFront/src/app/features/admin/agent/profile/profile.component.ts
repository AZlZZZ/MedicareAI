import { Component, inject, Input } from '@angular/core';
import { NgbActiveModal, NgbModal, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { AgentService } from '../../../../shared/services/agent.service';
import { ActivatedRoute } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AgentModalComponent } from '../agent-modal/agent-modal.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styles: `` , 
  standalone:true , 
  imports : [TranslateModule , CommonModule ,NgbNavModule ]
})
export class ProfileComponent {
  @Input() data  :any ; 
  // agentId: any;
  userInfo!: any;
  isChecked = true;
  active = 1;
  private modalService = inject(NgbModal);
  constructor(private api: AgentService, private route: ActivatedRoute ,  private translate: TranslateService,  public activeModal: NgbActiveModal) {}
  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      // this.agentId = +params['id'];
      this.getAgentById();
    });
   
  }
  edit_agent(){
    const modalRef = this.modalService.open(AgentModalComponent, {
      size: 'xl',
      centered: true,
    });
  modalRef.componentInstance.data = this.userInfo;
    modalRef.result
    .then((result) => {
     
    })
    .catch((reason) => {
    }); 
  }
  getAgentById() {
    this.api.getAgentInfo(this.data?.id).subscribe((res: any) => {
      if (res.code == 200) {
        this.userInfo = res.data;         
      } else if (res.code == 400) {
        console.log(' Bad request');
      } else if (res.code == 401) {
        console.log('Unauthorized');
      } else if (res.code == 3003) {
        console.log('Agent not  found ');
      } else if (res.code == 500) {
        console.log('Internal server error. ');
      }
    });
  }
  // onBlock() {
  //   this.userInfo.state = 'BLOCKED';

  //   // this.blockuser.emit(this.data);
  //   const modalRef = this.modalService.open(BlockModalComponent, {
  //     size: 'lg',
  //     centered: true,
  //   });
  //   modalRef.componentInstance.data = this.userInfo;
  //   modalRef.result
  //     .then((result) => {
  //       // This block is executed when the modal is closed with close()
  //       // console.log('Modal closed with result:', result);
  //       this.userInfo.state = 'BLOCKED'; // Update state if needed
  //       //this.blockModalClosed.emit('close');  // Notify parent that modal was closed
  //     })
  //     .catch((reason) => {
  //       // This block is executed when the modal is dismissed with dismiss()
  //       // console.log('Modal dismissed:', reason);
  //       this.userInfo.state = 'ENABLE';
  //       //this.blockModalClosed.emit('dismiss');  // Notify parent that modal was dismissed
  //     });
  // }
  getBackgroundColor(): string {
    return this.userInfo?.state === 'BLOCKED'
      ? 'rgba(228, 98, 111, 1)'
      : 'var(--Success-Success200, rgba(21, 176, 151, 1))';
  }

  isToggleDisabled(): boolean {
    return this.userInfo?.state === 'BLOCKED';
  }
  onSaveChanges() {
    const modalRef = this.modalService.open(AgentModalComponent, {
      size: 'lg',
      centered: true,
    });
    modalRef.componentInstance.data = this.userInfo;
  }
  dismissModal() {
    this.activeModal.dismiss('Modal Dismissed');
  }
  edit(){
    this.activeModal.dismiss('Modal Dismissed'); 
    const modalRef = this.modalService.open(AgentModalComponent, {
      size: 'xl',
      centered: true,
    });
    modalRef.componentInstance.data = this.userInfo;
  }
}
