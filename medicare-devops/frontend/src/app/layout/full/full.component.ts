import { Component, inject, OnInit } from '@angular/core';
import { StorageService } from '../../shared/services/storage.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmModalComponent } from '../../shared/confirm-modal/confirm-modal.component';
import { Router, RouterModule } from '@angular/router';
import { NavbarComponent } from '../../navigation/navbar/navbar.component';
import { SideBarComponent } from '../../navigation/side-bar/side-bar.component';

@Component({
  selector: 'app-full',
  templateUrl: './full.component.html',
  styleUrl: './full.component.css', 
  standalone :true , 
   imports: [RouterModule,SideBarComponent , NavbarComponent],
})
export class FullComponent implements OnInit {
    isMenuOpen = false;
  storageService = inject(StorageService)
    private modalService = inject(NgbModal);
    private router = inject(Router);
    menuBlocks : any[]=   [
      {
        id: 0,
        label: 'Dashboard',
        link: '/' , 
        icon :''
      }, 
       {
        id: 3,
        label: 'Clients',
        link: '/clients' , 
        icon :''
      },
       {
        id: 12,
        label: 'Chats',
        link: '/chat' , 
        icon :'/icons/message.svg'
      },
      {
        id: 2,
        label: 'Agents',
        link: '/agents' , 
        icon :''
      }, 
     
      {
        id: 4,
        label: 'Sales',
        link: '/sales' , 
        icon :''
      },
      
      {
        id: 5,
        label: 'Facture',
        link: '/invoices' , 
        icon :''
      }, 
       
      {
        id: 7,
        label: 'Produits',
        link: '/products' , 
        icon :''
      }, 
      {
        id: 8,
        label: 'Préfacturation',
        link: '/preinvoices' , 
        icon :'/'
      }, 
     
    ] 
  ngOnInit(): void {
  

    if (this.storageService.getEntrepriseID() === '1' ){
this.menuBlocks.push(   {
        id: 1,
        label: 'Parnters',
        link: '/partners' , 
        icon :''
      },  {
        id: 11,
        label: 'Codes Promo',
        link: '/codesPromo' , 
        icon :''
      }, 
      {
        id: 6,
        label: 'Categories',
        link: '/categories' , 
        icon :'/icons/message.svg'
      },  
       {
        id: 12,
        label: 'Badge',
        link: '/badages' , 
        icon :''
      },  {
        id: 8,
        label: 'Attributes',
        link: '/attributes' , 
        icon :''
      }, 
      {
        id: 9,
        label: 'Permissions',
        link: '/settings' , 
        icon :''
      },
      )
    }
  }
logout() {
    const modalRef = this.modalService.open(ConfirmModalComponent, {

    });
    modalRef.componentInstance.modalType = 'NavBar';
    modalRef.result
      .then((result) => {
        if (result === 'delete') {
          this.storageService.removeAll();
          this.storageService.clearSession()
          this.router.navigateByUrl('auth/login');
        }
    
      })
      .catch((reason) => {
      });
    
  }
   isChatOpen = false;

  toggleChat() {
    this.isChatOpen = !this.isChatOpen;
  }
 openChat() { 
  const chatWidget = document.getElementById('chat-widget');
  if (chatWidget) {
    chatWidget.style.display = 'block';
  } 
 }
}
