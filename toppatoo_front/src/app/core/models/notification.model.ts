// // export type TypeNotification =
// //   | 'RAPPEL_RDV'
// //   | 'ALERTE_MESURE'
// //   | 'RAPPEL_MEDICAMENT'
// //   | 'RESUME_CONSULTATION'
// //   | 'MESSAGE_MEDECIN'
// //   | 'SYSTEME';

// // export type CanalNotification = 'SMS' | 'EMAIL' | 'PUSH' | 'IN_APP';
// // export type StatutEnvoi = 'EN_ATTENTE' | 'ENVOYE' | 'ECHEC';

// // // Dans notification.model.ts
// // export interface Notification {
// //   id: string;
// //   titre: string;
// //   message: string;
// //   type: 'ALERTE_MESURE' | 'RAPPEL_MEDICAMENT' | 'RAPPEL_RENDEZVOUS' | 'RESULTAT_ANALYSE' | 'INFO';
// //   patientId: string;
// //   lue: boolean;
// //   createdAt: string;
// //   niveauAlerte?: 'CRITIQUE' | 'ATTENTION' | 'INFO';
// //   actions?: NotificationAction[];
// //   metadata?: any; // Pour stocker des données supplémentaires
// // }

// // export interface NotificationAction {
// //   type: string;
// //   label: string;
// //   data?: any;
// // }
// export interface Notification {
//   id?: number;
//   titre: string;
//   message: string;
//   type: string;
//   patientId: number;
//   lue: boolean;
//   createdAt: string;
//   niveauAlerte?: string;
//   actions?: NotificationAction[];
// }

// export interface NotificationAction {
//   type: string;
//   label: string;
//   data?: any;
// }
export interface Notification {
  id: number;  // ✅ id obligatoire (non-optionnel)
  titre: string;
  message: string;
  type: string;
  patientId: number;
  lue: boolean;
  createdAt: string;
  niveauAlerte?: string;
  actions?: NotificationAction[];
}

export interface NotificationAction {
  type: string;
  label: string;
  data?: any;
}