import "./App.css";
import { useEffect, useState, useRef } from "react";
import { api } from "./api/axios";

function App() {
  const [problems, setProblems] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [leetcodeUsername, setLeetcodeUsername] = useState("");
  const [syncLoading, setSyncLoading] = useState(false);

  const formRef = useRef(null);

  const [newProblem, setNewProblem] = useState({
    title: "",
    difficulty: "Easy",
    personalDifficulty: "Easy",
    topic: "",
    company: "",
    status: "Solved",
    confidencePercentage: 100,
    revisionCount: 0,
    leetcodeUrl: "",
    notes: "",
    revisionNeeded: false,
  });

  useEffect(() => {
    getProblems();
  }, []);

  // ---------------- FETCH ALL ----------------
  const getProblems = async () => {
    try {
      const res = await api.get("/problems");
      setProblems(res.data);
    } catch (err) {
      console.log(err);
    }
  };

  // ---------------- ADD / UPDATE ----------------
  const addProblem = async () => {
    try {
      const payload = {
        ...newProblem,
        revisionNeeded: Number(newProblem.confidencePercentage) < 70,
      };

      if (editingId) {
        await api.put(`/problems/${editingId}`, payload);
        setEditingId(null);
      } else {
        await api.post("/problems", payload);
      }

      resetForm();
      getProblems();
    } catch (err) {
      console.log(err);
    }
  };

  const resetForm = () => {
    setNewProblem({
      title: "",
      difficulty: "Easy",
      personalDifficulty: "Easy",
      topic: "",
      company: "",
      status: "Solved",
      confidencePercentage: 100,
      revisionCount: 0,
      leetcodeUrl: "",
      notes: "",
      revisionNeeded: false,
    });
  };

  // ---------------- DELETE ----------------
  const deleteProblem = async (id) => {
    try {
      await api.delete(`/problems/${id}`);
      getProblems();
    } catch (err) {
      console.log(err);
    }
  };

  // ---------------- EDIT ----------------
  const editProblem = (problem) => {
    setEditingId(problem.id);

    setNewProblem({
      title: problem.title,
      difficulty: problem.difficulty,
      personalDifficulty: problem.personalDifficulty || "Easy",
      topic: problem.topic,
      company: problem.company,
      status: problem.status,
      confidencePercentage: problem.confidencePercentage || 100,
      revisionCount: problem.revisionCount || 0,
      leetcodeUrl: problem.leetcodeUrl || "",
      notes: problem.notes || "",
      revisionNeeded: problem.revisionNeeded || false,
    });

    if (formRef.current) {
      formRef.current.scrollIntoView({
        behavior: "smooth",
      });
    }
  };

  // ---------------- LEETCODE SYNC ----------------
const syncLeetCode = async () => {
  try {
    setSyncLoading(true);

    const query = `
      query {
        matchedUser(username: "${leetcodeUsername}") {
          submitStats: submitStatsGlobal {
            acSubmissionNum {
              difficulty
              count
            }
          }
        }
      }
    `;

    const res = await fetch("https://leetcode.com/graphql", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ query }),
    });

    const data = await res.json();

    const stats =
      data.data.matchedUser.submitStats.acSubmissionNum;

    let easy = 0, medium = 0, hard = 0;

    stats.forEach((s) => {
      if (s.difficulty === "Easy") easy = s.count;
      if (s.difficulty === "Medium") medium = s.count;
      if (s.difficulty === "Hard") hard = s.count;
    });

    // send to backend
    const backendRes = await api.post("/problems/sync-full", {
      easy,
      medium,
      hard,
    });

    alert(backendRes.data);
    getProblems();

  } catch (err) {
    console.log(err);
    alert("Sync failed (check username)");
  } finally {
    setSyncLoading(false);
  }
};

  // ---------------- SMART SEARCH ----------------
  const searchProblem = async () => {
    if (!searchTerm) return;

    try {
      const res = await api.get(
        `/problems/search?title=${searchTerm}`
      );

      const data = res.data;

      if (data.revisionNeeded) {
        alert("⚠️ This problem is due for revision");
      } else {
        alert("✅ Problem added / moved to journal");
      }

      getProblems();
    } catch (err) {
      console.log(err);
    }
  };

  // ---------------- FILTER ----------------
  const filteredProblems = problems.filter((p) =>
    p.title?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // ---------------- STATS ----------------
  const easyCount = problems.filter((p) => p.difficulty === "Easy").length;
  const mediumCount = problems.filter((p) => p.difficulty === "Medium").length;
  const hardCount = problems.filter((p) => p.difficulty === "Hard").length;

  const revisionQueue = problems.filter(
  (p) => (p.confidencePercentage || 0) < 70
);
  

  const avgConfidence =
    problems.length > 0
      ? Math.round(
          problems.reduce(
            (sum, p) => sum + (p.confidencePercentage || 0),
            0
          ) / problems.length
        )
      : 0;

  return (
    <div className="container">
      {/* HEADER */}
      <div className="header">
        <h1>PrepTrack</h1>
        <p>Your Personal DSA Journal</p>

        {/* LEETCODE SYNC */}
       
      </div>

      {/* STATS CARDS */}
      <div className="cards">
        <div className="card">
          <h3>Total Problems</h3>
          <p>{problems.length}</p>
        </div>
       

        <div className="card">
          <h3>Easy</h3>
          <p>{easyCount}</p>
        </div>

        <div className="card">
          <h3>Medium</h3>
          <p>{mediumCount}</p>
        </div>

        <div className="card">
          <h3>Hard</h3>
          <p>{hardCount}</p>
        </div>
      </div>

      {/* CONTENT */}
      <div className="content">
        {/* FORM */}
        <div className="form-card" ref={formRef}>
          <h2>{editingId ? "Update Problem" : "Add Problem"}</h2>

          <input
            type="text"
            placeholder="Problem Title"
            value={newProblem.title}
            onChange={(e) =>
              setNewProblem({ ...newProblem, title: e.target.value })
            }
          />
          <select
  value={newProblem.difficulty}
  onChange={(e) =>
    setNewProblem({
      ...newProblem,
      difficulty: e.target.value,
    })
  }
>
  <option value="Easy">Easy</option>
  <option value="Medium">Medium</option>
  <option value="Hard">Hard</option>
</select>

         <select
  value={newProblem.personalDifficulty}
  onChange={(e) =>
    setNewProblem({
      ...newProblem,
      personalDifficulty: e.target.value,
    })
  }
>
  <option value="">Personal Difficulty</option>
  <option value="Easy">Easy</option>
  <option value="Medium">Medium</option>
  <option value="Hard">Hard</option>
  <option value="Very Hard">Very Hard</option>
</select>

          <input
            type="text"
            placeholder="Topic"
            
            value={newProblem.topic}
            onChange={(e) =>
              setNewProblem({ ...newProblem, topic: e.target.value })
            }
          />
          <input
  type="text"
  placeholder="Company"
  value={newProblem.company}
  onChange={(e) =>
    setNewProblem({
      ...newProblem,
      company: e.target.value,
    })
  }
/>
<input
  type="text"
  placeholder="LeetCode URL"
  value={newProblem.leetcodeUrl}
  onChange={(e) =>
    setNewProblem({
      ...newProblem,
      leetcodeUrl: e.target.value,
    })
  }
/>

          <input
            type="number"
            placeholder="Confidence %"
            value={newProblem.confidencePercentage}
            onChange={(e) =>
              setNewProblem({
                ...newProblem,
                confidencePercentage: e.target.value,
              })
            }
          />
          <textarea
  placeholder="Notes..."
  value={newProblem.notes}
  onChange={(e) =>
    setNewProblem({
      ...newProblem,
      notes: e.target.value,
    })
  }
/>

          <button onClick={addProblem}>
            {editingId ? "Update Problem" : "Add Problem"}
          </button>
        </div>

        {/* STATS BOX */}
        <div className="form-card">
          <h2>Preparation Stats</h2>
          <p>🎯 Avg Confidence: {avgConfidence}%</p>
          <p>📚 Revision Queue: {revisionQueue.length}</p>
          <p>🔥 Hard Problems: {hardCount}</p>
        </div>
      </div>

      {/* REVISION QUEUE */}
      <div className="table-card">
        <h2>Revision Queue</h2>

        {revisionQueue.length > 0 ? (
          <table>
            <thead>
              <tr>
                <th>Title</th>
                <th>Topic</th>
                <th>Confidence</th>
              </tr>
            </thead>
            <tbody>
              {revisionQueue.map((p) => (
                <tr key={p.id}>
                  <td>{p.title}</td>
                  <td>{p.topic}</td>
                  <td>
  <span className="confidence-badge">
    {p.confidencePercentage}%
  </span>
</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No revisions pending 🎉</p>
        )}
      </div>

      {/* SEARCH */}
      <div className="table-card">
        <h2>Problem Journal</h2>

        <input
          type="text"
          placeholder="🔍 Search Problem..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") searchProblem();
          }}
        />

        <br />
        <br />

        <table>
          <thead>
            <tr>
              <th>Title</th>
              <th>LC Diff</th>
              <th>My Diff</th>
              <th>Topic</th>
              <th>Confidence</th>
              <th>Revision</th>
              <th>Status</th>
              <th>Notes</th>
<th>LeetCode</th>
<th>Actions</th>
            </tr>
          </thead>

         <tbody>
  {filteredProblems.map((p) => (
    <tr key={p.id}>
      <td>{p.title}</td>
      <td>{p.difficulty}</td>
      <td>{p.personalDifficulty}</td>
      <td>{p.topic}</td>
      <td>{p.confidencePercentage}%</td>
<td>{p.revisionCount || 0}</td>
<td>{p.revisionNeeded ? "⚠️ Due" : "✅ Good"}</td>

      <td>
        <button
          className="notes-btn"
          onClick={() =>
            alert(p.notes || "No notes available")
          }
        >
          Notes
        </button>
      </td>
 <td>
  <a
    href={p.leetcodeUrl}
    target="_blank"
    rel="noreferrer"
  >
    Open
  </a>
</td>

      <td>
  <div className="action-buttons">
    <button
      className="edit-btn"
      onClick={() => editProblem(p)}
    >
      Edit
    </button>

    <button
      className="delete-btn"
      onClick={() => deleteProblem(p.id)}
    >
      Delete
    </button>
  </div>
</td>
    </tr>
  ))}
</tbody>
        </table>
      </div>
    </div>
  );
}

export default App;