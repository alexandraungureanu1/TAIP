import { Component, INJECTOR, ViewChild } from '@angular/core';
import { APP_METHODS } from '../utils/common_methods';
import { HttpClient } from '@angular/common/http';
import { ViewEncapsulation } from '@angular/core';
import { Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { FormBuilder, FormControl } from '@angular/forms';
import { APP_MACROS } from '../utils/frontend_macros';
import {
  Template,
  TemplatePage,
  TemplatePageField,
  TemplatePreview,
} from '../utils/common_models';

@Component({
  selector: 'app-template-create',
  templateUrl: './template-create.component.html',
  styleUrls: ['./template-create.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class TemplateCreateComponent {
  userRole: string;
  templateName: string = '';
  appMacros = APP_MACROS;
  zoomStyle: any = undefined;
  originalUploadWidth: any = undefined;
  originalUploadHeight: any = undefined;
  WIDTH_HTML_ZOOM = 580;
  SECTION_COLOR_NOT_SELECTED = '#fbff00';
  SECTION_COLOR_SELECTED = 'red';
  CATEGORIES_INFO: {
    [key: string]: string[];
  } = {};
  CATEGORIES_DEFAULT = '';

  listFields: TemplatePageField[] = [];
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

    this.http
      .get<any>(APP_MACROS.API_URL + '/templates/info', {
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
          this.CATEGORIES_INFO = data.body;
          this.CATEGORIES_DEFAULT = data.body.default;
          delete this.CATEGORIES_INFO['default'];
          this.addNewField();
        },
        (error) => this.handleError(error)
      );
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
          this.clearCanvasSelections();
          APP_METHODS.resizeImage(
            this.files[0] + '',
            this,
            function (
              template: TemplateCreateComponent,
              img: any,
              base64: string
            ) {
              template.previewFile = base64;
              template.originalUploadWidth = img.width;
              template.originalUploadHeight = img.height;
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

  deleteField(index: number) {
    this.listFields.splice(index, 1);
    this.selectedField = null;
    this.refreshCanvasSections();
  }

  testMe() {
    var e = document.getElementById('zoom-image-box');
    alert(e?.offsetLeft);
  }

  selectChange(element: any, event: any) {
    element.category = this.CATEGORIES_INFO[event.target.value][0];
  }

  selectedField: any = undefined;
  markSelected(item: TemplatePageField) {
    this.selectedField = item;
    this.refreshCanvasSections();
  }

  addNewField() {
    this.selectedField = null;
    this.listFields.push({
      id: '',
      name: '',
      page_id: '',
      sensitive: false,
      origin_set: this.CATEGORIES_DEFAULT,
      category: this.CATEGORIES_INFO[this.CATEGORIES_DEFAULT][0],
      p1: 0,
      p2: 0,
      p3: 0,
      p4: 0,
    });
    this.refreshCanvasSections();
  }

  checkTemplate() {
    if (this.templateName.length == 0) {
      return 'You need to fill in the template name';
    }
    if (
      this.files[0] == undefined ||
      this.files[0] == null ||
      this.files[0].length == 0
    ) {
      return 'You need to upload an image';
    }

    for (let i = 0; i < this.listFields.length; i++) {
      if (this.listFields[i].name.length == 0) {
        return 'You need to fill in the names of all fields';
      }

      if (this.listFields[i].category.length == 0) {
        return 'You need to choose a category for each field';
      }

      if (
        this.listFields[i].p1 == 0 &&
        this.listFields[i].p2 == 0 &&
        this.listFields[i].p3 == 0 &&
        this.listFields[i].p4 == 0
      ) {
        return 'You need to draw a selection for each field';
      }
    }
    return null;
  }

  uploadTemplate() {
    let error = this.checkTemplate();
    if (error != null) {
      APP_METHODS.error_prompt('Error', error);
      return;
    }

    this.isLoading = true;
    let postTemplate: Template = {
      id: '',
      name: this.templateName,
      pages: [
        {
          id: '',
          name: this.templateName,
          image: this.files[0] + '',
          fields: this.listFields,
        },
      ],
    };

    this.http
      .post<{ template: TemplatePreview }>(
        APP_MACROS.API_URL + '/templates',
        postTemplate,
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
          this.isLoading = false;
          APP_METHODS.success_prompt(
            'Success',
            'The template has been created'
          ).then((_) => {
            location.pathname = '/templates/' + data.body?.template.id;
          });
        },
        (httpError) => this.handleError(httpError)
      );
  }

  getHtmlPreviewSize() {
    let e = document.getElementById('zoom-image-box');
    return {
      width: e?.offsetWidth == undefined ? 0 : e.offsetWidth,
      height: e?.offsetHeight == undefined ? 0 : e.offsetHeight,
    };
  }

  getCanvasSize() {
    var canvas: any = document.getElementById('zoom-image-canvas');
    return {
      width: canvas.width,
      height: canvas.height,
    };
  }

  getOriginalUploadSize() {
    return {
      width: this.originalUploadWidth,
      height: this.originalUploadHeight,
    };
  }

  mouseLastX: number = -1;
  mouseLastY: number = -1;
  mouseDown: boolean = false;

  getImageCoordinates(event: any) {
    if (!event) {
      return;
    }

    let element = document.getElementById('zoom-image-box');
    if (!element) {
      return;
    }

    const rectangle = element.getBoundingClientRect();
    var PosX = Math.floor(event.pageX - rectangle.left);
    var PosY = Math.floor(event.pageY - rectangle.top);

    if (PosX >= 0 && PosX < this.WIDTH_HTML_ZOOM) {
      return [PosX, PosY];
    }
    return;
  }

  isNotSelectedField() {
    return this.selectedField == undefined || this.selectedField == null;
  }
  sectionMouseDown(event: any) {
    this.mouseDown = true;
    if (this.isNotSelectedField()) {
      return;
    }

    let coords = this.getImageCoordinates(event);
    if (coords == undefined || coords == null) {
      return;
    }
    this.mouseLastX = coords[0];
    this.mouseLastY = coords[1];

    this.selectedField.p1 = this.mouseLastX;
    this.selectedField.p2 = this.mouseLastY;
  }

  updateSelectedFieldCoords(event: any) {
    if (this.isNotSelectedField()) {
      return;
    }

    let coords = this.getImageCoordinates(event);
    if (coords == undefined || coords == null) {
      return;
    }

    let coordsP1 = this.getCoordsEquivalent(
      this.mouseLastX,
      this.mouseLastY,
      this.getHtmlPreviewSize(),
      this.getOriginalUploadSize()
    );
    if (coordsP1 == undefined || coordsP1 == null) {
      return;
    }
    let coordsP2 = this.getCoordsEquivalent(
      coords[0],
      coords[1],
      this.getHtmlPreviewSize(),
      this.getOriginalUploadSize()
    );
    if (coordsP2 == undefined || coordsP2 == null) {
      return;
    }

    this.selectedField.p1 = coordsP1[0];
    this.selectedField.p2 = coordsP1[1];
    this.selectedField.p3 = coordsP2[0];
    this.selectedField.p4 = coordsP2[1];
  }

  sectionMouseUp(event: any) {
    this.mouseDown = false;
    if (this.isNotSelectedField()) {
      return;
    }
    this.updateSelectedFieldCoords(event);
    this.refreshCanvasSections();
    this.mouseLastX = -1;
    this.mouseLastY = -1;
  }

  sectionMouseMove(event: any) {
    if (!this.mouseDown) {
      return;
    }
    if (this.isNotSelectedField()) {
      return;
    }

    this.updateSelectedFieldCoords(event);
    this.refreshCanvasSections();
  }

  renderOnCanvasField(ctx: any, renderField: TemplatePageField) {
    if (
      renderField.p1 == 0 &&
      renderField.p2 == 0 &&
      renderField.p3 == 0 &&
      renderField.p4 == 0
    ) {
      return;
    }

    ctx.strokeStyle =
      this.selectedField == renderField
        ? this.SECTION_COLOR_SELECTED
        : this.SECTION_COLOR_NOT_SELECTED;
    ctx.lineWidth = 2;

    let canvasCoordsP1 = this.getCoordsEquivalent(
      renderField.p1,
      renderField.p2,
      this.getOriginalUploadSize(),
      this.getCanvasSize()
    );
    let canvasCoordsP2 = this.getCoordsEquivalent(
      renderField.p3,
      renderField.p4,
      this.getOriginalUploadSize(),
      this.getCanvasSize()
    );

    ctx.beginPath();
    ctx.rect(
      canvasCoordsP1[0],
      canvasCoordsP1[1],
      canvasCoordsP2[0] - canvasCoordsP1[0],
      canvasCoordsP2[1] - canvasCoordsP1[1]
    );
    ctx.stroke();
  }

  clearCanvasSelections() {
    this.listFields.forEach(function (listField) {
      listField.p1 = 0;
      listField.p2 = 0;
      listField.p3 = 0;
      listField.p4 = 0;
    });
    this.refreshCanvasSections();
  }

  refreshCanvasSections() {
    let thisEntity = this;
    var canvas: any = document.getElementById('zoom-image-canvas');
    if (canvas == null) {
      return;
    }
    var ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    this.listFields.forEach(function (listField) {
      thisEntity.renderOnCanvasField(ctx, listField);
    });

    if (this.isNotSelectedField()) {
      return;
    }
    this.renderOnCanvasField(ctx, this.selectedField);
  }

  getCoordsEquivalent(
    x: number,
    y: number,
    resolutionFrom: {
      [key: string]: number;
    },
    resolutionTo: {
      [key: string]: number;
    }
  ) {
    return [
      Math.floor((resolutionTo['width'] * x) / resolutionFrom['width']),
      Math.floor((resolutionTo['height'] * y) / resolutionFrom['height']),
    ];
  }
}
