import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  inject,
  Output,
  ViewChild,
} from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import {
  NgbModal,
  NgbTypeaheadSelectItemEvent,
} from '@ng-bootstrap/ng-bootstrap';
import {
  debounceTime,
  distinctUntilChanged,
  Observable,
  OperatorFunction,
  Subject,
  switchMap,
  takeUntil,
} from 'rxjs';
// import { ThemeService } from '../../shared/services/theme.service';
import { ConfirmModalComponent } from '../../shared/confirm-modal/confirm-modal.component';
import { StorageService } from '../../shared/services/storage.service';
import { InputGeneralSearchComponent } from '../input-general-search/input-general-search.component';
import { PermissionStore } from '../../stores/permission.store';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  animations: [
    trigger('expandCollapse', [
      state(
        'collapsed',
        style({
          width: '40px',
        })
      ),
      state(
        'expanded',
        style({
          width: '330px',
        })
      ),
      transition('collapsed <=> expanded', [animate('300ms ease-in-out')]),
    ]),
  ],
  standalone: true,
  imports: [InputGeneralSearchComponent , RouterLink ],
})
export class NavbarComponent {
 
  @Output() isMenuOpenChange = new EventEmitter<boolean>();
  private destroy$ = new Subject<void>();
  isMenuOpen = false;
  profileMenuOpen = false;
@ViewChild('navList') navList!: ElementRef<HTMLUListElement>;
@ViewChild('moreDropdown') moreDropdown!: ElementRef<HTMLLIElement>;
@ViewChild('moreMenu') moreMenu!: ElementRef<HTMLUListElement>;
  private router = inject(Router);
  // private themeService= inject(ThemeService)
  private permissionStore = inject(PermissionStore);
  private storageService = inject(StorageService);
  prefixe: string = '';
  role!: any;
  gender!: any;
  menuBlocks: any[] = [];
 nom!: string;
  prenom!: string;
  theme: string = 'light';
  modalOpen = false;
  sidebarOpen = false;
  private modalService = inject(NgbModal);

  hideSearch: boolean = false;

  activeBlock: number | null = null; 
  ngOnInit(): void {
    this.nom = this.permissionStore.firstName() + '';
    this.prenom = this.permissionStore.lastName() + '';
    this.role = this.permissionStore.role();
    this.gender = this.permissionStore.gender();
    // this.menuBlocks = [
    //   {
    //     id: 1,
    //     label: 'Parnters',
    //     link: '/partners',
    //     icon: '',
    //   },
    //   {
    //     id: 2,
    //     label: 'Agents',
    //     link: '/agents',
    //     icon: '',
    //   },
    //   {
    //     id: 3,
    //     label: 'Clients',
    //     link: '/client',
    //     icon: '',
    //   },
    //   {
    //     id: 4,
    //     label: 'Sales',
    //     link: '/sales',
    //     icon: '',
    //   },
    //   {
    //     id: 5,
    //     label: 'Facture',
    //     link: '/invoices',
    //     icon: '',
    //   },
    //   {
    //     id: 6,
    //     label: 'Categories',
    //     link: '/categories',
    //     icon: '',
    //   },
    //   {
    //     id: 7,
    //     label: 'Produits',
    //     link: '/products',
    //     icon: '',
    //   },
    //   {
    //     id: 11,
    //     label: 'Codes Promo',
    //     link: '/codesPromo',
    //     icon: '',
    //   },
    //   {
    //     id: 8,
    //     label: 'Attributes',
    //     link: '/attributes',
    //     icon: '',
    //   },
    //   {
    //     id: 9,
    //     label: 'Permissions',
    //     link: '/permissions',
    //     icon: '',
    //   },

    //   {
    //     id: 10,
    //     label: 'Préfacturation',
    //     link: '/preinvoices',
    //     icon: '',
    //   },
    // ];
    // S'abonner aux événements du Router
    this.router.events
      .pipe(
        takeUntil(this.destroy$) // Gère la désinscription lorsque le composant est détruit
      )
      .subscribe((event) => {
        // Vérifier si l'événement est la fin d'une navigation réussie
        if (event instanceof NavigationEnd) {
          // Fermer le menu si la navigation se termine
          this.profileMenuOpen = false;
        }
      });
  }

  ngOnDestroy(): void {
    // Se désabonner de tous les observables pour éviter les fuites de mémoire
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngAfterViewInit() {
    this.adjustMenu();
  }

  @HostListener('window:resize')
  onResize() {
    this.adjustMenu();
  }

  private adjustMenu() {
    // Check if elements are defined
  if (!this.navList || !this.moreDropdown || !this.moreMenu) {
    return;
  }

  const navList = this.navList.nativeElement;
  const moreDropdown = this.moreDropdown.nativeElement;
  const moreMenu = this.moreMenu.nativeElement;

    const items = Array.from(navList.children).filter(
      (li) => li !== moreDropdown
    );

    // Reset du menu
    moreMenu.innerHTML = '';
    items.forEach((li) => li.classList.remove('d-none'));
    moreDropdown.classList.add('d-none');

    const availableWidth = navList.clientWidth - 50; // marge
    let totalWidth = 0;
    const overflowItems: HTMLElement[] = [];

    items.forEach((li) => {
      totalWidth += li.clientWidth;
      if (totalWidth > availableWidth) {
        overflowItems.push(li as HTMLElement);
      }
    });

    if (overflowItems.length > 0) {
      overflowItems.forEach((li) => {
        li.classList.add('d-none');
        const clone = li.cloneNode(true) as HTMLElement;
        clone.classList.remove('d-none');
        clone.classList.add('dropdown-item');
        moreMenu.appendChild(clone);
      });
      moreDropdown.classList.remove('d-none');
    }
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;

    this.isMenuOpenChange.emit(this.isMenuOpen);
  }

  toggleProfileMenu() {
    this.profileMenuOpen = !this.profileMenuOpen;
  }
 
  toggleBlock(id: number, event: MouseEvent): void {
    event.preventDefault(); // Empêche le comportement par défaut du lien
    if (this.activeBlock === id) {
      this.activeBlock = null; // Ferme le bloc si déjà actif
    } else {
      this.activeBlock = id; // Ouvre le bloc pour l'élément cliqué
    }
  }
  openModal() {
    this.modalOpen = !this.modalOpen;
  }
  closeBlock(): void {
    this.activeBlock = null; // Ferme le bloc
  }

  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
    document.body.classList.toggle('expanded', this.sidebarOpen);
  }
  toggleTheme() {
    this.theme = this.theme === 'light' ? 'dark' : 'light';
    document.body.classList.toggle('dark-mode', this.theme === 'dark');
    localStorage.setItem('theme', this.theme);
  }

  logout() {
    const modalRef = this.modalService.open(ConfirmModalComponent, {});
    modalRef.componentInstance.modalType = 'NavBar';
    modalRef.result
      .then((result) => {
        if (result === 'confirm') {
          this.storageService.removeAll();
          this.storageService.clearSession();
          this.router.navigateByUrl('auth/login');
        }
      })
      .catch((reason) => {});
  }
}
