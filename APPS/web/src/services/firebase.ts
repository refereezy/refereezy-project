import axios from 'axios';
import dotenv from 'dotenv';
import { Report, ShortMatch, Referee, Incident, Match } from '../types/firebase';
import { initializeApp } from 'firebase/app';
import { getFirestore, doc, getDocs, collection, CollectionReference, getDoc, Firestore, setDoc } from 'firebase/firestore';

// Load environment variables
dotenv.config({ path: process.cwd() + '/.env' });

// Check if Firebase config variables are loaded
console.log('Firebase Config Check:');
console.log('API Key loaded:', process.env.FIREBASE_KEY ? 'Yes' : 'No');
console.log('Auth Domain loaded:', process.env.FIREBASE_AUTH_DOMAIN ? 'Yes' : 'No');
console.log('Project ID loaded:', process.env.FIREBASE_PROJECT_ID ? 'Yes' : 'No');

// Initialize Firebase
const firebaseConfig = {
  apiKey: process.env.FIREBASE_KEY,
  authDomain: process.env.FIREBASE_AUTH_DOMAIN,
  projectId: process.env.FIREBASE_PROJECT_ID,
  storageBucket: process.env.FIREBASE_STORAGE_BUCKET,
  messagingSenderId: process.env.FIREBASE_MESSAGING_SENDER_ID,
  appId: process.env.FIREBASE_APP_ID,
  measurementId: process.env.FIREBASE_MEASUREMENT_ID
};

// Check if any config values are undefined
const missingKeys = Object.entries(firebaseConfig)
  .filter(([_, value]) => value === undefined)
  .map(([key]) => key);

if (missingKeys.length > 0) {
  console.error('Missing Firebase configuration keys:', missingKeys);
}

let db: Firestore;
let reportsRef: CollectionReference<Report>;
let REPORTS_COLLECTION = 'reports';
let REFEREEZY_API_URL: string; // Default URL for local development

try {
  const app = initializeApp(firebaseConfig);
  db = getFirestore(app);
  console.log('Firebase initialized successfully with project ID:', firebaseConfig.projectId);
  
  // Define the collection reference for reports
  REPORTS_COLLECTION = 'reports';
  reportsRef = collection(db, REPORTS_COLLECTION) as CollectionReference<Report>;

  // FastAPI backend URL
  REFEREEZY_API_URL = process.env.REFEREEZY_API_URL || 'http://localhost:8080';
} catch (error) {
  console.error('Error initializing Firebase:', error);
  throw error;
}

// SIMPLIFIED FUNCTION 1: Fetch all reports with minimal data (for listings)
export async function getAllReportsShort(): Promise<Report[]> {
  try {
    // Get basic report data from Firebase
    const reportsSnapshot = await getDocs(reportsRef);
    let reports = reportsSnapshot.docs.map(doc => ({
      ...doc.data(),
      id: doc.id,
      incidents: [] // Initialize empty incidents array
    })) as Report[];

    // Fetch incidents for each report from subcollections
    for (const report of reports) {
      const incidentsRef = collection(db, REPORTS_COLLECTION, report.id, 'incidents');
      const incidentsSnapshot = await getDocs(incidentsRef);
      
      // Map subcollection documents to incidents array
      report.incidents = incidentsSnapshot.docs.map(doc => ({
        id: parseInt(doc.id),
        ...doc.data()
      })) as Incident[];
    }

    // Enrich reports with minimal data from FastAPI backend
    reports = await populateReportsShort(reports);
    
    return reports;
  } catch (error) {
    console.error('Error fetching short reports from Firebase:', error);
    throw error;
  }
}

// SIMPLIFIED FUNCTION 2: Fetch a single report with full details (for detail view)
export async function getReportDetailById(reportId: string): Promise<Report | null> {
  try {
    // Get basic report data from Firebase
    const reportRef = doc(db, REPORTS_COLLECTION, reportId);
    const reportDoc = await getDoc(reportRef);
    
    if (!reportDoc.exists()) {
      return null;
    }
    
    const report = {
      id: reportDoc.id,
      ...reportDoc.data()
    } as Report;
    
    // Fetch incidents from subcollection
    const incidentsRef = collection(db, REPORTS_COLLECTION, reportId, 'incidents');
    const incidentsSnapshot = await getDocs(incidentsRef);
    
    // Map subcollection documents to incidents array
    report.incidents = incidentsSnapshot.docs.map(doc => ({
      id: parseInt(doc.id),
      ...doc.data()
    })) as Incident[];
    
    // Enrich the report with full detailed data from FastAPI backend
    const enrichedReport = await populateSingleReport(report);
    
    // The player information is now included directly in the incidents
    // No need to populate the incidents with player information
    
    return enrichedReport;
  } catch (error) {
    console.error(`Error fetching detailed report ${reportId} from Firebase:`, error);
    throw error;
  }
}

// Add a new incident to a report's subcollection
export async function addIncidentToReport(reportId: string, incident: Incident): Promise<Incident> {
  try {
    // Reference to the incidents subcollection for this report
    const incidentsRef = collection(db, REPORTS_COLLECTION, reportId, 'incidents');
    
    // Add the document to the subcollection
    // We'll use the incident.id as the document ID if it's provided
    const docRef = doc(incidentsRef, incident.id?.toString());
    await setDoc(docRef, incident);
    
    // Return the incident as is - player information should already be included
    return incident;
  } catch (error) {
    console.error(`Error adding incident to report ${reportId}:`, error);
    throw error;
  }
}

// Helper function to enrich a single report with minimal details (short form)
async function populateSingleReportShort(report: Report): Promise<Report> {
  try {
    // Get basic match details - uses the new endpoint we'll create
    if (report.match_id) {
      const matchResponse = await axios.get(`${REFEREEZY_API_URL}/matches/short/${report.match_id}`);
      report.match = matchResponse.data as ShortMatch;
    }
    
    // Get basic referee details
    if (report.referee_id) {
      const refereeResponse = await axios.get(`${REFEREEZY_API_URL}/referee/${report.referee_id}`);
      report.referee = refereeResponse.data as Referee;
    }
    
    return report;
  } catch (error) {
    console.error(`Error enriching report short details:`, error);
    // Return the original report even if enrichment fails
    return report;
  }
}

// Helper function to enrich a single report with details from FastAPI
async function populateSingleReport(report: Report): Promise<Report> {
  try {
    // Get match details
    if (report.match_id) {
      const matchResponse = await axios.get(`${REFEREEZY_API_URL}/matches/populated/${report.match_id}`) as any;
      const match = matchResponse.data
      report.match = match;
    }
    
    // Get referee details
    if (report.referee_id) {
      const refereeResponse = await axios.get(`${REFEREEZY_API_URL}/referee/${report.referee_id}`) as any;
      report.referee = refereeResponse.data;
    }
    
    return report;
  } catch (error) {
    console.error(`Error enriching report details:`, error);
    // Return the original report even if enrichment fails
    return report;
  }
}

// Helper function to enrich multiple reports with minimal data (for listing)
async function populateReportsShort(reports: Report[]): Promise<Report[]> {
  // Process in smaller batches to avoid overwhelming the API
  const batchSize = 10; // Can use larger batches for short data
  const enrichedReports = [];
  
  // Process reports in batches
  for (let i = 0; i < reports.length; i += batchSize) {
    const batch = reports.slice(i, i + batchSize);
    const enrichedBatch = await Promise.all(
      batch.map(report => populateSingleReportShort(report))
    );
    enrichedReports.push(...enrichedBatch);
  }
  
  return enrichedReports;
}

export default db;