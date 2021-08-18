import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { HeaderType } from 'src/app/enum/header-type.enum';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';
import { SubSink } from 'subsink';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  //this method was imported from npm install subsink --save
  //we will use it to unsubsuscribe our observerables. It is really good practice.
  private subs = new SubSink();
  private subscriptions: Subscription[] = []; //list of subscription so we can add all the subscription we are doing 

  constructor(private router:Router,private authenticationService: AuthenticationService, 
              private notifierService: NotificationService) { }

  ngOnInit(): void {
    if(this.authenticationService.isUserLoggedIn()){ //mechanism to reroute user to main page if there already logged in
      this.router.navigateByUrl('/user/management') //TODO:make this the log in page
    } else {
      this.router.navigateByUrl('/login')
    }
  }

  public onLogin(user: User){
    this.subscriptions.push(
      this.authenticationService.login(user).subscribe(
        (response: HttpResponse<User>) => { //if we get the correct responce from the observerable method. we want this to be executed
          const token = response.headers.get(HeaderType.JWT_TOKEN); //grab the token from the header
          this.authenticationService.saveToken(token); //saves token in local storage
          this.authenticationService.addUserToLoaclCache(response.body); //add user to local storage
          this.router.navigateByUrl('/user/management');
        },
        (errorResponse: HttpErrorResponse) => { //incase of an error
          console.log(errorResponse);
          this.sendErrorNotification(NotificationType.ERROR, errorResponse.error.message); //send the notifcaiton to the user
          
        }
      )
    );
  }

  private sendErrorNotification(NotificationType: NotificationType, message: string): void {
    if(message) { //java script way to check if something is true
      this.notifierService.notify(NotificationType, message); //sends the error message
    } else {
      this.notifierService.notify(NotificationType, 'An error occured. Please try again.  ');
    }
  }

  //once the component is distroyed. it runs this code
  ngOnDestroy():void{ //after we suscribe to a call. we want to unsuscribe to avoid memory leaks
    this.subscriptions.forEach(sub => sub.unsubscribe()); //we unsuscribe from the orginal http call.. we do no listen anymore after the component is distroyed
  }
 
}
