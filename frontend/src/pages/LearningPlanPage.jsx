import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import apiClient from "../services/apiClient.js";
import StatusBadge from "../components/StatusBadge.jsx";

function LearningPlanPage() {
  const { analysisId } = useParams();

  const [plan, setPlan] = useState(null);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  useEffect(() => {
    generatePlan();
  }, [analysisId]);

  async function generatePlan() {
    setLoading(true);
    setError("");

    try {
      const response = await apiClient.post(`/api/learning-plan/${analysisId}`);

      setPlan(response.data);
    } catch (err) {
      console.error("Learning plan error:", err);

      setError(
        err.response?.data?.message || "Unable to generate your learning plan.",
      );
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading-box">
          <h3>Building your learning roadmap...</h3>

          <p>
            DevHire is converting your skill gaps into practical learning
            priorities.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      {/* PAGE HEADER */}

      <div className="page-header">
        <p className="eyebrow">LEARNING INTELLIGENCE</p>

        <h1>Personalized Learning Plan</h1>

        <p>
          A focused roadmap generated from your missing and partial job skills
          so you know exactly what to learn next.
        </p>
      </div>

      {/* ERROR */}

      {error && (
        <div className="alert-error">
          <p>{error}</p>

          <button className="primary-button" onClick={generatePlan}>
            Try Again
          </button>
        </div>
      )}

      {plan && (
        <>
          {/* SUMMARY */}

          <div className="learning-summary-card">
            <div>
              <p className="eyebrow">TARGET ROLE</p>

              <h2>{plan.targetJob}</h2>

              <p>
                Learning priorities are based on your latest DevHire match
                analysis.
              </p>
            </div>

            <div className="learning-score">
              <span>Current Match</span>

              <strong>{plan.matchScore}%</strong>
            </div>
          </div>

          {/* EMPTY STATE */}

          {plan.recommendations?.length === 0 ? (
            <div className="learning-complete-card">
              <div className="learning-complete-icon">✓</div>

              <h2>No major skill gaps detected</h2>

              <p>
                Your current profile already covers the analyzed job skills
                strongly enough that DevHire has no learning priorities to
                recommend.
              </p>

              <Link to="/analysis" className="primary-button">
                Run Another Analysis →
              </Link>
            </div>
          ) : (
            <>
              {/* ROADMAP HEADER */}

              <div className="section-header">
                <div>
                  <p className="eyebrow">ROADMAP</p>

                  <h2>Your Learning Priorities</h2>

                  <p>Work through these skills in priority order.</p>
                </div>

                <span className="roadmap-count">
                  {plan.recommendations.length}{" "}
                  {plan.recommendations.length === 1
                    ? "priority"
                    : "priorities"}
                </span>
              </div>

              {/* ROADMAP */}

              <div className="learning-roadmap">
                {plan.recommendations.map((item, index) => (
                  <div
                    className="learning-roadmap-item"
                    key={`${item.priority}-${item.skill}`}
                  >
                    {/* TIMELINE */}

                    <div className="learning-timeline">
                      <div className="learning-priority-number">
                        {item.priority}
                      </div>

                      {index < plan.recommendations.length - 1 && (
                        <div className="learning-timeline-line" />
                      )}
                    </div>

                    {/* CARD */}

                    <div className="learning-card">
                      <div className="learning-card-header">
                        <div>
                          <p className="learning-priority-label">
                            PRIORITY {item.priority}
                          </p>

                          <h2>{item.skill}</h2>

                          <div className="learning-badge-row">
                            <StatusBadge value={item.priorityLevel} />

                            <StatusBadge value={item.requirementLevel} />

                            <StatusBadge value={item.status} />
                          </div>
                        </div>

                        <div className="difficulty-box">
                          <span>Difficulty</span>

                          <strong>{item.difficulty}</strong>
                        </div>
                      </div>

                      {/* WHY */}

                      <div className="learning-section">
                        <div className="learning-section-label">
                          WHY THIS MATTERS
                        </div>

                        <p>{item.whyNeeded}</p>
                      </div>

                      {/* WHAT */}

                      <div className="learning-section">
                        <div className="learning-section-label">
                          WHAT TO LEARN
                        </div>

                        <p>{item.whatToLearn}</p>
                      </div>

                      {/* SEQUENCE */}

                      <div className="learning-section">
                        <div className="learning-section-label">
                          LEARNING SEQUENCE
                        </div>

                        <p>{item.learningSequence}</p>
                      </div>

                      {/* PROJECT */}

                      <div className="learning-project">
                        <div className="learning-project-icon">BUILD</div>

                        <div>
                          <span>PRACTICAL PROJECT</span>

                          <h3>{item.suggestedProject}</h3>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </>
          )}

          {/* BOTTOM ACTIONS */}

          <div className="learning-bottom-actions">
            <Link to="/analysis" className="secondary-button">
              ← Back to Analysis
            </Link>

            <Link to="/dashboard" className="primary-button">
              Go to Dashboard
            </Link>
          </div>
        </>
      )}
    </div>
  );
}

export default LearningPlanPage;
