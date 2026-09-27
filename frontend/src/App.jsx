import { Routes, Route } from "react-router-dom";
import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import DashboardPage from "./pages/DashboardPage";
import ResumePage from "./pages/ResumePage";
import JobsPage from "./pages/JobsPage";
import AnalysisPage from "./pages/AnalysisPage";
import GithubPage from "./pages/GithubPage";
import LearningPlanPage from "./pages/LearningPlanPage";

import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
   
     
      <Routes>
        <Route path="/" element={<LandingPage />} />

        <Route path="/login" element={<LoginPage />} />

        <Route path="/register" element={<RegisterPage />} />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/resume"
          element={
            <ProtectedRoute>
              <ResumePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/jobs"
          element={
            <ProtectedRoute>
              <JobsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/analysis"
          element={
            <ProtectedRoute>
              <AnalysisPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/github"
          element={
            <ProtectedRoute>
              <GithubPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/learning-plan/:analysisId"
          element={
            <ProtectedRoute>
              <LearningPlanPage />
            </ProtectedRoute>
          }
        />
      </Routes>
   
  );
}

export default App;
