import { Router } from 'express';
import { getAllReportsShort, getReportDetailById } from './firebase';

const router = Router();

router.get('/api/status', (req, res) => {
  res.json({ status: 'API is running' });
});

// Get all reports in short form (for listing)
router.get('/api/reports/short', async (req, res): Promise<any> => {
  try {
    const reports = await getAllReportsShort();
    res.json(reports);
  } catch (error) {
    console.error('Error fetching short reports:', error);
    res.status(500).json({ error: 'Failed to fetch reports' });
  }
});

// Get a specific report by ID with full details
router.get('/api/reports/:id', async (req, res): Promise<any> => {
  try {
    const reportId = req.params.id;
    const report = await getReportDetailById(reportId);
    
    if (!report) {
      return res.status(404).json({ error: 'Report not found' });
    }
    
    res.json(report);
  } catch (error) {
    console.error(`Error fetching detailed report ${req.params.id}:`, error);
    res.status(500).json({ error: 'Failed to fetch report' });
  }
});

export default router;