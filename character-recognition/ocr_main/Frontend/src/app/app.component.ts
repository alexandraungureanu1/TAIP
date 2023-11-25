import { Component } from '@angular/core';
import { APP_METHODS } from './utils/common_methods';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  ngOnInit() {
    APP_METHODS.checkToken();
  }

  title = 'doc-ocr-frontend';
}
