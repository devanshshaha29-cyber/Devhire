function StatusBadge({ value }) {
  if (!value) {
    return null;
  }

  const normalized = value.toUpperCase();

  let className = "status-badge status-neutral";

  if (
    normalized === "MATCHED" ||
    normalized === "STRONG" ||
    normalized === "ANALYZED" ||
    normalized === "LOW"
  ) {
    className = "status-badge status-success";
  } else if (
    normalized === "PARTIAL" ||
    normalized === "MODERATE" ||
    normalized === "PREFERRED" ||
    normalized === "MEDIUM"
  ) {
    className = "status-badge status-warning";
  } else if (
    normalized === "MISSING" ||
    normalized === "WEAK" ||
    normalized === "HIGH"
  ) {
    className = "status-badge status-danger";
  } else if (normalized === "REQUIRED") {
    className = "status-badge status-info";
  }

  return <span className={className}>{value}</span>;
}

export default StatusBadge;
