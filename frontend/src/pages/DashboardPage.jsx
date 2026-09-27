import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../services/apiClient.js";

function DashboardPage() {
  const [dashboard, setDashboard] = useState(null);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  useEffect(() => {
    loadDashboard();
  }, []);

  async function loadDashboard() {
    setLoading(true);
    setError("");

    try {
      const response = await apiClient.get("/api/dashboard");

      setDashboard(response.data);
    } catch (err) {
      console.error("Dashboard error:", err);

      setError(err.response?.data?.message || "Unable to load dashboard.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading-box">Loading your DevHire dashboard...</div>
      </div>
    );
  }

  return (
    <div className="page-container">
      {/* HEADER */}

      <div className="page-header">
        <p className="eyebrow">DEVELOPER INTELLIGENCE</p>

        <h1>Welcome back, {dashboard?.fullName}</h1>

        <p>
          Track your job readiness, verified technical evidence and learning
          priorities.
        </p>
      </div>

      {error && <div className="alert-error">{error}</div>}

      {/* SCORE OVERVIEW */}

      <div className="dashboard-stats">
        <div className="stat-card stat-card-main">
          <p className="stat-label">Latest Job Match</p>

          <h2 className="stat-number-large">
            {dashboard?.latestMatchScore != null
              ? `${dashboard.latestMatchScore}%`
              : "—"}
          </h2>

          <p className="muted-text">Resume + GitHub evidence</p>
        </div>

        <div className="stat-card">
          <p className="stat-label">Matched</p>

          <h2 className="stat-number">{dashboard?.matchedCount ?? 0}</h2>

          <p className="muted-text">Skills ready</p>
        </div>

        <div className="stat-card">
          <p className="stat-label">Partial</p>

          <h2 className="stat-number">{dashboard?.partialCount ?? 0}</h2>

          <p className="muted-text">Skills needing improvement</p>
        </div>

        <div className="stat-card">
          <p className="stat-label">Missing</p>

          <h2 className="stat-number">{dashboard?.missingCount ?? 0}</h2>

          <p className="muted-text">Skills to learn</p>
        </div>
      </div>

      {/* QUICK ACTIONS */}

      <div className="section-header">
        <div>
          <h2>Quick Actions</h2>

          <p>Continue building your developer profile.</p>
        </div>
      </div>

      <div className="action-grid">
        <Link to="/resume" className="action-card">
          <div className="action-icon">CV</div>

          <h3>Resume Intelligence</h3>

          <p>Upload your resume and extract technical skills.</p>

          <span>Open Resume →</span>
        </Link>

        <Link to="/jobs" className="action-card">
          <div className="action-icon">JOB</div>

          <h3>Target Jobs</h3>

          <p>Add job descriptions and analyze their skill requirements.</p>

          <span>View Jobs →</span>
        </Link>

        <Link to="/github" className="action-card">
          <div className="action-icon">GH</div>

          <h3>GitHub Evidence</h3>

          <p>Verify technical skills using public repository evidence.</p>

          <span>Analyze GitHub →</span>
        </Link>

        <Link to="/analysis" className="action-card">
          <div className="action-icon">AI</div>

          <h3>Job Match Analysis</h3>

          <p>Compare your profile against a target job.</p>

          <span>Run Analysis →</span>
        </Link>
      </div>

      {/* INTELLIGENCE SECTION */}

      <div className="dashboard-two-column">
        {/* GITHUB */}

        <div className="panel-card">
          <div className="panel-header">
            <div>
              <p className="eyebrow">VERIFIED EVIDENCE</p>

              <h2>GitHub Proof</h2>
            </div>

            <Link to="/github" className="text-link">
              View →
            </Link>
          </div>

          <div className="evidence-row">
            <div>
              <span className="evidence-dot strong-dot" />
              Strong
            </div>

            <strong>{dashboard?.strongGithubEvidence ?? 0}</strong>
          </div>

          <div className="evidence-row">
            <div>
              <span className="evidence-dot moderate-dot" />
              Moderate
            </div>

            <strong>{dashboard?.moderateGithubEvidence ?? 0}</strong>
          </div>

          <div className="evidence-row">
            <div>
              <span className="evidence-dot weak-dot" />
              Weak
            </div>

            <strong>{dashboard?.weakGithubEvidence ?? 0}</strong>
          </div>
        </div>

        {/* SKILLS TO LEARN */}

        <div className="panel-card">
          <div className="panel-header">
            <div>
              <p className="eyebrow">LEARNING PRIORITY</p>

              <h2>Top Skills To Learn</h2>
            </div>
          </div>

          {dashboard?.topSkillsToLearn?.length === 0 ? (
            <div className="empty-state">No skill gaps available yet.</div>
          ) : (
            <div className="skill-list">
              {dashboard?.topSkillsToLearn?.map((skill, index) => (
                <div className="skill-list-item" key={`${skill}-${index}`}>
                  <span className="skill-number">{index + 1}</span>

                  <span>{skill}</span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* RECENT ANALYSIS */}

      <div className="panel-card recent-section">
        <div className="panel-header">
          <div>
            <p className="eyebrow">HISTORY</p>

            <h2>Recent Analyses</h2>
          </div>

          <Link to="/analysis" className="text-link">
            New Analysis →
          </Link>
        </div>

        {dashboard?.recentAnalyses?.length === 0 ? (
          <div className="empty-state">
            No analyses yet.
            <br />
            Run your first job match analysis to see results here.
          </div>
        ) : (
          <div className="recent-analysis-list">
            {dashboard?.recentAnalyses?.map((analysis) => (
              <div className="recent-analysis-item" key={analysis.analysisId}>
                <div>
                  <h3>{analysis.jobTitle}</h3>

                  <p>Analysis #{analysis.analysisId}</p>
                </div>

                <div className="analysis-score">
                  <strong>{analysis.score}%</strong>

                  <span>match</span>
                </div>

                <Link
                  to={`/learning-plan/${analysis.analysisId}`}
                  className="secondary-button"
                >
                  Learning Plan
                </Link>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default DashboardPage;
