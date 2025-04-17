import axios from 'axios';
import dotenv from 'dotenv';
import { Report, ShortMatch, Referee } from '../types/firebase';
import { initializeApp } from 'firebase/app';
import { getFirestore, doc, getDocs, collection, CollectionReference, getDoc, Firestore } from 'firebase/firestore';

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

// Fetch all reports in short form (for listings)
export async function getReportsShortFromFirebase(): Promise<Report[]> {
  try {
    const reportsSnapshot = await getDocs(reportsRef);
    let reports = reportsSnapshot.docs.map(doc => ({
      ...doc.data(),
      id: doc.id
    })) as Report[];

    // Enrich reports with minimal data from FastAPI backend
    reports = await populateReportsShort(reports);
    
    return reports;
  } catch (error) {
    console.error('Error fetching short reports from Firebase:', error);
    throw error;
  }
}

// Fetch all reports from Firebase
export async function getReportsFromFirebase(): Promise<Report[]> {
  try {
    const reportsSnapshot = await getDocs(reportsRef);
    let reports = reportsSnapshot.docs.map(doc => ({
      ...doc.data(),
      id: doc.id
    })) as Report[];

    // Enrich reports with data from FastAPI backend
    reports = await populateReports(reports);
    
    return reports;
  } catch (error) {
    console.error('Error fetching reports from Firebase:', error);
    throw error;
  }
}

// Fetch a specific report by ID from Firebase
export async function getReportById(reportId: string): Promise<Report | null> {
  try {
    const reportRef = doc(db, REPORTS_COLLECTION, reportId);
    const reportDoc = await getDoc(reportRef);
    
    if (!reportDoc.exists()) {
      return null;
    }
    
    const report = {
      id: reportDoc.id,
      ...reportDoc.data()
    } as Report;
    
    // Enrich the report with minimal data from FastAPI backend
    const enrichedReport = await populateSingleReportShort(report);
    
    return enrichedReport;
  } catch (error) {
    console.error(`Error fetching report ${reportId} from Firebase:`, error);
    throw error;
  }
}

// Fetch a specific report by ID with full details
export async function getReportWithDetailsById(reportId: string): Promise<Report | null> {
  try {
    const reportRef = doc(db, REPORTS_COLLECTION, reportId);
    const reportDoc = await getDoc(reportRef);
    
    if (!reportDoc.exists()) {
      return null;
    }
    
    const report = {
      id: reportDoc.id,
      ...reportDoc.data()
    } as Report;
    
    // Enrich the report with detailed data from FastAPI backend
    const enrichedReport = await populateSingleReport(report);
    
    return enrichedReport;
  } catch (error) {
    console.error(`Error fetching detailed report ${reportId} from Firebase:`, error);
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
    
    // Get player details for incidents
    if (report.incidents && report.incidents.length > 0) {
  
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

// Helper function to enrich multiple reports in batches
async function populateReports(reports: Report[]): Promise<Report[]> {
  // Process in smaller batches to avoid overwhelming the API
  const batchSize = 5;
  const enrichedReports = [];
  
  // Process reports in batches
  for (let i = 0; i < reports.length; i += batchSize) {
    // Slice the reports array into smaller batches
    const batch = reports.slice(i, i + batchSize);
    // awaits for all promises in the batch to resolve
    const enrichedBatch = await Promise.all(
      batch.map(report => populateSingleReport(report))
    );
    enrichedReports.push(...enrichedBatch);
  }
  
  return enrichedReports;
}

export default db;