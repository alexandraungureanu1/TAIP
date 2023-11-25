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
import { TemplatePreview, TemplatesPreview } from '../utils/common_models';

@Component({
  selector: 'app-templates',
  templateUrl: './templates.component.html',
  styleUrls: ['./templates.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class TemplatesComponent {
  page = 0;
  userRole: string;
  templates: TemplatePreview[] = [];
  blockNext: boolean = false;

  appMacros = APP_MACROS;

  checkoutForm = this.formBuilder.group({
    filter: '',
    goToPage: 1,
  });

  ngOnInit() {
    APP_METHODS.checkToken();
  }

  ngAfterViewInit() {}
  ngAfterViewChecked() {}

  constructor(
    private http: HttpClient,
    @Inject(DOCUMENT) document: Document,
    private formBuilder: FormBuilder
  ) {
    this.updateTemplates();
    this.userRole = localStorage.getItem('user_role') + '';
  }

  openTemplate(templateId: string) {
    location.pathname = '/templates/' + templateId;
  }

  logout() {
    APP_METHODS.logout();
  }

  filterSearch() {
    this.page = 0;
    this.updateTemplates();
  }

  changePage(increment: any) {
    if (increment != null) {
      this.page += increment ? 1 : -1;
      this.updateTemplates();
    } else if (this.checkoutForm.value.goToPage != null) {
      this.page = this.checkoutForm.value.goToPage - 1;
      this.updateTemplates();
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

  updateTemplates() {
    let url =
      APP_MACROS.API_URL +
      '/templates?page=' +
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
      .get<TemplatesPreview>(url, {
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
          this.templates = [];
          if (data.body != null) {
            if (data.body.templates.length == 0) {
              this.blockNext = true;
            } else {
              this.templates = data.body.templates;
              if (this.templates.length < APP_MACROS.TABLE_PAGE_SIZE) {
                this.blockNext = true;
                var limit = APP_MACROS.TABLE_PAGE_SIZE - this.templates.length;
                for (var index = 0; index < limit; index++) {
                  this.templates.push({
                    id: '',
                    name: '',
                    preview: '',
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
