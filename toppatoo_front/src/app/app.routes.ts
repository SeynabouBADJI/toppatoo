import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  {
    path: 'login',
    loadComponent: () =>
      import('./component/auth/login/login.component')
        .then((m) => m.LoginComponent),
  },

  // ── Espace médecin avec layout sidebar ──
  {
    path: 'medecin',
    loadComponent: () =>
      import('./component/medecin/layout/medecin-layout/medecin-layout.component')
        .then((m) => m.MedecinLayoutComponent),
    children: [
      {
        path: 'patients',
        loadComponent: () =>
          import('./component/medecin/patient-liste/patient-liste.component')
            .then((m) => m.PatientListeComponent),
      },
      {
        path: 'patients/nouveau',
        loadComponent: () =>
          import('./component/medecin/nouveau-patient/nouveau-patient.component')
            .then((m) => m.NouveauPatientComponent),
      },
      {
        path: 'patients/:id/detail',
        loadComponent: () =>
          import('./component/medecin/patient-detail/patient-detail.component')
            .then((m) => m.PatientDetailComponent),
      },
      {
        path: 'patients/:id/consultation',
        loadComponent: () =>
          import('./component/medecin/consultation/consultation.component')
            .then((m) => m.ConsultationComponent),
      },
      {
        path: 'rendez-vous',
        loadComponent: () =>
          import('./component/medecin/rendez-vous/rendez-vous.component')
            .then((m) => m.RendezVousComponent),
      },
      {
        path: 'alertes',
        loadComponent: () =>
          import('./component/medecin/alertes/alertes.component')
            .then((m) => m.AlertesComponent),
      },
      { path: '', redirectTo: 'patients', pathMatch: 'full' },
    ],
  },

  // ── Espace patient ──
  {
    path: 'patient/dashboard',
    loadComponent: () =>
      import('./component/patient/dashboard/patient-dashboard.component')
        .then((m) => m.PatientDashboardComponent),
  },

  { path: '**', redirectTo: 'login' },
];