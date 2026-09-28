import { Component } from '@angular/core';
import { LottieComponent, AnimationOptions, provideLottieOptions } from 'ngx-lottie';
import player from 'lottie-web';


export function playerFactory() {
  return player;
}
@Component({
  selector: 'app-loader',
  standalone: true,
  imports: [LottieComponent],
  providers: [
    provideLottieOptions({
          player: () => import('lottie-web'),
 
    })],
  templateUrl: './loader.component.html',
  styleUrl: './loader.component.css'
})
export class LoaderComponent {
  options: AnimationOptions = {
    path:  'assets/images/loaders/loader.json',
   autoplay: true, 
  loop: true
  };


}
