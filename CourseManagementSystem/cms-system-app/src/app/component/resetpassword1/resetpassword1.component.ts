import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { CustomHttpResponse } from 'src/app/model/custom-http-response';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-resetpassword1',
  templateUrl: './resetpassword1.component.html',
  styleUrls: ['./resetpassword1.component.css']
})
export class Resetpassword1Component implements OnInit {

  constructor(private userService: UserService, private notificationService: NotificationService, private router: Router) { }

  ngOnInit(): void {
  }
  private subscriptions: Subscription[] = []; 


  public onResetPassword(emailForm: NgForm): void {
    const emailAddress = emailForm.value['reset-password-email'];
    this.subscriptions.push(
      this.userService.resetPassword(emailAddress).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);  
          this.router.navigateByUrl('/login') //TODO:make this the log in page
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.WARNING, errorResponse.error.message);
        },
        () => emailForm.reset() //when you subscribe... there are 3 possible respones. a good responce, a error response, and a third response tht will run nomatter what
      )
    );
  }

  private sendNotification(NotificationType: NotificationType, message: string): void {
    if(message) { //java script way to check if something is true
      this.notificationService.notify(NotificationType, message); 
    } else {
      this.notificationService.notify(NotificationType, 'An error occured. Please try again.  ');
    }
 }  


 //once the component is distroyed. it runs this code
 ngOnDestroy():void{ //after we suscribe to a call. we want to unsuscribe to avoid memory leaks
  this.subscriptions.forEach(sub => sub.unsubscribe()); //we unsuscribe from the orginal http call.. we do no listen anymore after the component is distroyed
}

}
 