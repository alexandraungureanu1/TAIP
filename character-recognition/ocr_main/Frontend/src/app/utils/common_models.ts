import { APP_MACROS } from './frontend_macros';
export interface UserAccount {
  user: {
    id: string;
    name: string;
    mail: string;
    role: string;
  };
}

export interface TemplatesPreview {
  templates: TemplatePreview[];
}
export interface TemplatePreview {
  id: string;
  name: string;
  preview: string;
}

export interface TemplatePageField {
  id: string;
  page_id: string;
  name: string;
  sensitive: boolean;
  origin_set: string;
  category: string;
  p1: number;
  p2: number;
  p3: number;
  p4: number;
}

export interface TemplatePage {
  id: string;
  name: string;
  image: string;
  fields: TemplatePageField[];
}

export interface Template {
  id: string;
  name: string;
  pages: TemplatePage[];
}

export interface UsersPreview {
  users: UserPreview[];
}
export interface UserPreview {
  id: string;
  name: string;
  mail: string;
  role: string;
}
