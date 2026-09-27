import { Link, useLocation, useNavigate } from "react-router-dom";

function Navbar() {
  const location = useLocation();
  const navigate = useNavigate();

  const token = localStorage.getItem("devhire_token");

  function logout() {
    localStorage.clear();
    navigate("/login");
  }

  function isActive(path) {
    return location.pathname === path;
  }

  if (!token) {
    return null;
  }

  return (
    <nav className="devhire-navbar">
      <Link to="/dashboard" className="navbar-brand">
        DevHire
      </Link>

      <div className="navbar-links">
        <Link
          to="/dashboard"
          className={isActive("/dashboard") ? "nav-link active" : "nav-link"}
        >
          Dashboard
        </Link>

        <Link
          to="/resume"
          className={isActive("/resume") ? "nav-link active" : "nav-link"}
        >
          Resume
        </Link>

        <Link
          to="/jobs"
          className={isActive("/jobs") ? "nav-link active" : "nav-link"}
        >
          Jobs
        </Link>

        <Link
          to="/github"
          className={isActive("/github") ? "nav-link active" : "nav-link"}
        >
          GitHub
        </Link>

        <Link
          to="/analysis"
          className={isActive("/analysis") ? "nav-link active" : "nav-link"}
        >
          Analysis
        </Link>
      </div>

      <button className="navbar-logout" onClick={logout}>
        Logout
      </button>
    </nav>
  );
}

export default Navbar;
