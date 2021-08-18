import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { NotificationType } from '../enum/notification-type.enum';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuard implements CanActivate {

  constructor(private authenticationService: AuthenticationService, private router:Router,
              private notificationService: NotificationService){}

  //this method return true or false to determine if a user can access a path or not
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean { 
    return this.isUserLoggedIn();; 
  }

  //check if user is logged in
  private isUserLoggedIn(): boolean{
    if(this.authenticationService.isUserLoggedIn()){
      return true;
    }
    this.router.navigate(['/login']); // how to navigate a user to a new page
    this.notificationService.notify(NotificationType.ERROR, `You need to log in to acecess this page`); //our notfy method
    return false;
  }
  
}

//The point of having a gaurd is to gaurd certain routes in the application. if im not authenticatated and i try to acess protected resources or url
//the application should reroute me to a different page. 
//ng g guard guard/authentication