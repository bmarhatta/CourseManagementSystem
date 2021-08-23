import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Course } from '../model/course';
import { CustomHttpResponse } from '../model/custom-http-response';

@Injectable({providedIn: 'root'})
export class CourseService {

  private host = environment.apiUrl;

  constructor(private http:HttpClient) { }

  //fetch all the courses
  public getCourses(): Observable<Course[]>{
    return this.http.get<Course[]>(`${this.host}/course/all`);
  }

  //add courses
  public addUsers(formData: FormData): Observable<Course | HttpErrorResponse>{ //FormData is the key and value in a html data. 
    return this.http.post<Course>(`${this.host}/course/add`, formData);
  }

  //update courses
  public updateUser(formData: FormData): Observable<Course | HttpErrorResponse>{ 
    return this.http.put<Course>(`${this.host}/course/update`, formData);
  }
  
  //delete courses
  public deleteUser(courseID: string): Observable<CustomHttpResponse | HttpErrorResponse>{
    return this.http.delete<CustomHttpResponse>(`${this.host}/course/delete/${courseID}`); 
  }


  //add courses to the local storage
  public addCoursesToLocalCache(courses: Course[]): void{
    localStorage.setItem('courses', JSON.stringify(courses));
  }  

  //get courses from local storage
  public getCoursesFromLocalCache(): Course[]{
    if(localStorage.getItem('courses')){ //if i have something in local storage
      return JSON.parse(localStorage.getItem('courses')); //return list of Course
    }
    return null; 
  }  

  //creates courses form data
  public createsCourseFormData(course: Course): FormData{
    const formData = new FormData(); //creating formData object
    // console.log(course.courseCode,course.courseName,course.courseName,course.numberOfParticipants,course.courseDuration);
    formData.append('courseCode', course.courseCode);//append is how you write
    formData.append('courseName', course.courseName);
    formData.append('numberOfParticipants', JSON.stringify(course.numberOfParticipants)); //turning int to json string
    formData.append('courseDescription', course.courseDescription);
    formData.append('courseType', course.courseType); 
    formData.append('courseDuration', course.courseDuration); //turning int to json string
    formData.append('courseFees', JSON.stringify(course.courseFees)); 
    return formData;
  } 
}
 