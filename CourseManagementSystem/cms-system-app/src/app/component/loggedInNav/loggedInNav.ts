import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { Role } from 'src/app/enum/role.enum';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';


@Component({
  selector: 'loggedInNav',
  templateUrl: './loggedInNav.html',
  styleUrls: ['./loggedInNav.css']
})
export class loggedInNav implements OnInit {

  public user: User;

  constructor(private authenticationService:AuthenticationService,private router: Router,private notificationService: NotificationService, ) { }

  ngOnInit(): void {
    document.getElementById('cource').click();
    // console.log(2);
    this.user = this.authenticationService.getUserFromLocalCache();
  }  

 
  private titleSubject = new BehaviorSubject<string>('Profile');  //by default the behavior subject will be user. 
  //this is an action lisner
  public titleAction$ = this.titleSubject.asObservable(); //this action lisener change the title subject

  public changeTitle(title: string): void {
    this.titleSubject.next(title);
  }

  showDiv = {
    userDiv : false,
    courceDiv : false,
    settingDiv : false,
    profileDiv: false
  }

  public onLogOut(): void {
    this.authenticationService.logOut();
    this.router.navigateByUrl('/login');
    this.sendNotification(NotificationType.SUCCESS,'You been succesfully logged out');
  }


  private sendNotification(NotificationType: NotificationType, message: string): void {
    if(message) { //java script way to check if something is true
      this.notificationService.notify(NotificationType, message); 
    } else {
      this.notificationService.notify(NotificationType, 'An error occured. Please try again.  ');
    }
 } 

 //get the role of the user, and compare and see who has what permisstion
private getUserRole(): string {
  return this.authenticationService.getUserFromLocalCache().roles;
}

//getter like in java
public get isAdmin(): boolean {
  return this.getUserRole() === Role.ADMIN || this.getUserRole() == Role.SUPER_ADMIN;
}

public get isManager(): boolean {
  return this.isAdmin || this.getUserRole() == Role.MANAGER;
}

public get isAdminOrManager(): boolean {
  return this.isAdmin || this.isManager;
}

}
 