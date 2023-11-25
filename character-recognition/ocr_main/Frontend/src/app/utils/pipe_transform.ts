import { Pipe, PipeTransform } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Observable } from 'rxjs';

@Pipe({
  name: 'secure',
})
export class SecurePipe implements PipeTransform {
  constructor(private http: HttpClient, private sanitizer: DomSanitizer) {}
  async transform(src: string): Promise<string> {
    if (src.indexOf(';base64,') > -1) {
      return src;
    }
    const imageBlob = await this.http
      .get(src, { responseType: 'blob' })
      .toPromise();
    const reader = new FileReader();

    if (imageBlob != null) {
      return new Promise((resolve, reject) => {
        reader.onloadend = () => resolve(reader.result as string);
        reader.readAsDataURL(imageBlob);
      });
    }
    return new Promise((resolve, reject) => '');
  }
}
