import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";

import apiClient from "../services/apiClient.js";
import StatusBadge from "../components/StatusBadge.jsx";

function ResumePage() {
  const [selectedFile, setSelectedFile] = useState(null);

  const [resumes, setResumes] = useState([]);

  const [uploading, setUploading] = useState(false);

  const [loading, setLoading] = useState(true);

  const [message, setMessage] = useState("");

  const [error, setError] = useState("");

  const fileInputRef = useRef(null);

  useEffect(() => {
    loadResumes();
  }, []);

  async function loadResumes() {
    setLoading(true);

    try {
      const response = await apiClient.get("/api/resumes");

      setResumes(response.data || []);
    } catch (err) {
      console.error("Resume list error:", err);

      setError(err.response?.data?.message || "Unable to load your resumes.");
    } finally {
      setLoading(false);
    }
  }

  function handleFileChange(event) {
    setError("");
    setMessage("");

    const file = event.target.files?.[0];

    if (!file) {
      setSelectedFile(null);
      return;
    }

    const fileName = file.name.toLowerCase();

    if (!fileName.endsWith(".pdf") && !fileName.endsWith(".docx")) {
      setError("Please select a PDF or DOCX file.");

      event.target.value = "";

      setSelectedFile(null);

      return;
    }

    setSelectedFile(file);
  }

  async function uploadResume(event) {
    event.preventDefault();

    if (!selectedFile) {
      setError("Please select a resume first.");

      return;
    }

    setUploading(true);
    setError("");
    setMessage("");

    try {
      const formData = new FormData();

      formData.append("file", selectedFile);

      const response = await apiClient.post("/api/resumes", formData);

      setMessage(
        `Resume analyzed successfully — ${response.data.status || "ANALYZED"}`,
      );

      setSelectedFile(null);

      if (fileInputRef.current) {
        fileInputRef.current.value = "";
      }

      await loadResumes();
    } catch (err) {
      console.error("Resume upload error:", err);

      setError(err.response?.data?.message || "Unable to upload this resume.");
    } finally {
      setUploading(false);
    }
  }

  return (
    <div className="page-container">
      {/* PAGE HEADER */}

      <div className="page-header">
        <p className="eyebrow">RESUME INTELLIGENCE</p>

        <h1>Build Your Skill Profile</h1>

        <p>
          Upload your PDF or DOCX resume. DevHire extracts technical skills and
          converts your resume into a structured developer profile.
        </p>
      </div>

      {/* ALERTS */}

      {message && <div className="alert-success">{message}</div>}

      {error && <div className="alert-error">{error}</div>}

      {/* UPLOAD CARD */}

      <div className="panel-card resume-upload-card">
        <div className="panel-header">
          <div>
            <p className="eyebrow">UPLOAD</p>

            <h2>Analyze a Resume</h2>
          </div>
        </div>

        <form onSubmit={uploadResume}>
          <div className="resume-upload-zone">
            <div className="upload-icon">CV</div>

            <div>
              <h3>Choose your resume</h3>

              <p>PDF or DOCX · Maximum 5 MB</p>
            </div>

            <input
              ref={fileInputRef}
              type="file"
              accept=".pdf,.docx"
              onChange={handleFileChange}
              className="resume-file-input"
            />
          </div>

          {selectedFile && (
            <div className="selected-file">
              <div>
                <span className="selected-file-icon">FILE</span>
              </div>

              <div className="selected-file-info">
                <strong>{selectedFile.name}</strong>

                <span>{(selectedFile.size / 1024 / 1024).toFixed(2)} MB</span>
              </div>

              <StatusBadge value="READY" />
            </div>
          )}

          <button
            type="submit"
            className="primary-button resume-upload-button"
            disabled={uploading || !selectedFile}
          >
            {uploading ? "Analyzing Resume..." : "Upload & Analyze Resume"}
          </button>
        </form>
      </div>

      {/* PREVIOUS RESUMES */}

      <div className="section-header">
        <div>
          <h2>Your Resumes</h2>

          <p>Previously uploaded developer profiles.</p>
        </div>

        {resumes.length > 0 && (
          <Link to="/analysis" className="secondary-button">
            Run Analysis →
          </Link>
        )}
      </div>

      {loading ? (
        <div className="loading-box">Loading resumes...</div>
      ) : resumes.length === 0 ? (
        <div className="empty-state">
          <h3>No resumes yet</h3>

          <p>
            Upload your first resume above to begin building your DevHire skill
            profile.
          </p>
        </div>
      ) : (
        <div className="resume-list">
          {resumes.map((resume) => (
            <div className="resume-list-card" key={resume.id}>
              <div className="resume-document-icon">CV</div>

              <div className="resume-list-info">
                <h3>{resume.originalFileName}</h3>

                <p>Resume ID #{resume.id}</p>

                {resume.uploadedAt && (
                  <span>
                    Uploaded {new Date(resume.uploadedAt).toLocaleString()}
                  </span>
                )}
              </div>

              <div className="resume-status">
                <StatusBadge value={resume.status} />
              </div>
            </div>
          ))}
        </div>
      )}

      {/* NEXT STEP */}

      {resumes.length > 0 && (
        <div className="next-step-card">
          <div>
            <p className="eyebrow">NEXT STEP</p>

            <h2>Ready to compare?</h2>

            <p>
              Add a target job and compare its requirements against your resume
              and GitHub evidence.
            </p>
          </div>

          <Link to="/jobs" className="primary-button">
            Add Target Job →
          </Link>
        </div>
      )}
    </div>
  );
}

export default ResumePage;
