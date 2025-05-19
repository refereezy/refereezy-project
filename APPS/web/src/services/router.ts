import { Router } from 'express';
import { getAllReportsShort, getReportDetailById } from './firebase';
import { clockSockets } from './socket'; 

const router = Router();

router.get('/api/status', (req, res) => {
  res.json({ status: 'API is running' });
});

// Endpoints para estadísticas
router.get('/api/stats/devices', (req, res) => {
  const count = Object.keys(clockSockets).length;
  res.json({ count });
});

router.get('/api/stats/matches', (req, res) => {
  const count = Object.values(clockSockets).filter(clock => clock.status === 'working').length;
  res.json({ count });
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

router.get('/api/report/status/:id', async (req, res): Promise<any> => {

  const result = Object.values(clockSockets).find(clock => clock.reportId === req.params.id);
  console.log('Clock status:', result);
  res.json(result ? 'Report in use' : 'Report available');

});

export default router;