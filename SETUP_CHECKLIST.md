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

## 6. Setup TikTok API (FREE tier available)

**WARNING**: TikTok API access is complex and requires approval. For automated posting, consider these alternatives:
- Manual upload (generate videos, upload manually)
- Use unofficial libraries (against TikTok TOS, risk of ban)
- Wait for TikTok API approval (can take weeks/months)

### Option A: Apply for Official TikTok API (Recommended but slow)

1. **Create TikTok Developer Account**
   - Go to: https://developers.tiktok.com/
   - Click "Register" and sign up
   - Verify your email

2. **Create an App**
   - Go to "My Apps" → "Create App"
   - Fill in app details:
     - App name: "ViralBot"
     - Category: "Content Creation"
     - Description: "Automated video content generator"
   - Submit for review (can take 1-4 weeks)

3. **Get Client Key and Secret**
   - Once approved, go to your app dashboard
   - Copy "Client Key" and "Client Secret"

4. **Generate Access Token (OAuth 2.0 Flow)**
   
   TikTok uses OAuth 2.0, which requires user authorization:
   
   **Step 1: Get Authorization Code**
   ```bash
   # Open this URL in browser (replace YOUR_CLIENT_KEY and YOUR_REDIRECT_URI)
   https://www.tiktok.com/v2/auth/authorize/?client_key=YOUR_CLIENT_KEY&scope=user.info.basic,video.upload&response_type=code&redirect_uri=YOUR_REDIRECT_URI&state=random_string
   ```
   - User logs in and authorizes
   - TikTok redirects to your redirect_uri with `code` parameter
   - Copy the `code` from URL
   
   **Step 2: Exchange Code for Access Token**
   ```bash
   curl -X POST "https://open.tiktokapis.com/v2/oauth/token/" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "client_key=YOUR_CLIENT_KEY" \
     -d "client_secret=YOUR_CLIENT_SECRET" \
     -d "code=AUTHORIZATION_CODE_FROM_STEP_1" \
     -d "grant_type=authorization_code" \
     -d "redirect_uri=YOUR_REDIRECT_URI"
   ```
   
   Response:
   ```json
   {
     "access_token": "act.example...",
     "refresh_token": "rft.example...",
     "expires_in": 86400,
     "token_type": "Bearer"
   }
   ```
   
   **Step 3: Refresh Token (when expired)**
   ```bash
   curl -X POST "https://open.tiktokapis.com/v2/oauth/token/" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "client_key=YOUR_CLIENT_KEY" \
     -d "client_secret=YOUR_CLIENT_SECRET" \
     -d "grant_type=refresh_token" \
     -d "refresh_token=YOUR_REFRESH_TOKEN"
   ```

5. **Set Environment Variables**
   ```bash
   export TIKTOK_CLIENT_KEY="your_client_key"
   export TIKTOK_CLIENT_SECRET="your_client_secret"
   export TIKTOK_ACCESS_TOKEN="your_access_token"
   ```

### Option B: Skip TikTok API (Easiest)

**Just disable TikTok uploads and upload manually:**

1. Comment out TikTok upload code in `ViralBotOrchestrator.java`
2. Videos will be saved to `/tmp/viralbot/`
3. Manually upload to TikTok from your phone/computer

### Option C: Use TikTok Web Upload (No API needed)

**Use Selenium/Playwright to automate browser upload:**
- Automates the TikTok web interface
- No API approval needed
- Against TikTok TOS (use at your own risk)
- Can result in account ban

**Recommendation**: Start with **Option B** (manual upload) until TikTok API is approved.

---

## 7. Create YouTube & TikTok Accounts
- **YouTube**: Create account at youtube.com (if you don't have one)
- **TikTok**: Create account at tiktok.com (if you don't have one)

---

## 8. Start PostgreSQL
```bash
docker-compose up -d
```

---

## 9. Build Frontend
```bash
bash start.sh
```

---

## 10. Run Application
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

# TikTok
export TIKTOK_CLIENT_KEY="your_client_key"
export TIKTOK_CLIENT_SECRET="your_client_secret"
export TIKTOK_ACCESS_TOKEN="your_access_token"
```

Then run: `source ~/.zshrc`

---

## Quick Test

Once everything is set up:
1. Go to http://localhost:8080
2. Click "GENERATE NOW" for a channel
3. Check the logs to see progress
4. Videos will appear in the Videos page with YouTube/TikTok links

---

## Cost: FREE if you stay within limits (billing account required)
- Gemini: FREE unlimited (60 req/min)
- Google TTS: FREE 1M chars/month (~40 videos/day), then $4/1M chars
- Pexels: FREE 200 req/hour
- YouTube: FREE 10,000 units/day
- TikTok: FREE tier

**Total: $0/month if you generate ≤40 videos/day**
