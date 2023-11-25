import { Component, INJECTOR, ViewChild } from '@angular/core';
import { APP_METHODS } from '../utils/common_methods';
import { HttpClient } from '@angular/common/http';
import { ViewEncapsulation } from '@angular/core';
import { Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { FormBuilder, FormControl } from '@angular/forms';
import { APP_MACROS } from '../utils/frontend_macros';
import { prettyPrintJson, FormatOptions } from 'pretty-print-json';

@Component({
  selector: 'app-document-ocr',
  templateUrl: './document-ocr.component.html',
  styleUrls: ['./document-ocr.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class DocumentOCRComponent {
  userRole: string;
  templateName: string = '';
  appMacros = APP_MACROS;
  zoomStyle: any = undefined;
  WIDTH_HTML_ZOOM = 580;
  isLoading = false;

  ngOnInit() {
    APP_METHODS.checkToken();
  }

  ngAfterViewInit() {
    this.zoomStyle = document.createElement('STYLE');
    this.zoomStyle.innerText = `.ngxImageZoomLensEnabled {
      display: none !important;
    }`;
    document.body.appendChild(this.zoomStyle);
  }

  constructor(
    private http: HttpClient,
    @Inject(DOCUMENT) document: Document,
    private formBuilder: FormBuilder
  ) {
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

  handleError(httpError: any) {
    let error = '';
    this.isLoading = false;
    if (httpError.status == 401) {
      APP_METHODS.logout();
    } else if (httpError.status == 403) {
      location.pathname = '/home';
    } else if (httpError.status == 400) {
      error = httpError.error;
    } else if (httpError.status == 409) {
      error = 'Template already exists. Please insert a different name';
    } else {
      error = 'Something went wrong. Please try again later';
    }

    APP_METHODS.error_prompt('Error', error);
  }

  // ////////////////////////

  files: String[] = [];
  previewFile: String = '';

  onSelect(event: any) {
    let filesUploaded = [];
    filesUploaded.push(...event.addedFiles);
    if (APP_METHODS.check_input_file(filesUploaded[0]) == false) {
      filesUploaded = [];
      APP_METHODS.error_prompt(
        'Error',
        "Invalid image: size can't be greater than " +
          APP_MACROS.MAX_SIZE_MB_INPUT_IMG +
          ' MB'
      );
      return;
    }

    if (filesUploaded.length > 0) {
      const reader = new FileReader();
      reader.readAsDataURL(filesUploaded[0]);
      reader.onload = () => {
        if (reader.result) {
          this.files = [reader.result.toString()];
          APP_METHODS.resizeImage(
            this.files[0] + '',
            this,
            function (
              template: DocumentOCRComponent,
              img: any,
              base64: string
            ) {
              template.previewFile = base64;
            }
          );
        }
      };
      reader.onerror = (error) => {
        filesUploaded = [];
        APP_METHODS.error_prompt(
          'Error',
          "Something went wrong. Couldn't read file: " + error
        );
      };
    }
  }

  imgZoom: boolean = false;
  enableZoom() {
    this.imgZoom = !this.imgZoom;

    document.body.removeChild(this.zoomStyle);
    this.zoomStyle.innerText =
      `.ngxImageZoomLensEnabled {
        display: ` +
      (this.imgZoom ? 'block' : 'none') +
      ` !important;
      }`;
    document.body.appendChild(this.zoomStyle);
  }

  testMe() {
    var e = document.getElementById('zoom-image-box');
    alert(e?.offsetLeft);
  }

  checkTemplate() {
    if (
      this.files[0] == undefined ||
      this.files[0] == null ||
      this.files[0].length == 0
    ) {
      return 'You need to upload an image';
    }
    return null;
  }

  resultText = '';
  resultTextHTML = '';

  clipboard() {
    var copyText: any = document.getElementById('ocr-textarea');
    navigator.clipboard.writeText(this.resultText);
    APP_METHODS.success_prompt('Success', 'Copied to clipboard').then((_) => {
      // copyText.focus();
      // copyText.select();
      // copyText.setSelectionRange(0, 99999);
    });
  }

  documentOCR() {
    let error = this.checkTemplate();
    if (error != null) {
      APP_METHODS.error_prompt('Error', error);
      return;
    }

    this.resultText = '';
    this.resultTextHTML = '';
    this.documentOCRRequest();
  }

  documentOCRRequest() {
    this.isLoading = true;
    this.http
      .post<any>(
        APP_MACROS.API_URL + '/ocr',
        {
          image: this.files[0],
        },
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
          this.resultText = JSON.stringify(data.body, null, '\t');
          this.resultTextHTML = prettyPrintJson.toHtml(data.body, {
            indent: 4,
            lineNumbers: false,
            linkUrls: false,
            linksNewTab: false,
            quoteKeys: true,
            trailingComma: false,
          });
          var copyText: any = document.getElementById('ocr-textarea');
          copyText.innerHTML = this.resultTextHTML;

          this.isLoading = false;
        },
        (httpError) => this.handleError(httpError)
      );
  }

  downloadJSON() {
    var dataStr =
      'data:text/json;charset=utf-8,' + encodeURIComponent(this.resultText);
    var downloadAnchorNode = document.createElement('a');
    downloadAnchorNode.setAttribute('href', dataStr);
    downloadAnchorNode.setAttribute(
      'download',
      'document_ocr_' + Date.now() + '.json'
    );
    document.body.appendChild(downloadAnchorNode);
    downloadAnchorNode.click();
    downloadAnchorNode.remove();
  }
}
