import { useState } from "react";
import { Link } from "react-router-dom";

import apiClient from "../services/apiClient.js";
import StatusBadge from "../components/StatusBadge.jsx";

function GithubPage() {
  const [username, setUsername] = useState("");

  const [repositories, setRepositories] = useState([]);

  const [analysis, setAnalysis] = useState(null);

  const [loadingRepos, setLoadingRepos] = useState(false);

  const [analyzing, setAnalyzing] = useState(false);

  const [error, setError] = useState("");

  const [message, setMessage] = useState("");

  async function loadRepositories(event) {
    event.preventDefault();

    const cleanUsername = username.trim();

    if (!cleanUsername) {
      setError("Please enter a GitHub username.");

      return;
    }

    setLoadingRepos(true);
    setError("");
    setMessage("");
    setRepositories([]);
    setAnalysis(null);

    try {
      const response = await apiClient.get(
        `/api/github/${encodeURIComponent(cleanUsername)}/repositories`,
      );

      const data = response.data || [];

      setRepositories(data);

      if (data.length === 0) {
        setMessage(
          "No public repositories were found for this GitHub account.",
        );
      } else {
        setMessage(`${data.length} public repositories loaded successfully.`);
      }
    } catch (err) {
      console.error("GitHub repository error:", err);

      setError(
        err.response?.data?.message || "Unable to load GitHub repositories.",
      );
    } finally {
      setLoadingRepos(false);
    }
  }

  async function analyzeGithub() {
    const cleanUsername = username.trim();

    if (!cleanUsername) {
      return;
    }

    setAnalyzing(true);
    setError("");
    setMessage("");

    try {
      const response = await apiClient.post(
        `/api/github/${encodeURIComponent(cleanUsername)}/analyze`,
      );

      setAnalysis(response.data);

      setMessage("GitHub proof-of-skill analysis completed.");
    } catch (err) {
      console.error("GitHub analysis error:", err);

      setError(
        err.response?.data?.message ||
          "Unable to analyze GitHub proof of skill.",
      );
    } finally {
      setAnalyzing(false);
    }
  }

  const analyzedRepositories =
    analysis?.repositories ||
    analysis?.repositoryEvidence ||
    analysis?.results ||
    [];

  return (
    <div className="page-container">
      {/* PAGE HEADER */}

      <div className="page-header">
        <p className="eyebrow">CODE INTELLIGENCE</p>

        <h1>GitHub Proof of Skill</h1>

        <p>
          DevHire analyzes public repository evidence to support technical
          skills with real code signals instead of relying only on resume
          claims.
        </p>
      </div>

      {/* ALERTS */}

      {message && <div className="alert-success">{message}</div>}

      {error && <div className="alert-error">{error}</div>}

      {/* GITHUB SEARCH */}

      <div className="panel-card github-search-card">
        <div className="panel-header">
          <div>
            <p className="eyebrow">GITHUB ACCOUNT</p>

            <h2>Connect Public Repositories</h2>
          </div>
        </div>

        <form onSubmit={loadRepositories} className="github-search-form">
          <div className="form-group github-input-group">
            <label>GitHub Username</label>

            <input
              type="text"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              placeholder="e.g. devanshshaha29-cyber"
            />
          </div>

          <button
            type="submit"
            className="primary-button"
            disabled={loadingRepos}
          >
            {loadingRepos ? "Loading..." : "Load Repositories"}
          </button>
        </form>
      </div>

      {/* REPOSITORIES */}

      {repositories.length > 0 && (
        <>
          <div className="section-header">
            <div>
              <h2>Public Repositories</h2>

              <p>DevHire found these repositories for analysis.</p>
            </div>

            <button
              className="primary-button"
              onClick={analyzeGithub}
              disabled={analyzing}
            >
              {analyzing ? "Analyzing Evidence..." : "Analyze Proof of Skill"}
            </button>
          </div>

          <div className="github-repo-grid">
            {repositories.map((repo, index) => (
              <div
                className="github-repo-card"
                key={
                  repo.id || repo.githubRepositoryId || `${repo.name}-${index}`
                }
              >
                <div className="github-repo-top">
                  <div className="github-repo-icon">GH</div>

                  <div className="github-repo-heading">
                    <h3>{repo.name || repo.fullName || "Repository"}</h3>

                    <p>{repo.description || "No description available"}</p>
                  </div>
                </div>

                <div className="github-repo-meta">
                  <div>
                    <span>Language</span>

                    <strong>
                      {repo.primaryLanguage || repo.language || "Unknown"}
                    </strong>
                  </div>

                  <div>
                    <span>Stars</span>

                    <strong>{repo.stars ?? 0}</strong>
                  </div>

                  <div>
                    <span>Forks</span>

                    <strong>{repo.forks ?? 0}</strong>
                  </div>
                </div>

                {(repo.githubUrl || repo.htmlUrl) && (
                  <a
                    href={repo.githubUrl || repo.htmlUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="text-link github-repo-link"
                  >
                    View Repository →
                  </a>
                )}
              </div>
            ))}
          </div>
        </>
      )}

      {/* ANALYSIS RESULTS */}

      {analysis && (
        <div className="github-results-section">
          <div className="section-header">
            <div>
              <p className="eyebrow">VERIFIED SIGNALS</p>

              <h2>Proof of Skill Results</h2>

              <p>
                Evidence is derived from repository content such as languages,
                dependencies, configuration files and technology indicators.
              </p>
            </div>
          </div>

          {analyzedRepositories.length === 0 ? (
            <div className="empty-state">
              <h3>No reliable evidence found</h3>

              <p>
                Absence of GitHub evidence does not mean that you do not possess
                a skill.
              </p>
            </div>
          ) : (
            <div className="github-analysis-list">
              {analyzedRepositories.map((repo, index) => {
                const evidence =
                  repo.evidence ||
                  repo.evidences ||
                  repo.skillEvidence ||
                  repo.skills ||
                  [];

                return (
                  <div
                    className="github-analysis-card"
                    key={repo.repositoryId || repo.id || index}
                  >
                    <div className="github-analysis-header">
                      <div>
                        <p className="eyebrow">REPOSITORY</p>

                        <h3>
                          {repo.repositoryName ||
                            repo.name ||
                            repo.fullName ||
                            "Repository"}
                        </h3>
                      </div>

                      <span className="github-evidence-count">
                        {evidence.length}{" "}
                        {evidence.length === 1 ? "skill" : "skills"}
                      </span>
                    </div>

                    {evidence.length === 0 ? (
                      <div className="github-no-evidence">
                        No reliable technology evidence detected.
                      </div>
                    ) : (
                      <div className="github-evidence-list">
                        {evidence.map((item, evidenceIndex) => (
                          <div
                            className="github-evidence-item"
                            key={`${
                              item.skill || item.skillName
                            }-${evidenceIndex}`}
                          >
                            <div className="github-evidence-main">
                              <div>
                                <h4>{item.skill || item.skillName}</h4>

                                {(item.evidenceSummary || item.summary) && (
                                  <p>{item.evidenceSummary || item.summary}</p>
                                )}
                              </div>

                              <StatusBadge
                                value={item.evidenceLevel || item.level}
                              />
                            </div>

                            {item.confidence != null && (
                              <div className="github-confidence">
                                <div className="confidence-header">
                                  <span>Confidence</span>

                                  <strong>
                                    {Math.round(item.confidence * 100)}%
                                  </strong>
                                </div>

                                <div className="confidence-track">
                                  <div
                                    className="confidence-fill"
                                    style={{
                                      width: `${Math.round(
                                        item.confidence * 100,
                                      )}%`,
                                    }}
                                  />
                                </div>
                              </div>
                            )}
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          )}

          <div className="next-step-card">
            <div>
              <p className="eyebrow">NEXT STEP</p>

              <h2>Use evidence in your match</h2>

              <p>
                Run a job analysis to combine resume skills, job requirements
                and GitHub proof into one DevHire match score.
              </p>
            </div>

            <Link to="/analysis" className="primary-button">
              Run Job Analysis →
            </Link>
          </div>
        </div>
      )}
    </div>
  );
}

export default GithubPage;
