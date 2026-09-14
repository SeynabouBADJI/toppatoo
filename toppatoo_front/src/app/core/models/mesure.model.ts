export interface Mesure {
  id: number;  // ✅ id obligatoire
  type: string;
  valeur: number;
  valeur2?: number;
  unite: string;
  niveauAlerte?: string;
  dateMesure?: string;
  notes?: string;
  patientId?: number;
  consultationId?: number;
}