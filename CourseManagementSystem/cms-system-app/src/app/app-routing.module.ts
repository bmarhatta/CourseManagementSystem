import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './component/login/login.component';
import { loggedInNav } from './component/loggedInNav/loggedInNav';
import { RegisterComponent } from './component/register/register.component';
import { Resetpassword1Component } from './component/resetpassword1/resetpassword1.component';
import { AuthenticationGuard } from './guard/authentication.guard';

//this routes your component to uri
const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent},
  { path: 'resetPassword', component: Resetpassword1Component},
  { path: 'user/management', component: loggedInNav, canActivate: [AuthenticationGuard]}, //returns true or false if the user can access this route
  { path: '', redirectTo: '/login', pathMatch:'full'} //if the routes are non of the above. it will redirectTo to this uri... order is important. make sure this is the last route 
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }


//pathMatch: 'full' means, that the whole URL path needs to match and is consumed by the route matching algorithm.