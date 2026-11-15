import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthStateService } from '../services/auth-state.service';

/** Protege a rota interna: sem sessão ativa, redireciona para o login. */
export const authGuard: CanActivateFn = () => {
  const authState = inject(AuthStateService);
  const router = inject(Router);
  return authState.estaAutenticado ? true : router.parseUrl('/login');
};

/** Protege a rota de login: com sessão ativa, manda direto para o painel. */
export const guestGuard: CanActivateFn = () => {
  const authState = inject(AuthStateService);
  const router = inject(Router);
  return authState.estaAutenticado ? router.parseUrl('/interno') : true;
};
