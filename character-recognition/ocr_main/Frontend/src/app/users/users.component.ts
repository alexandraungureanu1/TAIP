import { Component, ViewChild } from '@angular/core';
import { APP_METHODS } from '../utils/common_methods';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { ViewEncapsulation } from '@angular/core';
import { Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { FormBuilder, FormControl } from '@angular/forms';
import { APP_MACROS } from '../utils/frontend_macros';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import {
  TemplatePreview,
  TemplatesPreview,
  UserPreview,
  UsersPreview,
} from '../utils/common_models';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class UsersComponent {
  page = 0;
  userRole: string;
  name: string = '';
  users: UserPreview[] = [];
  blockNext: boolean = false;

  appMacros = APP_MACROS;

  checkoutForm = this.formBuilder.group({
    filter: '',
    goToPage: 1,
  });

  ngOnInit() {
    APP_METHODS.checkToken();
    this.name = localStorage.getItem('user_name') + '';
  }

  ngAfterViewInit() {}
  ngAfterViewChecked() {}

  constructor(
    private http: HttpClient,
    @Inject(DOCUMENT) document: Document,
    private formBuilder: FormBuilder
  ) {
    this.updateUsers();
    this.userRole = localStorage.getItem('user_role') + '';
  }

  logout() {
    APP_METHODS.logout();
  }

  filterSearch() {
    this.page = 0;
    this.updateUsers();
  }

  promoteUser(userId: string, userRole: string) {
    this.http
      .patch<any>(
        APP_MACROS.API_URL + '/users/' + userId,
        { role: userRole },
        {
          observe: 'response',
          headers: {
            'x-access-token': localStorage.getItem('api_token') + '',
          },
        }
      )
      .subscribe(
        (data) => {
          if (data.headers.get('X-Access-Token') != null) {
            localStorage.setItem(
              'X-Access-Token',
              data.headers.get('X-Access-Token') + ''
            );
          }
          for (var i = 0; i < this.users.length; i++) {
            if (this.users[i].id == userId) {
              this.users[i].role = userRole;
              break;
            }
          }
          APP_METHODS.success_prompt(
            'Success',
            "The user's role has been updated successfully"
          );
        },
        (error) => this.handleError(error)
      );
  }

  changePage(increment: any) {
    if (increment != null) {
      this.page += increment ? 1 : -1;
      this.updateUsers();
    } else if (this.checkoutForm.value.goToPage != null) {
      this.page = this.checkoutForm.value.goToPage - 1;
      this.updateUsers();
    }
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

  handleError(httpError: any) {
    let error = '';
    if (httpError.status == 401) {
      APP_METHODS.logout();
    } else if (httpError.status == 403) {
      location.pathname = '/home';
    } else if (httpError.status == 400) {
      error = httpError.error;
    } else {
      error = 'Something went wrong. Please try again later';
    }

    APP_METHODS.error_prompt('Error', error);
  }

  updateUsers() {
    let url =
      APP_MACROS.API_URL +
      '/users?page=' +
      this.page +
      '&size=' +
      APP_MACROS.TABLE_PAGE_SIZE;

    if (
      this.checkoutForm.value.filter != undefined &&
      this.checkoutForm.value.filter != '' &&
      this.checkoutForm.value.filter?.length > 0
    ) {
      url += '&filter=' + this.checkoutForm.value.filter;
    }
    this.http
      .get<UsersPreview>(url, {
        observe: 'response',
        headers: {
          'x-access-token': localStorage.getItem('api_token') + '',
        },
      })
      .subscribe(
        (data) => {
          if (data.headers.get('X-Access-Token') != null) {
            localStorage.setItem(
              'X-Access-Token',
              data.headers.get('X-Access-Token') + ''
            );
          }
          this.users = [];
          if (data.body != null) {
            if (data.body.users.length == 0) {
              this.blockNext = true;
            } else {
              this.users = data.body.users;
              if (this.users.length < APP_MACROS.TABLE_PAGE_SIZE) {
                this.blockNext = true;
                var limit = APP_MACROS.TABLE_PAGE_SIZE - this.users.length;
                for (var index = 0; index < limit; index++) {
                  this.users.push({
                    id: '',
                    name: '',
                    mail: '',
                    role: '',
                  });
                }
              } else {
                this.blockNext = false;
              }
            }
          }
        },
        (error) => this.handleError(error)
      );
  }
}
