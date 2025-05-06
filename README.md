
# 🕳️ Real-Time Pothole Detection and Mapping

A hybrid mobile-IoT project that detects road potholes in real-time using sensor data, maps the locations using GPS, and visualizes them through an Android interface.

---

## 🧠 Overview

This project combines **mobile sensing**, **deep learning (LSTM)**, and **Flask-based server communication** to detect potholes based on motion sensor data and map them to actual road coordinates.

---

## 🧰 Tech Stack

- **Android App**: Java, Location APIs, Google Maps
- **Server**: Flask (Python), Ngrok tunnel
- **Model**: LSTM (trained on multi-sensor data)
- **Data**: Accelerometer, gyroscope, and magnetometer readings labeled as `Pothole` and `Non-Pothole`
- **Deployment**: Local with Ngrok for public API exposure

---


## 📱 Android App Features

- Real-time location tracking using GPS
- Collects sensor data (accelerometer, gyroscope, magnetometer)
- Sends live data to the Flask backend
- Displays pothole positions using Google Maps

**Key Files:**
- `MainActivity.java` – UI + sensor listener
- `CurrentLocationListener.java` – GPS updates
- `MapsActivity.java` – Google Maps integration
- `AndroidManifest.xml` – permissions and services

---

## 🧪 ML Model

- **Architecture**: LSTM (Long Short-Term Memory)
- **Input**: Multivariate time series from:
  - Accelerometer (X, Y, Z)
  - Gyroscope (X, Y, Z)
  - Magnetometer (X, Y, Z)
- **Output**: Binary classification (Pothole / Non-Pothole)
- **Training File**: `LSTM_model.ipynb`
- **Model File**: `lstm_model.h5`

---

## 🌐 Flask API

- Receives sensor data via POST
- Loads `lstm_model.h5` and performs inference
- Returns class label and probability

**Start API:**

```bash
cd python/
python flask_app.ipynb  # or convert it to .py for deployment
```

---

## 🔧 Setup Instructions

### ✅ Android
1. Open `Pothole-detection-main` in Android Studio.
2. Run the app on a mobile device with GPS + motion sensors.
3. Ensure internet access for API connectivity.

### ✅ Python/Flask
```bash
cd python/
pip install -r requirements.txt  # if using a requirements file
python flask_app.ipynb
```

---

## 📍 Output

- Real-time pothole classification
- Pothole map with precise GPS tagging
- Live updates sent from mobile to backend

---

## 📌 Future Enhancements

- Add cloud storage for pothole locations
- Model retraining using real-time feedback
- Improve UI with user reports and severity levels
- Integration with traffic authority systems

---

## 📜 License

MIT License — feel free to modify and use.

---
