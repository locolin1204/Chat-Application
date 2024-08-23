import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { User } from "@app/model/User";
import { Observable, tap } from "rxjs";
import { environment } from "../../environments/environment.development";

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  constructor(
    private http: HttpClient,
  ) { }

  private backendUrl = environment.backendUrl;
  private curUserId = environment.curUserId

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  }

  getCurrentUser(userId?: string): Observable<User> {
    if(userId) this.curUserId = userId
    console.log("cur userId: ", this.curUserId)

    return this.http.get<User>(`${this.backendUrl}/user/${this.curUserId}`)
      .pipe(
        tap(user => console.log(`fetched current user: ${user._id}`)),
      )
  }

  getAllUsers(): Observable<User[]>{
    return this.http.get<User[]>(`${this.backendUrl}/users`)
      .pipe(
        tap(_=> console.log(`fetched users`)),
      )
  }

  getUserById(id: string): Observable<User>{
    return this.http.get<User>(`${this.backendUrl}/user/${id}`)
      .pipe(
        tap(_=> console.log(`fetched user ${id}`)),
      )
  }

}
