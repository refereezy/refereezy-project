export type RefereePairing = {
  clockCode: string
  refereeId: string
  refereePass: string
};

export interface Incident {
  id: number;
  reportId: string;
  description: string;
  minute: number;
  player_id: number;
  type: string;
}

export interface ReportUpdate {
  id: string;
  done?: boolean;
  timer?: [number, number];
  match_id?: number;
  referee_id?: number;
}

export interface SocketEvents {
  // Client to server events
  register: (clockCode: string) => void;
  'validate-clockCode': (pairingData: RefereePairing) => void;
  'new-report': (id: string) => void;
  'new-incident': (id: string) => void;
  
  // Server to client events
  pair: (pairingData: RefereePairing) => void;
  'clock-not-online': () => void;
  'report-updated': (report: ReportUpdate) => void;
  'incident-added': (incident: Incident) => void;
}