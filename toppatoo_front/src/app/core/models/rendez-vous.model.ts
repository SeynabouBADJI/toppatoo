export interface RendezVous {
  id?: string;
  patientId: string;
  medecinId: string;
  medecinNom?: string;
  dateHeure: string;
  motif: string;
  lieu: string;
  statut?: string;
}