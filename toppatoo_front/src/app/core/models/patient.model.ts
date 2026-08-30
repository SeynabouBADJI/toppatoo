export interface Patient {
  id: string;
  user: {
    id: string;
    nom: string;
    prenom: string;
    email: string;
    telephone: string;
    role: string;
    actif: boolean;
  };
  medecinId?: string;
  medecinNom?: string;
  dateNaissance: string;
  sexe: string;
  groupeSanguin?: string;
  adresse?: string;
  ville?: string;
  antecedents?: string;
  allergies?: string;
  maladieChronique: string;
  seuilGlycemieMin?: number;
  seuilGlycemieMax?: number;
  seuilTensionSystMax?: number;
  seuilTensionDiastMax?: number;
  seuilPoidsMin?: number;
  seuilPoidsMax?: number;
}

export type MaladieChronique =
  | 'DIABETE_TYPE1'
  | 'DIABETE_TYPE2'
  | 'HYPERTENSION'
  | 'INSUFFISANCE_RENALE'
  | 'INSUFFISANCE_CARDIAQUE'
  | 'ASTHME'
  | 'BPCO'
  | 'AUTRE';

export const MALADIE_LABELS: Record<string, string> = {
  DIABETE_TYPE1:          'Diabète type 1',
  DIABETE_TYPE2:          'Diabète type 2',
  HYPERTENSION:           'Hypertension',
  INSUFFISANCE_RENALE:    'Insuffisance rénale',
  INSUFFISANCE_CARDIAQUE: 'Insuffisance cardiaque',
  ASTHME:                 'Asthme',
  BPCO:                   'BPCO',
  AUTRE:                  'Autre',
};