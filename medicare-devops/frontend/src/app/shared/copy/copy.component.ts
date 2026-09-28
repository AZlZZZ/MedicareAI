import { Component, Input } from '@angular/core';
import { Clipboard } from '@angular/cdk/clipboard';

@Component({
  selector: 'app-copy',
  templateUrl: './copy.component.html',
  styleUrl: './copy.component.css',
  standalone: true
})
export class CopyComponent {
  @Input() prefixe!: string;
  @Input() id!: number;
  iconSrc: string = 'assets/icons/copy-icon.svg';
  constructor(private clipboard: Clipboard) {

  }
  copyToClipboard(): void {
    this.clipboard.copy(`${this.prefixe}-${this.id}`);
    this.iconSrc = 'assets/icons/copied-icon.svg';
    setTimeout(()=>{
        this.iconSrc = 'assets/icons/copy-icon.svg';
  
    },1000)
    
  }
}
