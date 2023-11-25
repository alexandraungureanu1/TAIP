import { APP_MACROS } from './frontend_macros';
import * as shajs from 'sha.js';
import swal from 'sweetalert';
import { UserAccount } from '../utils/common_models';
import { HttpClient } from '@angular/common/http';
import { TemplateCreateComponent } from '../template-create/template-create.component';

export class APP_METHODS {
  static checkAdmin() {
    if (localStorage.getItem('user_role') != 'admin') {
      location.pathname = '/home';
    }
  }

  static refreshUser(http: HttpClient) {
    if (
      localStorage.getItem('api_token') === '' ||
      localStorage.getItem('user_id') === ''
    ) {
      return;
    }

    http
      .get<UserAccount>(
        APP_MACROS.API_URL + '/users/' + localStorage.getItem('user_id'),
        {
          observe: 'response',
          headers: { 'x-access-token': localStorage.getItem('api_token') + '' },
        }
      )
      .subscribe((data) => {
        APP_METHODS.storeUser(
          data.headers.get('X-Access-Token'),
          data.body?.user
        );
      });
  }

  static checkToken() {
    let api_token = localStorage.getItem('api_token') + '';
    let user_role = localStorage.getItem('user_role') + '';

    if (
      api_token === '' &&
      !['/login', '/register'].includes(location.pathname)
    ) {
      location.pathname = '/login';
    }

    if (api_token.length > 0) {
      if (user_role != 'admin') {
        if (!['/home', '/account', '/ocr', '/'].includes(location.pathname)) {
          location.pathname = '/home';
        }
      }
      if (['/login', '/register', '/'].includes(location.pathname)) {
        location.pathname = '/home';
      }
    }
  }

  static checkEmail(email: string) {
    if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email)) {
      return '';
    }
    return 'Invalid email. Please enter a valid one';
  }

  static storeUser(api_token: any, user: any) {
    if (api_token.length > 0) {
      localStorage.setItem('api_token', api_token);
    }
    if (user == null) {
      return;
    }

    localStorage.setItem('user_id', user.id + '');
    localStorage.setItem('user_name', user.name + '');
    localStorage.setItem('user_mail', user.mail + '');
    localStorage.setItem('user_role', user.role + '');
  }

  static logout() {
    localStorage.setItem('api_token', '');
    localStorage.setItem('user_id', '');
    localStorage.setItem('user_name', '');
    localStorage.setItem('user_mail', '');
    localStorage.setItem('user_role', '');
    location.pathname = '/login';
  }

  static convertStringToSha256(content: any) {
    return shajs('sha256').update(content).digest('hex');
  }

  static validateAccountData(fieldName: string, fieldValue: string) {
    if (
      fieldValue === undefined ||
      fieldValue === null ||
      fieldValue.length == 0
    ) {
      return 'Please fill out all fields';
    }

    if (fieldName == 'email') {
      return this.checkEmail(fieldValue);
    }

    return '';
  }

  static confirm_prompt(prompt_title: string, prompt_text: string) {
    return swal({
      title: prompt_title,
      text: prompt_text,
      icon: 'warning',
      dangerMode: true,
      buttons: ['CANCEL', 'OK'],
    });
  }

  static error_prompt(prompt_title: string, prompt_text: string) {
    return swal({
      title: prompt_title,
      text: prompt_text,
      icon: 'error',
      dangerMode: true,
      buttons: ['CANCEL', 'OK'],
    });
  }

  static success_prompt(prompt_title: string, prompt_text: string) {
    return swal({
      title: prompt_title,
      text: prompt_text,
      icon: 'success',
      dangerMode: true,
    });
  }

  static check_input_file(file: File) {
    return file.size < 1024 * 1024 * APP_MACROS.MAX_SIZE_MB_INPUT_IMG;
  }

  static resizeImage(base64Str: string, template: any, cb: any) {
    var img = new Image();

    img.onload = function () {
      var canvas = document.createElement('canvas');
      var PREVIEW_WIDTH = APP_MACROS.ZOOM_PREVIEW_WIDTH;
      var PREVIEW_HEIGHT = Math.floor((PREVIEW_WIDTH * img.height) / img.width);

      canvas.width = PREVIEW_WIDTH;
      canvas.height = PREVIEW_HEIGHT;
      var ctx = canvas.getContext('2d');
      if (ctx) {
        ctx.drawImage(img, 0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
      }

      var result = canvas.toDataURL();
      canvas.remove();
      cb(template, img, result);
    };
    img.src = base64Str;
  }
}
