export interface Mesure {
  id?: number;
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

export interface LigneMedicament {
  id?: number;
  nomMedicament: string;
  dosage: string;
  posologie: string;
  dureeJours: number;
  heuresRappel?: string;
  instructions?: string;
}

export interface Ordonnance {
  id?: number;
  dateEmission?: string;
  dateExpiration: string;
  notes?: string;
  medicaments: LigneMedicament[];
}

export interface Consultation {
  id?: number;
  patientId: number;
  medecinId: number;
  medecinNom?: string;
  dateConsultation?: string;
  compteRendu: string;
  objectifsMois: string;
  type: string;
  resumeIA?: string;
  mesures?: Mesure[];
  ordonnance?: Ordonnance;
}