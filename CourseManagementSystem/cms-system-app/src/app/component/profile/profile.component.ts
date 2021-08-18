import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { Role } from 'src/app/enum/role.enum';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit,OnDestroy {

  public user: User; //user were going to use to represent logged in user
  public editUser = new User();
  private currentUsername: string;
  private subscriptions: Subscription[] = []; 
  public fileName: string;
  public refreshing: boolean;
  public users: User[];
  public ProfileImage: File;


  constructor(private authenticationService:AuthenticationService, private userService: UserService,
              private notificationService: NotificationService, private AuthenticationService:AuthenticationService, private router: Router) { }

  ngOnInit(): void {
    this.user = this.authenticationService.getUserFromLocalCache();
  }
 
  public onUpdateCurrentUser(user: User): void {
    this.currentUsername=this.authenticationService.getUserFromLocalCache().username;
    const formData = this.userService.createsUserFormData(this.currentUsername, user, null, ); //skipped picture because it did not work
    this.subscriptions.push(
      this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.authenticationService.addUserToLoaclCache(response);
          this.getUsers(false); //will refresh all the users
          this.fileName=null;
          this.ProfileImage=null;
          this.sendNotification(NotificationType.SUCCESS, `${response.name} updated successfully`);
          
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.getUsers(true);
          this.ProfileImage = null;
          this.refreshing=true;
        }
      )
    );
  }


  public getUsers(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.userService.getUsers().subscribe(
        (response: User[]) => {
          this.userService.addUsersToLocalCache(response);
          this.users = response; //setting responce to array of users
          this.refreshing = false;
          console.log(this.users);
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} users loaded successfully.`)
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing=false;
        }
      )
    )
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

 //once the component is distroyed. it runs this code
 ngOnDestroy():void{ //after we suscribe to a call. we want to unsuscribe to avoid memory leaks
  this.subscriptions.forEach(sub => sub.unsubscribe()); //we unsuscribe from the orginal http call.. we do no listen anymore after the component is distroyed
}

}
