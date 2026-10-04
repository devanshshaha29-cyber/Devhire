import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import apiClient from "../services/apiClient";

function LoginPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((previousData) => ({
      ...previousData,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      const response = await apiClient.post(
        "/api/auth/login",
        formData,
      );

      localStorage.setItem("devhire_token", response.data.token);
      localStorage.setItem("devhire_user_name", response.data.fullName);
      localStorage.setItem("devhire_user_email", response.data.email);

      navigate("/dashboard");
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message ||
          "Login failed. Please check your email and password.",
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <Link to="/" className="auth-logo">
          DevHire
        </Link>

        <h1>Welcome back</h1>

        <p>Login to continue your DevHire journey.</p>

        <form onSubmit={handleSubmit}>
          <label>Email</label>

          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="you@example.com"
            required
          />

          <label>Password</label>

          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            placeholder="Enter your password"
            required
          />

          {error && (
            <p style={{ color: "#f87171", marginTop: "15px" }}>{error}</p>
          )}

          <button
            type="submit"
            className="primary-button auth-submit"
            disabled={loading}
          >
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        <p className="auth-switch">
          Don't have an account? <Link to="/register">Create one</Link>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;
