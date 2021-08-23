import { HttpClient, HttpErrorResponse, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CustomHttpResponse } from '../model/custom-http-response';
import { User } from '../model/user';

@Injectable({ providedIn: 'root' })
export class UserService {
  private host = environment.apiUrl;

  constructor(private http:HttpClient) { }

  //fetch all the users
  public getUsers(): Observable<User[]>{
    return this.http.get<User[]>(`${this.host}/user/list`);
  }

  //add users
  public addUsers(formData: FormData): Observable<User | HttpErrorResponse>{ //FormData is the key and value in a html data. 
    return this.http.post<User>(`${this.host}/user/add`, formData);
  }

  //update users
  public updateUser(formData: FormData): Observable<User | HttpErrorResponse>{ 
    return this.http.post<User>(`${this.host}/user/update`, formData);
  }

  //reset users password
  public resetPassword(email: string): Observable<CustomHttpResponse | HttpErrorResponse>{ 
    // console.log(`${this.host}/user/resetPassword/${email}`)
    return this.http.get<CustomHttpResponse>(`${this.host}/user/resetPassword/${email}`);
  }
  
  //update profile image
  public updateProfileImage(formData: FormData): Observable<HttpEvent<User> | HttpErrorResponse>{ //We return a event because. because there is a whole bunch of lil events coming back before we get the user responce
    return this.http.post<User>(`${this.host}/user/updateProfileImage`, formData
    ,{reportProgress: true, //i want to report progress because it is a upload of a image
        observe: 'events' //We return a event because. because we want to track the upload progress
     }); 
  }

  //delete user
  public deleteUser(username: string): Observable<CustomHttpResponse | HttpErrorResponse>{
    return this.http.delete<CustomHttpResponse>(`${this.host}/user/delete/${username}`); 
  }

  //add users to the local storage
  public addUsersToLocalCache(users: User[]): void{
    localStorage.setItem('users', JSON.stringify(users));
  }  

  //get users from local storage
  public getUsersFromLocalCache(): User[]{
    if(localStorage.getItem('users')){ //if i have something in local storage
      return JSON.parse(localStorage.getItem('users')); //return list of users
    }
    return null;
  }  

  //creates user form data
  public createsUserFormData(loggedInUsername:string, user: User, profileImage:File): FormData{
    const formData = new FormData(); //creating formData object
    formData.append('currentUsername', loggedInUsername);//append is how you write
    formData.append('name', user.name);
    formData.append('username', user.username);
    formData.append('email', user.email);
    formData.append('role', user.roles);
    formData.append('profileImage', profileImage);
    formData.append('isActive', JSON.stringify(user.active)); //backend is especting a string
    formData.append('isNonLocked', JSON.stringify(user.notLocked)); //backend is especting a string
    return formData;
  } 
  
}


