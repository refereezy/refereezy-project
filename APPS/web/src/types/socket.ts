import { PlayerIncident } from "./firebase";

export type RefereePairing = {
  id: number
  token: string
};

export interface Incident {
  id: number;
  reportId: string;
  description: string;
  minute: number;
  type: string;
  player?: PlayerIncident;
}

export interface ReportUpdate {
  id: string;
  done?: boolean;
  timer?: [number, number];
  match_id?: number;
  referee_id?: number;
}

export interface ClockStatus {
  clockCode: string;
  status: 'available' | 'paired' | 'working';
  reportId?: string;
}