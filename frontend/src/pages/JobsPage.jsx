import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import apiClient from "../services/apiClient.js";
import StatusBadge from "../components/StatusBadge.jsx";

function JobsPage() {
  const [title, setTitle] = useState("");

  const [companyName, setCompanyName] = useState("");

  const [description, setDescription] = useState("");

  const [jobs, setJobs] = useState([]);

  const [loading, setLoading] = useState(true);

  const [saving, setSaving] = useState(false);

  const [message, setMessage] = useState("");

  const [error, setError] = useState("");

  useEffect(() => {
    loadJobs();
  }, []);

  async function loadJobs() {
    setLoading(true);

    try {
      const response = await apiClient.get("/api/jobs");

      setJobs(response.data || []);
    } catch (err) {
      console.error("Job list error:", err);

      setError(
        err.response?.data?.message || "Unable to load your target jobs.",
      );
    } finally {
      setLoading(false);
    }
  }

  async function saveJob(event) {
    event.preventDefault();

    setError("");
    setMessage("");

    if (!title.trim()) {
      setError("Please enter a job title.");

      return;
    }

    if (description.trim().length < 50) {
      setError("Please paste a more complete job description.");

      return;
    }

    setSaving(true);

    try {
      await apiClient.post("/api/jobs", {
        title: title.trim(),

        companyName: companyName.trim(),

        description: description.trim(),
      });

      setMessage("Target job analyzed successfully.");

      setTitle("");
      setCompanyName("");
      setDescription("");

      await loadJobs();
    } catch (err) {
      console.error("Save job error:", err);

      setError(
        err.response?.data?.message || "Unable to save this target job.",
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="page-container">
      {/* PAGE HEADER */}

      <div className="page-header">
        <p className="eyebrow">JOB INTELLIGENCE</p>

        <h1>Define Your Target Role</h1>

        <p>
          Add a job description and DevHire will identify required and preferred
          technical skills for comparison with your profile.
        </p>
      </div>

      {/* ALERTS */}

      {message && <div className="alert-success">{message}</div>}

      {error && <div className="alert-error">{error}</div>}

      {/* JOB FORM */}

      <div className="panel-card job-form-card">
        <div className="panel-header">
          <div>
            <p className="eyebrow">NEW TARGET</p>

            <h2>Add a Job Description</h2>
          </div>
        </div>

        <form onSubmit={saveJob}>
          <div className="job-form-grid">
            <div className="form-group">
              <label>Job Title</label>

              <input
                type="text"
                value={title}
                onChange={(event) => setTitle(event.target.value)}
                placeholder="e.g. Java Backend Developer"
              />
            </div>

            <div className="form-group">
              <label>Company</label>

              <input
                type="text"
                value={companyName}
                onChange={(event) => setCompanyName(event.target.value)}
                placeholder="e.g. ABC Technologies"
              />
            </div>
          </div>

          <div className="form-group job-description-group">
            <div className="form-label-row">
              <label>Job Description</label>

              <span>{description.length} characters</span>
            </div>

            <textarea
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              placeholder="Paste the complete job description here..."
              rows="12"
            />
          </div>

          <button type="submit" className="primary-button" disabled={saving}>
            {saving ? "Analyzing Job..." : "Save & Analyze Job"}
          </button>
        </form>
      </div>

      {/* JOB LIST */}

      <div className="section-header">
        <div>
          <h2>Your Target Jobs</h2>

          <p>Roles you have already analyzed.</p>
        </div>

        {jobs.length > 0 && (
          <Link to="/analysis" className="secondary-button">
            Run Match Analysis →
          </Link>
        )}
      </div>

      {loading ? (
        <div className="loading-box">Loading target jobs...</div>
      ) : jobs.length === 0 ? (
        <div className="empty-state">
          <h3>No target jobs yet</h3>

          <p>
            Add your first job description above to begin comparing job
            requirements with your skills.
          </p>
        </div>
      ) : (
        <div className="job-list">
          {jobs.map((job) => (
            <div className="job-list-card" key={job.id}>
              <div className="job-icon">JOB</div>

              <div className="job-list-info">
                <h3>{job.title}</h3>

                <p className="job-company">
                  {job.companyName ? job.companyName : "Company not specified"}
                </p>

                <div className="job-meta">
                  <span>Job ID #{job.id}</span>

                  {job.createdAt && (
                    <span>
                      Added {new Date(job.createdAt).toLocaleString()}
                    </span>
                  )}
                </div>
              </div>

              <div className="job-status">
                <StatusBadge value={job.status} />
              </div>
            </div>
          ))}
        </div>
      )}

      {/* NEXT STEP */}

      {jobs.length > 0 && (
        <div className="next-step-card">
          <div>
            <p className="eyebrow">NEXT STEP</p>

            <h2>Compare your profile</h2>

            <p>
              Select one of your resumes and a target job to calculate your
              skill match score.
            </p>
          </div>

          <Link to="/analysis" className="primary-button">
            Run Job Match →
          </Link>
        </div>
      )}
    </div>
  );
}

export default JobsPage;
