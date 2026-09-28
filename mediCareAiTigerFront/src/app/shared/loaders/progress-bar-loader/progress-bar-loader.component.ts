import { Component } from '@angular/core';
import { LottieComponent, AnimationOptions, provideLottieOptions } from 'ngx-lottie';
import player from 'lottie-web';


export function playerFactory() {
  return player;
}
@Component({
  selector: 'app-progress-bar-loader',
  standalone: true,
  imports: [LottieComponent],
  templateUrl: './progress-bar-loader.component.html',
  providers: [
    provideLottieOptions({
          player: () => import('lottie-web'),
 
    })],
  styleUrl: './progress-bar-loader.component.css'
})
export class ProgressBarLoaderComponent {
  options: AnimationOptions = {
    path:  'assets/images/loaders/progressBarLoader.json',
   autoplay: true, 
  loop: true
  };
}
