import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";

function LandingPage() {
  return (
    <div>
      <Navbar />

      <main>
        <section className="hero">
          <div className="hero-content">
            <span className="hero-badge">
              AI-Powered Developer Intelligence
            </span>

            <h1>
              Know exactly what you're missing
              <span> before you apply.</span>
            </h1>

            <p>
              DevHire analyzes your resume, GitHub projects and target job
              description to show your strengths, missing skills and the
              smartest path to becoming job-ready.
            </p>

            <div className="hero-buttons">
              <Link to="/register" className="primary-button">
                Analyze My Profile
              </Link>

              <a href="#how-it-works" className="secondary-button">
                See How It Works
              </a>
            </div>
          </div>
        </section>

        <section id="how-it-works" className="section">
          <div className="section-heading">
            <p className="section-label">HOW IT WORKS</p>

            <h2>From resume to job-readiness insights</h2>

            <p>
              DevHire combines your profile, target job and real project
              evidence to give you a clearer picture of your readiness.
            </p>
          </div>

          <div className="cards">
            <div className="feature-card">
              <div className="card-number">01</div>
              <h3>Upload Your Resume</h3>
              <p>
                DevHire extracts and organizes your technical skills using AI.
              </p>
            </div>

            <div className="feature-card">
              <div className="card-number">02</div>
              <h3>Add Your Target Job</h3>
              <p>
                Paste a job description and identify required and preferred
                technologies.
              </p>
            </div>

            <div className="feature-card">
              <div className="card-number">03</div>
              <h3>Connect GitHub</h3>
              <p>
                Public repositories provide supporting evidence for the skills
                you demonstrate.
              </p>
            </div>

            <div className="feature-card">
              <div className="card-number">04</div>
              <h3>Get Your Roadmap</h3>
              <p>
                Receive a match score, missing skills and a personalized action
                plan.
              </p>
            </div>
          </div>
        </section>

        <section className="section intelligence-section">
          <div className="section-heading">
            <p className="section-label">DEVHIRE INTELLIGENCE</p>

            <h2>More than keyword matching</h2>
          </div>

          <div className="intelligence-grid">
            <div className="intelligence-card">
              <h3>AI Skill Analysis</h3>
              <p>
                Understand the technologies, frameworks and tools demonstrated
                in your resume.
              </p>
            </div>

            <div className="intelligence-card">
              <h3>Proof of Skill</h3>
              <p>
                GitHub repository evidence helps distinguish claimed skills from
                demonstrated project experience.
              </p>
            </div>

            <div className="intelligence-card">
              <h3>Job Match Score</h3>
              <p>
                See matched, partial and missing skills rather than receiving a
                meaningless keyword count.
              </p>
            </div>

            <div className="intelligence-card">
              <h3>Learning Plan</h3>
              <p>
                Prioritize what to learn next according to the job you actually
                want.
              </p>
            </div>
          </div>
        </section>

        <section className="cta-section">
          <h2>Turn uncertainty into a clear action plan.</h2>

          <p>Analyze your skills before sending your next application.</p>

          <Link to="/register" className="primary-button">
            Start Free Analysis
          </Link>
        </section>
      </main>

      <footer>
        <p>DevHire — AI-Powered Developer Skill & Job Intelligence Platform</p>
      </footer>
    </div>
  );
}

export default LandingPage;
