# ViralBot Setup Checklist

Complete these steps in order to get ViralBot running:

## 1. Install FFmpeg
```bash
brew install ffmpeg
```
Verify: `ffmpeg -version`

---

## 2. Get Google Gemini API Key (FREE)
1. Go to: https://makersuite.google.com/app/apikey
2. Sign in with Google account
3. Click "Create API Key"
4. Copy the key
5. Set environment variable:
   ```bash
   export GEMINI_API_KEY="your_key_here"
   ```

---

## 3. Get Google Cloud TTS Credentials (FREE - 1M chars/month, but requires billing)
1. Go to: https://console.cloud.google.com/
2. Create new project or select existing
3. **Add billing account** (required even for free tier)
   - Go to "Billing" → "Link a billing account"
   - Add credit card (won't be charged if you stay under 1M chars/month)
4. Enable "Cloud Text-to-Speech API"
   - Search for "Text-to-Speech API" in the search bar
   - Click "Enable"
5. Create Service Account:
   - Go to "IAM & Admin" → "Service Accounts"
   - Click "Create Service Account"
   - Name: `viralbot-tts`
   - Click "Create and Continue"
   - Role: Select "Basic" → "Editor" (or "Cloud Text-to-Speech User")
   - Click "Continue" → "Done"
6. Create JSON Key:
   - Click on the service account you just created
   - Go to "Keys" tab
   - Click "Add Key" → "Create new key"
   - Select "JSON"
   - Click "Create" (JSON file downloads automatically)
7. Save the downloaded JSON file to: `/path/to/google-tts-credentials.json`
8. Set environment variable:
   ```bash
   export GOOGLE_CREDENTIALS_PATH="/path/to/google-tts-credentials.json"
   ```

**Cost:** FREE for first 1M characters/month (~1,250-2,000 videos), then $4/1M chars

---

## 4. Get Pexels API Key (FREE - 200 requests/hour)
1. Go to: https://www.pexels.com/api/
2. Sign up for free account
3. Click "Your API Key"
4. Copy the key
5. Set environment variable:
   ```bash
   export PEXELS_API_KEY="your_key_here"
   ```

---

## 5. Setup YouTube API (FREE - 10,000 units/day)
1. Go to: https://console.cloud.google.com/
2. Enable "YouTube Data API v3"
3. Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client ID"
4. Application type: "Desktop app"
5. Download JSON file
6. Save file to: `/path/to/youtube-credentials.json`
7. Set environment variables:
   ```bash
   export YOUTUBE_CLIENT_ID="your_client_id"
   export YOUTUBE_CLIENT_SECRET="your_client_secret"
   export YOUTUBE_CREDENTIALS_PATH="/path/to/youtube-credentials.json"
   ```

---

## 6. Start PostgreSQL
```bash
docker-compose up -d
```

---

## 7. Build Frontend
```bash
bash start.sh
```

---

## 8. Run Application
1. Open IntelliJ IDEA
2. Open `ViralBotApplication.java`
3. Click green ▶ button
4. Go to: http://localhost:8080

---

## Environment Variables Summary

Add these to your `~/.zshrc` or `~/.bash_profile`:

```bash
# Google Gemini
export GEMINI_API_KEY="your_key_here"

# Google Cloud TTS
export GOOGLE_CREDENTIALS_PATH="/path/to/google-tts-credentials.json"

# Pexels
export PEXELS_API_KEY="your_key_here"

# YouTube
export YOUTUBE_CLIENT_ID="your_client_id"
export YOUTUBE_CLIENT_SECRET="your_client_secret"
export YOUTUBE_CREDENTIALS_PATH="/path/to/youtube-credentials.json"
```

Then run: `source ~/.zshrc`

---

## Quick Test

Once everything is set up:
1. Go to http://localhost:8080
2. Click "GENERATE NOW" for a channel
3. Check the logs to see progress
4. Videos will appear in the Videos page with YouTube links

---

## Cost: FREE if you stay within limits (billing account required)
- Gemini: FREE unlimited (60 req/min)
- Google TTS: FREE 1M chars/month (~40 videos/day), then $4/1M chars
- Pexels: FREE 200 req/hour
- YouTube: FREE 10,000 units/day

**Total: $0/month if you generate ≤40 videos/day**
