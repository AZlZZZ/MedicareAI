import { Component } from '@angular/core';
import { LottieComponent, provideLottieOptions, AnimationOptions } from 'ngx-lottie';

@Component({
  selector: 'app-btn-loader',
  standalone: true,
   imports: [LottieComponent],
   providers: [
     provideLottieOptions({
           player: () => import('lottie-web'),
  
     })],
  templateUrl: './btn-loader.component.html',
  styleUrl: './btn-loader.component.css'
})
export class BtnLoaderComponent {
  options: AnimationOptions = {
    path:  'assets/images/loaders/Loader-btn.json',
   autoplay: true, 
  loop: true
  };
}
