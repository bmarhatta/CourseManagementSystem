import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from'@angular/common/http'

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthenticationService } from './service/authentication.service';
import { UserService } from './service/user.service';
import { AuthInterceptor } from './interceptor/auth.interceptor';
import { AuthenticationGuard } from './guard/authentication.guard';
import { NotificationModule } from './notification.module';
import { NotificationService } from './service/notification.service';
import { LoginComponent } from './component/login/login.component';
import { RegisterComponent } from './component/register/register.component';
import { UserComponent } from './component/user/user.component';
import { FormsModule } from '@angular/forms';
import { CourseComponent } from './component/course/course.component';
import { loggedInNav } from './component/loggedInNav/loggedInNav';
import { Resetpassword2Component } from './component/resetpassword2/resetpassword2.component';
import { ProfileComponent } from './component/profile/profile.component';
import { Resetpassword1Component } from './component/resetpassword1/resetpassword1.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    UserComponent,
    CourseComponent,
    loggedInNav,
    Resetpassword2Component,
    ProfileComponent,
    Resetpassword1Component,
    
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NotificationModule,
    FormsModule
  ],
  
  //this are services that you can use globally anywhere in this application
  providers: [NotificationService, AuthenticationGuard, AuthenticationService, UserService, CourseComponent,
    {provide: HTTP_INTERCEPTORS, useClass:AuthInterceptor, multi:true}], //all the multi is doing is creating multiple instances in the injector
  bootstrap: [AppComponent]
})
export class AppModule { }


