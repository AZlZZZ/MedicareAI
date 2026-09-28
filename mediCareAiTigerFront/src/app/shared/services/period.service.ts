import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})

// C'est un service  de calcul de périodes dynamiques sont typiquement réutilisables et indépendantes de l’affichage
export class PeriodService {

  constructor() { }


  updatePeriod(value: string): string[] {
    switch (value) {
      case 'CURRENT_WEEK':
        return this.getCurrentWeekDays()
        break;

      case 'LAST_THREE_WEEKS':
        return this.getLastThreeWeeks()
        break;

      case 'CURRENT_MONTH':
        return this.getWeeksFromStartOfMonthToToday()
        break;

      case 'LAST_THREE_MONTHS':
        return this.getLastThreeMonths();
        break;

      case 'LAST_SIX_MONTHS':
        return this.getLastSixMonths();
        break;

      default:
        console.log('Statut non reconnu');
        return ['Erreur'];
        break;
    }
  }

  getCurrentWeekDays(): string[] {
    const today = new Date();

    // Trouver le lundi de la semaine
    const monday = new Date(today);
    const day = monday.getDay(); // 0 = dimanche, 1 = lundi, ...
    const diffToMonday = day === 0 ? -6 : 1 - day;
    monday.setDate(today.getDate() + diffToMonday);

    const result: string[] = [];

    for (let i = 0; i < 7; i++) {
      const day = new Date(monday);
      day.setDate(monday.getDate() + i);

      result.push(day.toLocaleDateString('fr-FR')); // Format: "17/04/2025"
    }

    return result;
  }

  getLastSixMonths(): string[] {
    const monthNames = [
      "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
      "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    ];

    const result: string[] = [];
    const today = new Date();

    for (let i = 0; i < 6; i++) {
      const date = new Date(today.getFullYear(), today.getMonth() - i, 1);
      const label = `${monthNames[date.getMonth()]}`;
      result.push(label);
    }

    return result.reverse(); // pour avoir de l'ancien au plus récent
  }

  getLastThreeMonths(): string[] {
    const monthNames = [
      "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
      "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    ];

    const result: string[] = [];
    const today = new Date();

    for (let i = 0; i < 3; i++) {
      const date = new Date(today.getFullYear(), today.getMonth() - i, 1);
      const label = `${monthNames[date.getMonth()]}`;
      result.push(label);
    }

    return result.reverse(); // pour avoir de l'ancien au plus récent
  }

  getWeeksFromStartOfMonthToToday(): string[] {
    const result: string[] = [];

    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth(); // Avril = 3
    const startOfMonth = new Date(year, month, 1);

    let currentStart = new Date(startOfMonth);
    let weekIndex = 1;

    while (currentStart <= today) {
      const weekStart = new Date(currentStart);
      const weekEnd = new Date(weekStart);
      weekEnd.setDate(weekStart.getDate() + 6);

      // Limiter la fin de la semaine à aujourd’hui
      const limitedEnd = weekEnd > today ? today : weekEnd;

      const format = (d: Date) => d.toLocaleDateString('fr-FR');
      result.push(`Semaine ${weekIndex}`)
      // result.push(`Semaine ${weekIndex} (${format(weekStart)} - ${format(limitedEnd)})`);

      // Passer au début de la semaine suivante
      currentStart.setDate(currentStart.getDate() + 7);
      weekIndex++;
    }

    return result;
  }

  getLastThreeWeeks(): string[] {
    const result: string[] = [];
    const today = new Date();

    // Trouver le début de la semaine actuelle (lundi)
    const currentMonday = new Date(today);
    const day = currentMonday.getDay(); // 0 = dimanche, 1 = lundi, ...
    const diffToMonday = day === 0 ? -6 : 1 - day;
    currentMonday.setDate(today.getDate() + diffToMonday);

    for (let i = 2; i >= 0; i--) {
      const start = new Date(currentMonday);
      start.setDate(currentMonday.getDate() - i * 7);

      const end = new Date(start);
      end.setDate(start.getDate() + 6);

      const format = (d: Date) => d.toLocaleDateString('fr-FR');

      result.push(`Semaine ${i} `);
    }

    return result.reverse();
  }


}
