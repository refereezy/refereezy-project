// Types for the reports and related entities

export interface ShortTeam {
  id: number;
  name: string;
  logo_url: string;
}

export interface Team extends ShortTeam {
  primary_color: string;
  secondary_color: string;
  players?: Player[];
}

export interface Player {
  id: number;
  name: string;
  dorsal: number;
  goalkeeper: boolean;
}

export interface PlayerIncident extends Player {
  team_local: boolean;
}

export interface Referee {
  id: number;
  name: string;
  client_id: number;
  clock_code?: string;
}

interface MatchRaw {
  id: number;
  date: string;
  client_id: number;
  referee_id: number;
  local_team_id: number;
  visitor_team_id: number;
}


export interface ShortMatch {
  raw: MatchRaw;
  local_team: ShortTeam;
  visitor_team: ShortTeam;
}

export interface Match extends ShortMatch {
  local_team: Team;
  visitor_team: Team;
}

export interface Incident {
  id: number;
  description: string;
  minute: number;
  type: string;
  player?: PlayerIncident;
}

export interface Report {
  id: string;
  done: boolean;
  match_id: number;
  referee_id: number;
  timer: [number, number];
  incidents: Incident[];
  match?: ShortMatch | Match;
  referee?: Referee;
}

export interface FirebaseDoc<T> {
  id: string;
  data(): T;
  exists: boolean;
}

export interface FirebaseSnapshot<T> {
  docs: FirebaseDoc<T>[];
  empty: boolean;
  size: number;
}