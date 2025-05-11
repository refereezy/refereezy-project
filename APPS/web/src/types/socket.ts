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

export interface SocketEvents {
  // Client to server events
  register: (clockCode: string) => void;
  'validate-code': (code: string, pairingData: RefereePairing) => void;
  'new-report': (id: string, code?: string | null) => void;
  'new-incident': (reportId: string, incident: Incident) => void;
  'unregister': (clockCode: string) => void;
  'pair-confirmed': (clockCode: string) => void;
  'report-received': (clockCode: string, reportId: string) => void;
  'work-completed': (clockCode: string, reportId: string) => void;
  'check-clock-status': (clockCode: string) => void;
  
  // Server to client events
  pair: (pairingData: RefereePairing) => void;
  'register-success': (clockCode: string) => void;
  'register-error': (message: string) => void;
  'unregister-success': () => void;
  'unregister-error': (message: string) => void;
  'clock-not-online': () => void;
  'clock-busy': () => void;
  'clock-notified': (status: ClockStatus) => void;
  'clock-confirmed-pairing': (data: {clockCode: string}) => void;
  'clock-confirmed-report': (data: {clockCode: string, reportId: string, status: string}) => void;
  'clock-work-completed': (data: {clockCode: string, reportId: string, status: string}) => void;
  'clock-disconnected': (data: {clockCode: string, reportId?: string}) => void;
  'clock-unregistered': (data: {clockCode: string}) => void;
  'clock-status': (data: {clockCode: string, online: boolean, status?: string, reportId?: string, message: string}) => void;
  'report-incident-added': (data: {reportId: string, incident: Incident}) => void;
  'report-updated': (report: ReportUpdate) => void;
  'incident-added': (data: {reportId: string, incident: Incident}) => void;
  'report-error': (data: {message: string}) => void;
}