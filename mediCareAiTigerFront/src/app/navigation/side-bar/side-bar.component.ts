import { Component, inject, Input, OnChanges, SimpleChanges } from '@angular/core';
import { StorageService } from '../../shared/services/storage.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrl: './side-bar.component.css',
  standalone: true,
  imports: [RouterModule, CommonModule]
})
export class SideBarComponent implements OnChanges {
  @Input() isMenuOpen = false;

  storageService = inject(StorageService);
  private router = inject(Router);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isMenuOpen']) {
      this.isMenuOpen = changes['isMenuOpen'].currentValue;
    }
  }

  get menuBlocks(): any[] {
    const userType = this.storageService.getUserType();
    const base = [
      { id: 6, label: 'Records',      link: '/records',      icon: '/icons/users-icon.svg' },
      { id: 7, label: 'Appointments', link: '/appointments', icon: '/icons/users-icon.svg' },
    ];

    if (userType === 'AGENT') {
      return [
        { id: 2, label: 'Clients',      link: '/clients',      icon: '/icons/users-icon.svg' },
        { id: 4, label: 'Agents',       link: '/agents',       icon: '/icons/users-icon.svg' },
        { id: 5, label: 'Doctors',      link: '/doctors',      icon: '/icons/users-icon.svg' },
        { id: 8, label: 'Medicaments',  link: '/medicaments',  icon: '/icons/users-icon.svg' },
        ...base,
      ];
    }

    if (userType === 'DOCTOR') {
      return [
        { id: 2, label: 'Patients',     link: '/clients',      icon: '/icons/users-icon.svg' },
        { id: 8, label: 'Medicaments',  link: '/medicaments',  icon: '/icons/users-icon.svg' },
        { id: 9, label: 'Schedules',    link: '/schedules',    icon: '/icons/users-icon.svg' },
        ...base,
      ];
    }

    return [
      { id: 9, label: 'Schedules', link: '/schedules', icon: '/icons/users-icon.svg' },
      ...base,
    ];
  }
}
