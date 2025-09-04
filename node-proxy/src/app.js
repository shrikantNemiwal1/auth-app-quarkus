const express = require("express");
const session = require("express-session");
const axios = require("axios");
const cors = require("cors");

const app = express();

// CORS for React frontend
app.use(
  cors({
    origin: "http://localhost:4173",
    credentials: true,
  })
);

app.use(express.json());

// Session configuration - 15 minute timeout with cross-tab sharing
app.use(
  session({
    secret: "your-secret-key",
    resave: false,
    saveUninitialized: false,
    cookie: {
      httpOnly: true,
      maxAge: 15 * 60 * 1000, // 15 minutes
      secure: false, // set to true in production with HTTPS
    },
    rolling: true, // Reset expiration on each request
  })
);

const QUARKUS_URL = "http://127.0.0.1:8080";

// Middleware to refresh session on activity
const refreshSession = (req, res, next) => {
  if (req.session?.user) {
    req.session.touch();
  }
  next();
};

// Login endpoint
app.post("/api/auth/login", async (req, res) => {
  try {
    const response = await axios.post(`${QUARKUS_URL}/auth/login`, req.body);
    const { token, userId, emailVerified, message } = response.data;

    // Store user info in session
    req.session.user = {
      userId,
      emailVerified,
      email: req.body.email,
      token,
    };

    res.json({
      success: true,
      emailVerified,
      message,
      userId,
    });
  } catch (error) {
    res.status(error.response?.status || 500).json({
      success: false,
      message: error.response?.data?.message || "Login failed",
    });
  }
});

// Logout endpoint
app.post("/api/auth/logout", (req, res) => {
  req.session.destroy((err) => {
    if (err) {
      return res.status(500).json({ success: false, message: "Logout failed" });
    }
    res.clearCookie("connect.sid");
    res.json({ success: true, message: "Logged out successfully" });
  });
});

// Session check endpoint
app.get("/api/auth/session", refreshSession, (req, res) => {
  if (req.session?.user) {
    res.json({
      authenticated: true,
      user: {
        userId: req.session.user.userId,
        emailVerified: req.session.user.emailVerified,
        email: req.session.user.email,
      },
    });
  } else {
    res.status(401).json({ authenticated: false });
  }
});

// User registration - proxy to Quarkus
app.post("/api/users", async (req, res) => {
  try {
    const response = await axios.post(`${QUARKUS_URL}/users`, req.body);
    res.status(response.status).json(response.data);
  } catch (error) {
    console.log(error);
    res.status(error.response?.status || 500).json(
      error.response?.data || {
        success: false,
        message: "Registration failed",
      }
    );
  }
});

// Email verification - proxy to Quarkus
app.get("/api/users/verify", async (req, res) => {
  try {
    const response = await axios.get(`${QUARKUS_URL}/users/verify`, {
      params: req.query,
    });

    // Update session if user is logged in
    if (req.session?.user) {
      req.session.user.emailVerified = true;
    }

    res.json(response.data);
  } catch (error) {
    res.status(error.response?.status || 500).json(
      error.response?.data || {
        success: false,
        message: "Email verification failed",
      }
    );
  }
});

// Dashboard endpoint - shows email verification status
app.get("/api/dashboard", refreshSession, (req, res) => {
  if (!req.session?.user) {
    return res.status(401).json({
      authenticated: false,
      message: "Please login to access the portal",
    });
  }

  const { user } = req.session;
  const message = user.emailVerified
    ? "Your email is validated. You can access the portal"
    : "You need to validate your email to access the portal";

  res.json({
    success: true,
    message,
    emailVerified: user.emailVerified,
    userId: user.userId,
    email: user.email,
  });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Node.js server running on port ${PORT}`);
});
