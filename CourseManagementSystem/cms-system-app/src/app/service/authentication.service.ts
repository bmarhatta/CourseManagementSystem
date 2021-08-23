import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { User } from '../model/user';
import { JwtHelperService } from "@auth0/angular-jwt"; //this command adds this libary to our angular project: npm i @auth0/angular-jwt
//we use this libary to help use communicate with the back end



@Injectable({providedIn: 'root'})
export class AuthenticationService {

  public host = environment.apiUrl;
  private token: string;
  private loggedInUsername: string;
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) { }

  //function to login user
  public login(user: User): Observable<HttpResponse<User>> { //this can return a response or an error
    return this.http.post<User>(`${this.host}/user/login`, user, {observe: `response`}); //{observe: `response`} means give me the entire responce. including the body, header, ect
    //line 17 says, we call that request and pass in the user object. and then we get the responce
  }

  //function to register user
  public register(user: User): Observable<User> { //this can return a user or an error
    return this.http.post<User>
    (`${this.host}/user/register`, user); //here want back the user. we do not need the header
  }

    //function to log user out
  public logOut(): void { 
    this.token = null;
    this.loggedInUsername = null;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('users');
  }

  //method to save token in local storage (aka cache)
  public saveToken(token: string): void { 
    this.token = token;
    localStorage.setItem('token', token); //in the local storage. set this token and and reference it by this id
  }

  //function to add user to the local storage (adding user to cache)
  public addUserToLoaclCache(user: User): void { 
    localStorage.setItem('user', JSON.stringify(user)); //JSON.stringify turns the object into a string . allowing use to save it the local storage
  }

  //function to get the user from the cache
  public getUserFromLocalCache(): User { 
    return JSON.parse(localStorage.getItem('user'));  //json.parse will take a string and transform it back into a acutall object
  }

  //load token from the local storage.
  public loadToken(): void { 
    this.token = localStorage.getItem(`token`); 
  }

  //get token from local storage.
  public getToken(): string { 
    return this.token;
  }

  //function to see if the user is logged in or not.. 
  public isUserLoggedIn(): boolean { 
    this.loadToken();
    if(this.token != null && this.token !== '') { //if token is not null and not empty
      // console.log("1work");
      if(this.jwtHelper.decodeToken(this.token).sub != null || ''){  //decode the token and get the subject. if subject is not empty or null. we can move forward
        // console.log("2work");
        if(!this.jwtHelper.isTokenExpired(this.token)){ //if token is NOT expired
          // console.log("3work");
          this.loggedInUsername = this.jwtHelper.decodeToken(this.token).sub; //where setting the username in this class to the subject (we made subject the username in the backend)
            return true;
        }
      }
    } else { //if it is empty were just going to log the user out
        this.logOut();
        // console.log(1);
        return false;
      }
    return false;
      }

}


//observable: Do this for me and let me know when i get the responce back.
//you would want to change the name of the item in your local storage once it goes into production. Something random/difficult to read to hacker
//cannto know your token and other local variables. 
