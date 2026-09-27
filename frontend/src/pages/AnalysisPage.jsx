import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import apiClient from "../services/apiClient.js";
import StatusBadge from "../components/StatusBadge.jsx";

function AnalysisPage() {
  const [resumes, setResumes] = useState([]);

  const [jobs, setJobs] = useState([]);

  const [resumeId, setResumeId] = useState("");

  const [jobId, setJobId] = useState("");

  const [result, setResult] = useState(null);

  const [loadingOptions, setLoadingOptions] = useState(true);

  const [analyzing, setAnalyzing] = useState(false);

  const [error, setError] = useState("");

  useEffect(() => {
    loadOptions();
  }, []);

  async function loadOptions() {
    setLoadingOptions(true);
    setError("");

    try {
      const [resumeResponse, jobResponse] = await Promise.all([
        apiClient.get("/api/resumes"),
        apiClient.get("/api/jobs"),
      ]);

      const resumeList = resumeResponse.data || [];

      const jobList = jobResponse.data || [];

      setResumes(resumeList);
      setJobs(jobList);

      if (resumeList.length > 0) {
        setResumeId(String(resumeList[0].id));
      }

      if (jobList.length > 0) {
        setJobId(String(jobList[0].id));
      }
    } catch (err) {
      console.error("Unable to load analysis options:", err);

      setError(
        err.response?.data?.message || "Unable to load resumes and jobs.",
      );
    } finally {
      setLoadingOptions(false);
    }
  }

  async function runAnalysis(event) {
    event.preventDefault();

    if (!resumeId || !jobId) {
      setError("Please select both a resume and a job.");

      return;
    }

    setAnalyzing(true);
    setError("");
    setResult(null);

    try {
      const response = await apiClient.post("/api/analysis", {
        resumeId: Number(resumeId),

        jobId: Number(jobId),
      });

      setResult(response.data);
    } catch (err) {
      console.error("Analysis error:", err);

      setError(
        err.response?.data?.message || "Unable to run job match analysis.",
      );
    } finally {
      setAnalyzing(false);
    }
  }

  const skillResults =
    result?.skills || result?.skillResults || result?.results || [];

  return (
    <div className="page-container">
      {/* PAGE HEADER */}

      <div className="page-header">
        <p className="eyebrow">MATCH INTELLIGENCE</p>

        <h1>Job Match Analysis</h1>

        <p>
          Compare your resume, target job requirements and GitHub proof of skill
          to generate a complete DevHire match score.
        </p>
      </div>

      {/* ERROR */}

      {error && <div className="alert-error">{error}</div>}

      {/* SELECTION CARD */}

      <div className="panel-card analysis-selection-card">
        <div className="panel-header">
          <div>
            <p className="eyebrow">ANALYSIS INPUT</p>

            <h2>Select Profile & Target</h2>
          </div>
        </div>

        {loadingOptions ? (
          <div className="loading-box">Loading resumes and jobs...</div>
        ) : (
          <form onSubmit={runAnalysis}>
            <div className="analysis-select-grid">
              {/* RESUME */}

              <div className="form-group">
                <label>Resume</label>

                <select
                  value={resumeId}
                  onChange={(event) => setResumeId(event.target.value)}
                >
                  {resumes.length === 0 && (
                    <option value="">No resumes available</option>
                  )}

                  {resumes.map((resume) => (
                    <option key={resume.id} value={resume.id}>
                      {resume.originalFileName}
                      {" — "}
                      {resume.status}
                    </option>
                  ))}
                </select>

                {resumes.length === 0 && (
                  <p className="helper-text">
                    No resume found.{" "}
                    <Link to="/resume" className="text-link">
                      Upload one →
                    </Link>
                  </p>
                )}
              </div>

              {/* JOB */}

              <div className="form-group">
                <label>Target Job</label>

                <select
                  value={jobId}
                  onChange={(event) => setJobId(event.target.value)}
                >
                  {jobs.length === 0 && (
                    <option value="">No jobs available</option>
                  )}

                  {jobs.map((job) => (
                    <option key={job.id} value={job.id}>
                      {job.title}

                      {job.companyName ? ` — ${job.companyName}` : ""}
                    </option>
                  ))}
                </select>

                {jobs.length === 0 && (
                  <p className="helper-text">
                    No target job found.{" "}
                    <Link to="/jobs" className="text-link">
                      Add one →
                    </Link>
                  </p>
                )}
              </div>
            </div>

            <button
              type="submit"
              className="primary-button analysis-run-button"
              disabled={analyzing || !resumeId || !jobId}
            >
              {analyzing ? "Analyzing Match..." : "Run Job Match Analysis"}
            </button>
          </form>
        )}
      </div>

      {/* RESULT */}

      {result && (
        <div className="analysis-results">
          {/* SCORE HERO */}

          <div className="analysis-score-card">
            <div>
              <p className="eyebrow">OVERALL MATCH</p>

              <h2 className="analysis-main-score">{result.overallScore}%</h2>

              <p className="analysis-score-description">
                Combined resume and GitHub evidence against the selected target
                role.
              </p>
            </div>

            <div className="analysis-score-details">
              <div>
                <span>Resume / Job Score</span>

                <strong>{result.baseScore}%</strong>
              </div>

              <div>
                <span>GitHub Proof Bonus</span>

                <strong>+{result.githubBonus}</strong>
              </div>
            </div>
          </div>

          {/* COUNTS */}

          <div className="analysis-stat-grid">
            <div className="analysis-stat-card analysis-match-card">
              <span>Matched</span>

              <strong>{result.matchedCount ?? 0}</strong>

              <p>Skills confidently supported.</p>
            </div>

            <div className="analysis-stat-card analysis-partial-card">
              <span>Partial</span>

              <strong>{result.partialCount ?? 0}</strong>

              <p>Skills with partial evidence.</p>
            </div>

            <div className="analysis-stat-card analysis-missing-card">
              <span>Missing</span>

              <strong>{result.missingCount ?? 0}</strong>

              <p>Skills currently unsupported.</p>
            </div>
          </div>

          {/* LEARNING PLAN CTA */}

          <div className="analysis-learning-cta">
            <div>
              <p className="eyebrow">NEXT ACTION</p>

              <h2>Close your skill gaps</h2>

              <p>
                Generate a personalized learning roadmap based on the missing
                and partial skills in this analysis.
              </p>
            </div>

            <Link
              to={`/learning-plan/${result.analysisId}`}
              className="primary-button"
            >
              Generate Learning Plan →
            </Link>
          </div>

          {/* SKILL BREAKDOWN */}

          <div className="section-header">
            <div>
              <p className="eyebrow">SKILL INTELLIGENCE</p>

              <h2>Skill Breakdown</h2>

              <p>
                Resume evidence, requirement level and GitHub support for every
                analyzed skill.
              </p>
            </div>
          </div>

          {skillResults.length === 0 ? (
            <div className="empty-state">No skill results available.</div>
          ) : (
            <div className="analysis-skill-list">
              {skillResults.map((item, index) => {
                const candidatePercent =
                  item.candidateConfidence != null
                    ? Math.round(item.candidateConfidence * 100)
                    : null;

                const githubPercent =
                  item.githubConfidence != null
                    ? Math.round(item.githubConfidence * 100)
                    : null;

                return (
                  <div
                    className="analysis-skill-card"
                    key={`${item.skill}-${index}`}
                  >
                    <div className="analysis-skill-header">
                      <div>
                        <h3>{item.skill}</h3>

                        <div className="analysis-badge-row">
                          <StatusBadge value={item.status} />

                          <StatusBadge value={item.requirementLevel} />
                        </div>
                      </div>

                      {candidatePercent != null && (
                        <div className="skill-confidence-number">
                          <strong>{candidatePercent}%</strong>

                          <span>resume evidence</span>
                        </div>
                      )}
                    </div>

                    {/* RESUME CONFIDENCE */}

                    {candidatePercent != null && (
                      <div className="analysis-evidence-block">
                        <div className="confidence-header">
                          <span>Resume Evidence Confidence</span>

                          <strong>{candidatePercent}%</strong>
                        </div>

                        <div className="confidence-track">
                          <div
                            className="confidence-fill"
                            style={{
                              width: `${candidatePercent}%`,
                            }}
                          />
                        </div>
                      </div>
                    )}

                    {/* GITHUB */}

                    <div className="analysis-github-row">
                      <div>
                        <span className="analysis-small-label">
                          GitHub Evidence
                        </span>

                        {item.githubEvidenceLevel ? (
                          <StatusBadge value={item.githubEvidenceLevel} />
                        ) : (
                          <span className="muted-text">No evidence</span>
                        )}
                      </div>

                      {githubPercent != null && (
                        <div className="github-confidence-value">
                          <strong>{githubPercent}%</strong>

                          <span>confidence</span>
                        </div>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default AnalysisPage;
