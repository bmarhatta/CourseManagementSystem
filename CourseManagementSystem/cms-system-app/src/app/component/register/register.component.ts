import { HttpErrorResponse, } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {

  public showLoading: boolean; //

  private subscriptions: Subscription[] = []; //list of subscription so we can add all the subscription we are doing

  constructor(private router:Router,private authenticationService: AuthenticationService, 
              private notifierService: NotificationService) { }

  ngOnInit(): void {
    if(this.authenticationService.isUserLoggedIn()){ //mechanism to reroute user to main page if there already logged in
      this.router.navigateByUrl('/user/management') //TODO:make this the log in page
    } 
  }

  public onRegister(user: User){
    this.subscriptions.push(
      this.authenticationService.register(user).subscribe( //call in register and pass in the user
        (response: User) => { 
          this.sendNotification(NotificationType.SUCCESS, `A new account was for ${response.name}.please check your email for password to log in. `); //send the notifcaiton to the user
          this.router.navigateByUrl('/login');
        },
        (errorResponse: HttpErrorResponse) => { //incase of an error
          // console.log(errorResponse);
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message); //send the notifcaiton to the user
          this.showLoading=false;
          
        }
      )
    );
  }

  private sendNotification(NotificationType: NotificationType, message: string): void {
    if(message) { //java script way to check if something is true
      this.notifierService.notify(NotificationType, message); 
    } else {
      this.notifierService.notify(NotificationType, 'An error occured. Please try again.  ');
    }
  }



  //once the component is distroyed. it runs this code
  ngOnDestroy():void{ //after we suscribe to a call. we want to unsuscribe to avoid memory leaks
    this.subscriptions.forEach(sub => sub.unsubscribe()); //we unsuscribe from the orginal http call.. we do no listen anymore after the component is distroyed
  }
}
