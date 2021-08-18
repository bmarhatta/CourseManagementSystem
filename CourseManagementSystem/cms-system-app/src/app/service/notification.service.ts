import { Injectable } from '@angular/core';
import { NotifierService } from 'angular-notifier';
import { NotificationType } from '../enum/notification-type.enum';

@Injectable({ providedIn: 'root' })
export class NotificationService {

  constructor(  private notifier: NotifierService) { } //NotifierService is from the package we installed

  //method were going to use to send notification
  public notify(type: NotificationType, message:string){
    this.notifier.notify(type,message);
  }

}
