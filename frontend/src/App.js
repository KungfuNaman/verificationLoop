import React, { useState, useEffect, useRef } from 'react';
import { TextField, Button, Box, Typography, Grid, Paper, FormControlLabel, Checkbox, Table, TableBody, TableCell, TableRow, TableHead, TableContainer } from '@mui/material';
import './App.css';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';
import axios from 'axios';
import demoJson from "./demos.json";
import { BarChart } from '@mui/x-charts/BarChart';



function App() {
  const [demos, setDemos] = useState({});

  useEffect(() => {
    setDemos(demoJson);
  }, []);

  const firstUpdate = useRef(true);


  const [classCode, setClassCode] = useState('');
  const [className, setClassName] = useState('');
  const [checkStyleConfig, setCheckStyleConfig] = useState('');
  const [checkstyleResult, setCheckstyleResult] = useState({ status: "pending" });
  const [inferResult, setInferResult] = useState({ status: "pending" });
  const [symbolicExecutionResult, setSymbolicExecutionResult] = useState({ status: "pending" });
  const [useDefaultCheckStyle, setUseDefaultCheckStyle] = useState(false);
  const [results, setResults] = useState([]);
  const [updateTable, setUpdateTable] = useState(false);



  useEffect(() => {
    console.log("called")
    if (firstUpdate.current) {
      firstUpdate.current = false;
      return;
    }
    let checkstyleIssues = checkstyleResult.issues != null ? checkstyleResult.issues : 0;
    let inferIssues = inferResult.issues != null ? inferResult.issues : 0;
    let symbolicIssues = symbolicExecutionResult.issues != null ? symbolicExecutionResult.issues : 0;
    const newResults = [...results, {
      checkstyle: checkstyleIssues,
      infer: inferIssues,
      symbolic: symbolicIssues,
      iteration: results.length + 1,
    }]
    setResults(newResults);
  }, [updateTable])
  const handleCheckStyleConfigChange = (event) => {
    setCheckStyleConfig(event.target.value);
    if (event.target.value !== '') {
      setUseDefaultCheckStyle(false);
    }
  };

  const handleCheckboxChange = (event) => {
    setUseDefaultCheckStyle(event.target.checked);
    if (event.target.checked) {
      setCheckStyleConfig('');
    }
  };

  const handleSubmit = async () => {
    // Start Checkstyle verification
    setCheckstyleResult('Fetching...');
    try {
      const responseCheckstyle = await axios.post('http://localhost:8080/verifyWithCheckstyle', { "code": classCode, "fileName": className, checkStyleConfig });
      setCheckstyleResult(responseCheckstyle.data);
    } catch (error) {
      setCheckstyleResult('Failed to verify');
    }

    // Start Infer verification
    setInferResult('Fetching...');
    try {
      const responseInfer = await axios.post('http://localhost:8080/verifyWithInfer', { "code": classCode, "fileName": className });
      setInferResult(responseInfer.data);
    } catch (error) {
      setInferResult('Failed to verify');
    }

    // Start Symbolic Execution verification
    setSymbolicExecutionResult('Fetching...');
    try {
      const responseSymbolic = await axios.post('http://localhost:8080/verifyWithSymbolicExecution', { "code": classCode, "fileName": className });
      setSymbolicExecutionResult(responseSymbolic.data);
    } catch (error) {
      setSymbolicExecutionResult('Failed to verify');
    }
    // Update results table and graph data
    setUpdateTable(!updateTable);


  };



  const handleFillWithDemoValues = (demoKey) => {
    const demo = demos[demoKey];
    setClassName(demo.fileName);
    setClassCode(demo.code);
    handleCheckboxChange({ "target": { "checked": checkStyleConfig.length > 0 ? false : true } })
  };
  return (
    <Box sx={{ display: 'flex', m: 2, height: '100vh'}}>
      <Box sx={{ width: '70%' }}>
      <Box sx={{ display:"flex",justifyContent:'space-between' }}>

        <Typography variant="h4" gutterBottom>
          Iterative Feedback Verification Dashboard
        </Typography>
        <Button variant="contained" color="primary" onClick={()=>window.location.reload()} sx={{ m: 2, display: 'flex' }}>
          Clear Iterations
        </Button>
        </Box>

        {/* Button to set all fields to default values */}
        <Box sx={{ background: "#80808042", padding: "10px", borderRadius: "10px" }}>
          <Typography variant="h6" gutterBottom>
            Try with demo completions from chatgpt 3.5
          </Typography>
          {Object.keys(demos).map((demoKey) => (
            <Button
              key={demoKey}
              variant="contained"
              color="secondary"
              onClick={() => handleFillWithDemoValues(demoKey)}
              sx={{ mb: 2, mr: 1 }} // Added some right margin for spacing between buttons
            >
              {demos[demoKey]['title']}
            </Button>
          ))}
        </Box>

        <TextField
          label="Class Name"
          fullWidth
          margin="normal"
          variant="outlined"
          value={className}
          onChange={(e) => setClassName(e.target.value)}
        />
        <TextField
          label="Class Code"
          multiline
          rows={4}
          fullWidth
          margin="normal"
          variant="outlined"
          value={classCode}
          onChange={(e) => setClassCode(e.target.value)}
        />
        <TextField
          label="Checkstyle Configuration"
          multiline
          rows={4}
          fullWidth
          margin="normal"
          variant="outlined"
          value={checkStyleConfig}
          onChange={handleCheckStyleConfigChange}
          disabled={useDefaultCheckStyle}
        />
        <FormControlLabel
          control={<Checkbox checked={useDefaultCheckStyle} onChange={handleCheckboxChange} />}
          label="Use Default Checkstyle Configuration"
        />
        <Button variant="contained" color="primary" onClick={handleSubmit} sx={{ m: 2, display: 'flex' }}>
          Submit
        </Button>
        <Grid container spacing={2} alignItems="flex-start" justifyContent="center">
          <Grid item xs={3}>
            <Paper style={{ position: 'sticky', top: 0, zIndex: 10, background: getPaperColor(checkstyleResult) }}>
              <Typography variant="h6" gutterBottom>
                Checkstyle
              </Typography>
            </Paper>
            <div style={{ maxHeight: 200, overflow: 'auto', padding: 8, background: "cyan" }}>
              {checkstyleResult.result}
            </div>
          </Grid>
          <Grid item >
            <ArrowForwardIosIcon />
          </Grid>
          <Grid item xs={3}>
            <Paper style={{ position: 'sticky', top: 0, zIndex: 10, background: getPaperColor(inferResult) }}>
              <Typography variant="h6" gutterBottom>
                Infer
              </Typography>
            </Paper>
            <div style={{ maxHeight: 200, overflow: 'auto', padding: 8, background: "cyan" }}>
              {inferResult.result}
            </div>
          </Grid>
          <Grid item >
            <ArrowForwardIosIcon />
          </Grid>
          <Grid item xs={3}>
            <Paper style={{ position: 'sticky', top: 0, zIndex: 10, background: getPaperColor(symbolicExecutionResult) }}>
              <Typography variant="h6" gutterBottom>
                Symbolic Execution
              </Typography>
            </Paper>
            <div style={{ maxHeight: 200, overflow: 'auto', padding: 8, background: "cyan" }}>
              {symbolicExecutionResult.result}
            </div>
          </Grid>
        </Grid>


      </Box>
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, width: '30%', ml: 2, height: '100%' }}>
        <Box sx={{ flexGrow: 1, overflow: 'auto' }}> {/* Removed overflow: 'auto' */}
          <Typography sx={{ fontWeight: "bold" }}> Resulted number of issues from the verifier tools</Typography>
          <TableContainer component={Paper}>
            <Table stickyHeader>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: "bold" }}>Iteration Number</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Checkstyle</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Infer</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Symbolic Execution</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {results.map((result, index) => (
                  <TableRow key={index}>
                    <TableCell>{result.iteration}</TableCell>
                    <TableCell>{result.checkstyle}</TableCell>
                    <TableCell>{result.infer}</TableCell>
                    <TableCell>{result.symbolic}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>
        <Box sx={{
          height: "50vh", // Adjusted height
        }}> {/* Removed overflow: 'auto' */}
          <Typography sx={{ fontWeight: 'bold' }}>Graph for Number of issues for every iteration</Typography>
          <BarChart
            dataset={results}
            xAxis={[{ scaleType: 'band', dataKey: 'iteration', label: 'Iteration' }]}
            yAxis={[{ label: 'Number of Issues' }]}
            series={[
              { dataKey: 'checkstyle', label: 'Checkstyle' },
              { dataKey: 'infer', label: 'Infer' },
              { dataKey: 'symbolic', label: 'Symbolic Execution' }
            ]}
          // margin={{ top: 20, bottom: 30, left: 40, right: 10 }}
          />
        </Box>
      </Box>

    </Box>
  );
}

function getPaperColor(result) {
  if (result.status == "pending") {
    return '#FFFF00'; // Yellow for fetching
  } else if (result.status == 0) {
    return '#00FF00'; // Green for success
  } else if (result.status == 1) {
    return 'red'; // Green for success
  } else {
    return '#D3D3D3'; // Grey for initial or error state
  }
}

export default App;
