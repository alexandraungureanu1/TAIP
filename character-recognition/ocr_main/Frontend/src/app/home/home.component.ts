import { Component } from '@angular/core';
import { APP_METHODS } from '../utils/common_methods';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { ViewEncapsulation } from '@angular/core';
import { Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class HomeComponent {
  userRole: string;
  ngOnInit() {
    APP_METHODS.checkToken();
  }

  constructor(private http: HttpClient, @Inject(DOCUMENT) document: Document) {
    this.userRole = localStorage.getItem('user_role') + '';
  }

  logout() {
    APP_METHODS.logout();
  }

  dropdownButton(event: any) {
    var target = event.target || event.srcElement || event.currentTarget;
    var element = document.getElementById(target.attributes.id.nodeValue);
    if (element == null) {
      return;
    }

    element.classList.toggle('active');
    var dropdownContent = document.getElementById(
      element.nextElementSibling?.id + ''
    );
    if (dropdownContent == null) {
      return;
    }

    if (dropdownContent.style.display === 'block') {
      dropdownContent.style.display = 'none';
    } else {
      dropdownContent.style.display = 'block';
    }
  }
}
