import { NgModule } from '@angular/core';
import { NotifierModule, NotifierOptions } from 'angular-notifier';

//these are the default configuration. i just tweaked with numbers here and there suxh as vertical and horziontal distance
const customNotifierOptions: NotifierOptions = {
    position: {
      horizontal: {
        position: 'left',
        distance: 150,
      },
      vertical: {
        position: 'top',
        distance: 12,
        gap: 10,
      },
    },
    theme: 'material',
    behaviour: {
      autoHide: 5000,
      onClick: 'hide',
      onMouseover: 'pauseAutoHide',
      showDismissButton: true,
      stacking: 4,
    },
    animations: {
      enabled: true,
      show: {
        preset: 'slide',
        speed: 300,
        easing: 'ease',
      },
      hide: {
        preset: 'fade',
        speed: 300,
        easing: 'ease',
        offset: 50,
      },
      shift: {
        speed: 300,
        easing: 'ease',
      },
      overlap: 150,
    },
  };


@NgModule({
    imports: [NotifierModule.withConfig(customNotifierOptions)],
    exports: [NotifierModule]
  })
  export class NotificationModule { }






//https://www.npmjs.com/package/angular-notifier
//adding npm angular-notifer. this will greatly enhance the user exsperence. making this application really nice
//npm install angular-notifier


//we are creating our own module instead of saying all of this in app module
