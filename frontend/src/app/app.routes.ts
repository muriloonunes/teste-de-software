import { Routes } from '@angular/router';

import { authGuard, guestGuard } from './core/guards/auth.guard';
import { InternalScreen } from './screens/internal-screen/internal-screen';
import { LoginScreen } from './screens/login-screen/login-screen';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'login', component: LoginScreen, canActivate: [guestGuard] },
  { path: 'interno', component: InternalScreen, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' },
];
