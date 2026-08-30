export interface Mesure {
  id?: string;
  type: string;
  valeur: number;
  valeur2?: number;
  unite: string;
  niveauAlerte?: string;
  dateMesure?: string;
  notes?: string;
}

export interface LigneMedicament {
  id?: string;
  nomMedicament: string;
  dosage: string;
  posologie: string;
  dureeJours: number;
  heuresRappel?: string;
  instructions?: string;
}

export interface Ordonnance {
  id?: string;
  dateEmission?: string;
  dateExpiration: string;
  notes?: string;
  medicaments: LigneMedicament[];
}

export interface Consultation {
  id?: string;
  patientId: string;
  medecinId: string;
  medecinNom?: string;
  dateConsultation?: string;
  compteRendu: string;
  objectifsMois: string;
  type: string;
  resumeIA?: string;
  mesures?: Mesure[];
  ordonnance?: Ordonnance;
}