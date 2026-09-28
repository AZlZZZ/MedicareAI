import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule, NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../environments/environment.development';
import { TranslateModule } from '@ngx-translate/core';
import { PaginatorComponent } from '../paginator/paginator.component';
import { LoaderComponent } from '../loaders/loader/loader.component';
import { NgxStarsModule } from "ngx-stars";

@Component({
  selector: 'app-reusable-table',
  standalone: true,
  imports: [NgbPaginationModule, FormsModule, NgbModule, CommonModule, TranslateModule, PaginatorComponent, LoaderComponent, NgxStarsModule],
  templateUrl: './reusable-table.component.html',
  styles: ``
})
export class ReusableTableComponent {
  @Input() displayedColumns: string[] = [];
    @Output() enableActions = new EventEmitter<any[]>();  
    expandedRowId: number | null = null; 
  @Input() name: string = '';
  @Input() totalPages!: number;
  @Input() prefixe!: string;
  @Input() dataSource: any[] = [];
  @Input() dataColumns: any[] = []; // les columns à afficher
  @Input() numberOfDriver!: number;
  @Input() loading: boolean = false;
  @Input() roleRestrict = false;

  @Output() edit = new EventEmitter<any>(); // EventEmitter for edit action
  @Output() delete = new EventEmitter<any>(); // EventEmitter for delete actio
  @Output() details = new EventEmitter<any>(); // EventEmitter for delete actio
  @Output() increment = new EventEmitter<any>();
  @Output() decrement = new EventEmitter<any>();
  @Output() update = new EventEmitter<any>();
  @Output() deleteItem = new EventEmitter<any>();
  @Output() pagelength = new EventEmitter<any>(); // nombre de la page
  @Output() size = new EventEmitter<any>(); // nombre d'élements par  page
  @Output() totalCalculated = new EventEmitter<any>();
  url : string = environment.urlFiles;
  @Input() canEnable = true;
  @Output() chnageStatePromoCode = new EventEmitter<any>(); // EventEmitter for delete actio

 // pageSize: number = 1;

  // Pagination parameters
  // page: number = 1;
  @Input() page: number = 1;
  @Input() pageSize: number = 5;
  collectionSize: number = 0;
  maxSize: number = 5;

  ngOnInit() {
    // Pagination parameters
  }
  // ngAfterViewInit() {
  //   this.page = 1

  //   this.maxSize = 5;
  // }
  constructor() {

  }
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['loading']) {
      if (this.loading) {
      }
      if (changes["dataSource"]) {
        this.dataSource = changes["dataSource"].currentValue;
        // console.log(changes["dataSource"].currentValue);
      }
      if (changes["totalPages"] && changes["totalPages"].currentValue !== undefined) {
        this.collectionSize = changes["totalPages"].currentValue;
        // console.log(changes["totalPages"].currentValue);
      }
    }
  }

 

  trackByFn(index: number, item: any): any {
    return item ? item.id : undefined;
  }


 
  updateItem(element: any) {
    this.update.emit(element);
  }
  onDeleteItem(element: any){
    this.deleteItem.emit(element);
  }



  getButtonClass(status: string): string {
    // const isSelected = this.selectedStatuses.some((s) => s.value === status);
    switch (status) {
      case 'SUPER_ADMIN' : 
      return 'color: #1248ED; background: #ccddfd'
      case 'ADMIN':
        case 'IN_PROGRESS':
        case 'IN_PROGRESS_ADMIN' : 
        return 'color: #04cd80; background: #cdf5da' 
      case 'AGENT':
        case "ON_HOLD_ADMIN" : 
        return 'color: #FF8D3C; background: #fdf2cc ; '
      case 'DELIVERED':
        return 'color: #FF8D3C; border: 1px solid #FF8D3C   ;  '
      case 'COMPLETED':
        return 'color: #04ca47; border: 1px solid #04ca47  ;  '
      case "CANCELED":
        return 'color: #E4626F ; border: 1px solid #E4626F ; '
      // case "AGENT":
        // case 'ON_HOLD_ADMIN' : 
        // return 'color: #01b3e6; background: #ccf0fa ; '
      // case 'CANCELED':
      case 'CANCELED_DRIVER':
      case 'CANCELED_ADMIN':
        return 'background: #E4626F;' 
      case 'END':
        case 'END_ADMIN':
        return 'background: var(--Success-Success200, #15B097);' 
      default:
        return  'background: var(--Success-Success200,rgb(120, 126, 125)) ;'
    }
  }

  onEdit(element: any) {
    this.edit.emit(element);
  }
  onDelete(element: any) {
    this.delete.emit(element);
  }
  onDetails(element: any) {
    this.details.emit(element);
  }
  changePage(value: any) {
    this.pagelength.emit(value)
  }
  changeSizePage(value: any) {
    this.size.emit(value.target.value);

  }

selectedItems:any [] =  []
   toggleSelectAll(event: any) {
    const checked = event.target.checked;

    this.dataSource.forEach((item) => {
      item.selected = checked;
    });

    this.selectedItems = checked ? [...this.dataSource] : [];
    console.log(this.selectedItems);
    this.enableActions.emit(this.selectedItems);
  }

  onSelectOne(item: any) {
    if (item.selected) {
      this.selectedItems.push(item);
    } else {
      this.selectedItems = this.selectedItems.filter((i) => i.id !== item.id);
    }

    this.enableActions.emit(this.selectedItems);
  }
  toggleRow(item: any): void {
    // Supposons que chaque élément a une propriété unique 'id'
    if (this.expandedRowId === item.id) {
      this.expandedRowId = null; // Collapse
    } else {
      this.expandedRowId = item.id; // Expand
    }
  }

  isExpanded(item: any): boolean {
    return this.expandedRowId === item.id;
  } 
  openModalListAgents(item: any) {
    // this.openCollabModal.emit(item);
    // console.log(id);
  } 

  // Empêcher l'ouverture de la ligne lors du clic sur un bouton
editRole(role: any) {
  console.log("Edition du rôle:", role.name);
  // Votre logique de modal ici
}

deleteRole(role: any) {
  if(confirm(`Supprimer le rôle ${role.name} ?`)) {
    // Votre logique de suppression
  }
}

toggleFunction(role: any, func: any) {
  func.attached = !func.attached;
  console.log(`Fonction ${func.name} modifiée pour ${role.name}`);
  // Appel API ici pour sauvegarder le changement
} 
}
