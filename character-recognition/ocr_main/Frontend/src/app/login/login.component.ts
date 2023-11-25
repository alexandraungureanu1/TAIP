import { Component } from '@angular/core';
import { ViewEncapsulation } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { APP_METHODS } from '../utils/common_methods';
import { APP_MACROS } from '../utils/frontend_macros';
import { UserAccount } from '../utils/common_models';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class LoginComponent {
  ngOnInit() {}

  checkoutForm = this.formBuilder.group({
    username: '',
    password: '',
  });

  error = '';
  showError = false;
  appMacros = APP_MACROS;

  constructor(private formBuilder: FormBuilder, private http: HttpClient) {}

  onSubmit() {
    this.hideErrorBox();
    Object.keys(this.checkoutForm.controls).forEach((key) => {
      if (this.showError === false) {
        this.error = APP_METHODS.validateAccountData(
          key,
          this.checkoutForm.get(key)?.value
        );
        this.showError = this.error != '';
      }
    });

    if (this.showError === false) {
      this.sendLoginRequest();
    }
  }

  hideErrorBox() {
    this.showError = false;
  }

  handleError(httpError: any) {
    this.showError = true;
    if (httpError.status == 401) {
      this.error = 'Please enter valid credentials';
    } else {
      this.error = 'Something went wrong. Please try again later';
    }
  }

  sendLoginRequest() {
    this.http
      .post<UserAccount>(
        APP_MACROS.API_URL + '/login',
        {
          name: this.checkoutForm.value.username,
          password: APP_METHODS.convertStringToSha256(
            this.checkoutForm.value.password
          ),
        },
        { observe: 'response' }
      )
      .subscribe(
        (data) => {
          APP_METHODS.storeUser(
            data.headers.get('X-Access-Token') + '',
            data.body?.user
          );
          location.pathname = '/home';
        },
        (httpError) => this.handleError(httpError)
      );
  }
}
