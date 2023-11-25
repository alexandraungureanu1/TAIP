import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountComponent } from './account/account.component';
import { DocumentOCRComponent } from './document-ocr/document-ocr.component';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { TemplateCreateComponent } from './template-create/template-create.component';
import { TemplateEditComponent } from './template-edit/template-edit.component';
import { TemplatesComponent } from './templates/templates.component';
import { UsersComponent } from './users/users.component';

const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: 'home',
    component: HomeComponent,
  },
  {
    path: 'account',
    component: AccountComponent,
  },
  {
    path: 'templates',
    component: TemplatesComponent,
  },
  {
    path: 'templates/new',
    component: TemplateCreateComponent,
  },
  {
    path: 'templates/:template_id',
    component: TemplateEditComponent,
  },
  {
    path: 'ocr',
    component: DocumentOCRComponent,
  },
  {
    path: 'users',
    component: UsersComponent,
  },
  { path: '**', component: HomeComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
