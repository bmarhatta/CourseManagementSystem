import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../service/authentication.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService) {}

  intercept(httpRequest: HttpRequest<any>, httpHandler: HttpHandler): Observable<HttpEvent<any>> {
    if(httpRequest.url.includes(`${this.authenticationService.host}/user/login`)) { //if the request has this pattern. 
      return httpHandler.handle(httpRequest); //then do not do anything. just handle the request, we don't want to add the tokens to these routes
    }
    if(httpRequest.url.includes(`${this.authenticationService.host}/user/register`)) { 
      return httpHandler.handle(httpRequest); 
    }
    if(httpRequest.url.includes(`${this.authenticationService.host}/user/resetPassword`)) { 
      return httpHandler.handle(httpRequest); 
    }
    this.authenticationService.loadToken(); //load token from local storage and load it into the token variable we created in authentication service
    const token = this.authenticationService.getToken(); //this is just going to return the token in the authentication class.
    //const token1 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJVc2VyIE1hbmFnZW1lbnQiLCJzdWIiOiJiaXNod28iLCJBVVRIT1JJVElFUyI6WyJ1c2VyOnJlYWQiLCJ1c2VyOmNyZWF0ZSIsInVzZXI6dXBkYXRlIiwidXNlcjpkZWxldGUiXSwiaXNzIjoiVGhpcyB3YXMgcHJvdmlkZWQgYnkgQ29nZW50J3MgQ291cnNlIE1hbmFnZW1lbnQgU3lzdGVtISIsImV4cCI6MTYyODkxNDgxMSwiaWF0IjoxNjI4ODcxNjExfQ.wPCvrhUCZITNqd6f85RyVFNPsyVMxd_vdc1tnawsqw0Lj8SpHqq8ZmDv2aeE-kz2sH7XdztXa5b4VouASZB-PQ";
   
    const request = httpRequest.clone({setHeaders:{Authorization: `Bearer ${token}`}}); //clones the orignall request and adds a authorization token in the header
  
    return httpHandler.handle(request);  
  }
}

//The main point of HttpInterceptors is to intercept the request before it gets sent out
//somewhere in the middle. you catch the request and make changes. then send it through


//this interceptor is already intercepting all the request for you. all you have to do now is write the logic

//ng g interceptor auth
