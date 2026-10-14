# 🧬 BioGenome – Scientific Research Frontend

> **Decoupled Frontend Interface for BioGenome Platform**  
> *Clean, Modern, Restrained Scientific Theme for Genomics & DSA Demonstration*

---

## 📌 Architecture Overview

The frontend is completely decoupled from the Java backend:

```
BioGenome_Web/
├── backend/ (Root Java Maven project: src/main/java)
│   └── Comprises all pure DSA algorithm implementations & Spring Boot REST API
└── frontend/
    ├── index.html        # Main scientific research UI
    ├── css/style.css     # Clean, restrained scientific theme
    ├── js/api.js         # API client with automatic offline educational fallback
    ├── js/visualizers.js # Interactive step-by-step educational visualizers
    └── js/app.js         # Navigation, forms & analysis controllers
```

- **Primary Source of Truth**: The Java DSA algorithms in `com.biogenome.*` remain the ultimate source of truth.
- **API Protocol**: The frontend communicates via standard HTTP REST requests to `http://localhost:8080/api/v1/...`.
- **Offline / Standalone Resilience**: If the Java backend is not running, `api.js` automatically activates simulated educational datasets so the UI is 100% testable and operable in VS Code without crashing.

---

## 💻 How to Run the Frontend in VS Code

You can run the frontend independently using any of the following standard methods in Visual Studio Code:

### Option A: Using the VS Code Live Server Extension (Recommended)
1. Open the `BioGenome_Web` folder in VS Code.
2. Navigate to `frontend/index.html`.
3. Right-click `index.html` and select **"Open with Live Server"**.
4. The dashboard will automatically launch at `http://127.0.0.1:5500/frontend/index.html`.

### Option B: Using Python's Built-in HTTP Server
Open your terminal in VS Code and run:
```bash
cd frontend
python -m http.server 3000
```
Then visit: `http://localhost:3000`

### Option C: Direct File Inspection
Double-click `frontend/index.html` in your file explorer to open it in Google Chrome, Microsoft Edge, or Mozilla Firefox.

---

## 🔗 Connecting with the Java Backend
1. Start the Java backend in Eclipse (run `BioGenomeApplication.java`) or via Maven terminal:
   ```bash
   mvn spring-boot:run
   ```
2. The backend will bind to port `8080` with CORS enabled for all origins.
3. Open or refresh the frontend. The status badge in the top right will display:  
   🟢 **Backend Live (Port 8080)**.
