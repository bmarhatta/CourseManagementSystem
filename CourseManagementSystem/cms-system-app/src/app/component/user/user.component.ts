import { Component, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { User } from 'src/app/model/user';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { HttpErrorResponse } from '@angular/common/http';
import { NgForm } from '@angular/forms';
import { CustomHttpResponse } from 'src/app/model/custom-http-response';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { Role } from 'src/app/enum/role.enum';
import { SubSink } from 'subsink';
 

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit, OnDestroy {

  //this method was imported from npm install subsink --save
  //we will use it to unsubsuscribe our observerables. This is a must have for application with multiple pages.
  private subs = new SubSink();


  public users: User[];
  private subscriptions: Subscription[] = []; 
  public selectedUser: User;
  public fileName: string;
  public ProfileImage: File;
  public editUser = new User();
  private currentUsername: string;

  constructor(private userService: UserService, private notificationService: NotificationService, private authenticationService: AuthenticationService) { }

  ngOnInit(): void {
   this.getUsers(true); //we call this function to load the user
  }

  public getUsers(showNotification: boolean): void {
      this.userService.getUsers().subscribe(
        (response: User[]) => {
          this.userService.addUsersToLocalCache(response);
          this.users = response; //setting responce to array of users
          console.log(this.users);
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} users loaded successfully.`)
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      );
    } 

//sends all information to back end
public onAddNewUser(userForm: NgForm): void {
  const formData = this.userService.createsUserFormData(null, userForm.value, null, ); //skipped picture because it did not work
  this.subs.add(
    this.userService.addUsers(formData).subscribe(
      (response: User) => {
        this.clickButton('new-user-close');
        this.getUsers(true);
        this.fileName=null;
        this.ProfileImage=null;
        userForm.reset();
        this.sendNotification(NotificationType.SUCCESS, `${response.name} Added successfully`);
      },
      (errorResponse: HttpErrorResponse) => {
        this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        this.ProfileImage=null;
      }
    )
  );
}

public onUpdateUser():void {
  const formData = this.userService.createsUserFormData(this.currentUsername, this.editUser, null, ); //skipped picture because it did not work
  this.subscriptions.push(
    this.userService.updateUser(formData).subscribe(
      (response: User) => {
        this.clickButton('closeEditUserButton');
        this.getUsers(false); //will refresh all the users
        this.fileName=null;
        this.ProfileImage=null;
        this.sendNotification(NotificationType.SUCCESS, `${response.name} updated successfully`);
      },
      (errorResponse: HttpErrorResponse) => {
        this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        this.getUsers(true);
      }
    )
  );
}

public onDeleteUser(username: string):void {
  this.subscriptions.push(
    this.userService.deleteUser(username).subscribe(
      (response: CustomHttpResponse) => {
        this.sendNotification(NotificationType.SUCCESS, response.message);  
        this.getUsers(false);
      },
      (errorResponse: HttpErrorResponse) => {
        this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
      }
    )
  );
}


public onResetPassword(emailForm: NgForm): void {
  const emailAddress = emailForm.value['reset-password-email'];
  this.subscriptions.push(
    this.userService.resetPassword(emailAddress).subscribe(
      (response: CustomHttpResponse) => {
        this.sendNotification(NotificationType.SUCCESS, response.message);  
      },
      (errorResponse: HttpErrorResponse) => {
        this.sendNotification(NotificationType.WARNING, errorResponse.error.message);
      },
      () => emailForm.reset() //when you subscribe... there are 3 possible respones. a good responce, a error response, and a third response tht will run nomatter what
    )
  );
}

public searchUsers(searchTerm: string):void{
  const results: User[] = [];
  for (const user of this.userService.getUsersFromLocalCache()) { //getting list of users
    if (user.name.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1) { //for this search term, im checking to see the loop of local users... if the name of user is in the array
      results.push(user); //push user to the arraylist
    }
  }
  this.users=results;
  if (results.length == 0 || !searchTerm) {
    this.userService.getUsersFromLocalCache();
  }
}

private sendNotification(NotificationType: NotificationType, message: string): void {
  if(message) { //java script way to check if something is true
    this.notificationService.notify(NotificationType, message); 
  } else {
    this.notificationService.notify(NotificationType, 'An error occured. Please try again.  ');
  }
} 


private clickButton(buttonId: string): void { 
  document.getElementById(buttonId).click();
}

public saveNewUser(): void {
  this.clickButton('new-user-save'); //this will get use the html element and click it
}

public onEditUser(editUser: User):void{
  this.editUser = editUser;
  //current username to call the back end
  this.currentUsername = editUser.username;
  this.clickButton('openUserEdit');
}

//opening model
public onSelectUser(selectedUser: User):void {
  this.selectedUser = selectedUser;
  this.clickButton('openUserInfo'); //this will get use the html element and click it
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

 //once the component is distroyed. it runs this code
 ngOnDestroy():void{ //after we suscribe to a call. we want to unsuscribe to avoid memory leaks
  this.subscriptions.forEach(sub => sub.unsubscribe()); //we unsuscribe from the orginal http call.. we do no listen anymore after the component is distroyed
  this.subs.unsubscribe();
}

}

//Basic understanding of RxJs.
// BehaviorSubject is a type of subject, a subject is a special type of observable so you can subscribe to messages like any other observable. The unique features of BehaviorSubject are:
// It needs an initial value as it must always return a value on subscription even if it hasn't received a next()
// Upon subscription, it returns the last value of the subject. A regular observable only triggers when it receives an onnext
// at any point, you can retrieve the last value of the subject in a non-observable code using the getValue() method. 