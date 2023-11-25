import { Component } from '@angular/core';
import { APP_METHODS } from '../utils/common_methods';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { ViewEncapsulation } from '@angular/core';
import { Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { FormBuilder, FormControl } from '@angular/forms';
import { APP_MACROS } from '../utils/frontend_macros';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class AccountComponent {
  name: string;
  userRole: string;
  email: string;
  appMacros = APP_MACROS;

  checkoutForm: any;

  ngOnInit() {
    APP_METHODS.checkToken();
  }

  isError = true;
  error = '';
  deleteError = '';
  showError = false;
  showDeleteError = false;
  hideErrorBox() {
    this.showError = false;
  }
  hideDeleteErrorBox() {
    this.showDeleteError = false;
  }
  constructor(
    private http: HttpClient,
    @Inject(DOCUMENT) document: Document,
    private formBuilder: FormBuilder
  ) {
    this.name = localStorage.getItem('user_name') + '';
    this.userRole = localStorage.getItem('user_role') + '';

    this.email = localStorage.getItem('user_mail') + '';
    this.checkoutForm = this.formBuilder.group({
      username: this.name,
      email: this.email,
      password: '',
      confirm_password: '',
    });
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

  updateAccount() {
    this.hideErrorBox();
    this.isError = true;

    if (
      this.checkoutForm.value.confirm_password.length > 0 ||
      this.checkoutForm.value.password.length > 0
    ) {
      if (
        this.checkoutForm.value.confirm_password.length == 0 ||
        this.checkoutForm.value.password.length == 0
      ) {
        this.error = 'Please fill out both password fields';
      } else if (
        this.checkoutForm.value.confirm_password !=
        this.checkoutForm.value.password
      ) {
        this.error =
          "Passwords don't match. Please confirm the entered password";
      } else {
        this.error = '';
      }
      this.showError = this.error != '';
    }

    if (this.showError == false) {
      Object.keys(this.checkoutForm.controls).forEach((key) => {
        if (this.showError === false) {
          if (!['confirm_password', 'password'].includes(key)) {
            this.error = APP_METHODS.validateAccountData(
              key,
              this.checkoutForm.get(key)?.value
            );
            this.showError = this.error != '';
          }
        }
      });
    }

    if (this.showError === false) {
      this.sendUpdateRequest();
    }
  }

  handleError(httpError: any) {
    this.showError = true;
    this.isError = true;
    if (httpError.status == 404 || httpError.status == 401) {
      APP_METHODS.logout();
    } else if (httpError == 403) {
      location.pathname = '/home';
    } else if (httpError.status == 409) {
      this.error =
        'This account name/email is already taken. Please choose another one';
    } else if (httpError.status == 400) {
      this.error = httpError.error;
    } else {
      this.error = 'Something went wrong. Please try again later';
    }
  }
  handleDeleteError(httpError: any) {
    this.showDeleteError = true;
    if (httpError.status == 404 || httpError.status == 401) {
      APP_METHODS.logout();
    } else if (httpError == 403) {
      location.pathname = '/home';
    } else {
      this.deleteError = 'Something went wrong. Please try again later';
    }
  }
  sendUpdateRequest() {
    var patchObject;

    if (this.checkoutForm.value.password.length > 0) {
      patchObject = {
        name: this.checkoutForm.value.username,
        mail: this.checkoutForm.value.email,
        password: APP_METHODS.convertStringToSha256(
          this.checkoutForm.value.password
        ),
      };
    } else {
      patchObject = {
        name: this.checkoutForm.value.username,
        mail: this.checkoutForm.value.email,
      };
    }

    this.http
      .patch<any>(
        APP_MACROS.API_URL + '/users/' + localStorage.getItem('user_id'),
        patchObject,
        {
          observe: 'response',
          headers: { 'x-access-token': localStorage.getItem('api_token') + '' },
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
          localStorage.setItem('user_name', this.checkoutForm.value.username);
          localStorage.setItem('user_mail', this.checkoutForm.value.email);

          this.isError = false;
          this.showError = true;
          this.error = 'Account edited successfully';
        },
        (httpError) => this.handleError(httpError)
      );
  }

  deleteAccount() {
    APP_METHODS.confirm_prompt(
      'Are you sure?',
      'Once deleted, you will not be able to recover your account'
    ).then((willDelete) => {
      if (willDelete) {
        this.deleteRequest();
      }
    });
  }

  deleteRequest() {
    this.http
      .delete<any>(
        APP_MACROS.API_URL + '/users/' + localStorage.getItem('user_id'),
        {
          observe: 'response',
          headers: { 'x-access-token': localStorage.getItem('api_token') + '' },
        }
      )
      .subscribe(
        (data) => {
          APP_METHODS.logout();
        },
        (httpError) => this.handleDeleteError(httpError)
      );
  }
}
