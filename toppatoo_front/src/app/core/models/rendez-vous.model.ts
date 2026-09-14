export interface RendezVous {
  id: number;  // ✅ id obligatoire
  patientId: number;
  medecinId: number;
  medecinNom?: string;
  dateHeure: string;
  motif: string;
  lieu: string;
  statut?: string;
}