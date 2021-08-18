import { Component, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { User } from 'src/app/model/user';
import { NotificationService } from 'src/app/service/notification.service';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { HttpErrorResponse } from '@angular/common/http';
import { NgForm } from '@angular/forms';
import { CustomHttpResponse } from 'src/app/model/custom-http-response';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { Course } from 'src/app/model/course';
import { CourseService } from 'src/app/service/course.service';
import { Role } from 'src/app/enum/role.enum';
 

@Component({
  selector: 'app-course',
  templateUrl: './course.component.html',
  styleUrls: ['./course.component.css']
})
export class CourseComponent implements OnInit, OnDestroy {

  private titleSubject = new BehaviorSubject<string>('Profile');  //by default the behavior subject will be user. 
  //this is an action lisner
  public titleAction$ = this.titleSubject.asObservable(); //this action lisener change the title subject

  public courses: Course[];
  public refreshing: boolean;
  private subscriptions: Subscription[] = []; 
  public selectedCourse: Course;
  public fileName: string;
  public ProfileImage: File;
  public editCourse = new Course();

  constructor(private courseServices: CourseService, private notificationService: NotificationService, private authenticationService: AuthenticationService) { }

  ngOnInit(): void {
    this.getCourses(true); //we call this function to load the user
  }

  public getCourses(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.courseServices.getCourses().subscribe(
        (response: Course[]) => {
          this.courseServices.addCoursesToLocalCache(response);
          this.courses = response; //setting responce to array of users
          console.log(this.courses);
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} courses loaded successfully.`)
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing=false;
        }
      )
    )
  }
  
  public onDeleteCourses(courseCode: string):void {
    this.subscriptions.push(
      this.courseServices.deleteUser(courseCode).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);  
          this.getCourses(false);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }

  public onUpdateCourse():void {
    const formData = this.courseServices.createsCourseFormData(this.editCourse); //skipped picture because it did not work
    this.subscriptions.push(
      this.courseServices.updateUser(formData).subscribe(
        (response: Course) => {
          this.clickButton('closeeditUserButton');
          this.getCourses(false); //will refresh all the users
          this.sendNotification(NotificationType.SUCCESS, `${response.courseName} updated successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.getCourses(true);
        }
      )
    );
  }

//sends all information to back end
public onAddNewCourse(courseForm: NgForm): void {
  const formData = this.courseServices.createsCourseFormData(courseForm.value); //skipped picture because it did not work
  console.log(formData)
  this.subscriptions.push(
    this.courseServices.addUsers(formData).subscribe(
      (response: Course) => {
        this.clickButton('new-course-close');
        this.getCourses(true);
        courseForm.reset();
        this.sendNotification(NotificationType.SUCCESS, `${response.courseName} Added successfully`);
      },
      (errorResponse: HttpErrorResponse) => {
        this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
      }
    )
  );
}

public searchCourse(searchTerm: string):void{
  const results: Course[] = [];
  for (const course of this.courseServices.getCoursesFromLocalCache()) { //getting list of users
    if (course.courseName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1) { //for this search term, im checking to see the loop of local users... if the name of user is in the array
      results.push(course); //push user to the arraylist
    }
  }
  this.courses=results;
  if (results.length == 0 || !searchTerm) {
    this.courseServices.getCoursesFromLocalCache();
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


private clickButton(buttonId: string): void { 
   document.getElementById(buttonId).click();  
} 
  
public saveNewCourse(): void {
  this.clickButton('new-course-save'); //this will get use the html element and click it
}

public onEditCourse(editCourse: Course):void{
  this.editCourse = editCourse;
  this.clickButton('openCourserEdit');
}

//opening view course model
public onSelectUser(selectedCourse: Course):void {
  this.selectedCourse = selectedCourse;
  this.clickButton('openCourseInfo'); //this will get use the html element and click it
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