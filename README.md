# ViralBot - Automated Social Media Content Generator

Automated video content generation and publishing system for YouTube Shorts using 100% free-tier services.

## Free Tier Plan

### Monthly Capacity (Free Tier)
- **30-60 videos/day** (~900-1800 videos/month)
- **$0 monthly cost** if within limits

### Services Used

#### AI Content Generation
- **Google Gemini API** (Free tier: 60 requests/minute)
  - Sign up: https://makersuite.google.com/app/apikey
  - Cost: FREE

#### Text-to-Speech
- **Google Cloud TTS** (Free: 1M characters/month = ~33k chars/day)
  - Sign up: https://cloud.google.com/text-to-speech
  - Cost: FREE for first 1M characters

#### Media Assets
- **Pexels API** (Free: 200 requests/hour)
  - Sign up: https://www.pexels.com/api/
  - Cost: FREE

#### Video Processing
- **FFmpeg** (Open source)
  - Install: `brew install ffmpeg` (macOS) or `apt-get install ffmpeg` (Linux)
  - Cost: FREE

#### Social Media APIs
- **YouTube Data API v3** (Free: 10,000 units/day)
  - Setup: https://console.cloud.google.com/
  - Cost: FREE

#### Hosting Options
- **Railway** (Free tier: 500 hours/month)
- **Render** (Free tier available)
- **AWS Lambda** (Free: 1M requests/month)

## Setup Instructions

### 1. Prerequisites
```bash
# Install Java 21
brew install openjdk@21

# Install FFmpeg
brew install ffmpeg

# Install Maven
brew install maven
```

### 2. Get API Keys

#### Google Gemini API
1. Visit https://makersuite.google.com/app/apikey
2. Create API key
3. Copy key for configuration

#### Google Cloud TTS
1. Visit https://console.cloud.google.com/
2. Enable Text-to-Speech API
3. Create service account
4. Download JSON credentials file

#### Pexels API
1. Visit https://www.pexels.com/api/
2. Sign up and get API key

#### YouTube API
1. Visit https://console.cloud.google.com/
2. Enable YouTube Data API v3
3. Create OAuth 2.0 credentials
4. Download credentials JSON

### 3. Configuration

Create `.env` file or set environment variables:

```bash
# AI Content Generation
export GEMINI_API_KEY=your_gemini_api_key

# Text-to-Speech
export GOOGLE_CREDENTIALS_PATH=/path/to/google-credentials.json

# Media Assets
export PEXELS_API_KEY=your_pexels_api_key

# YouTube
export YOUTUBE_CLIENT_ID=your_youtube_client_id
export YOUTUBE_CLIENT_SECRET=your_youtube_client_secret
export YOUTUBE_CREDENTIALS_PATH=/path/to/youtube-credentials.json
```

### 4. Build and Run

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Or run JAR
java -jar target/viralbot-1.0.0.jar
```

## Usage

### Manual Execution
```bash
# Process all channels
curl -X POST http://localhost:8080/api/process-all

# Process specific channel
curl -X POST http://localhost:8080/api/process/tech
```

### Scheduled Execution
The application runs automatically based on cron schedule in `application.properties`:
```properties
viralbot.schedule.cron=0 0 9 * * ?  # Daily at 9 AM
```

## Project Structure

```
ViralBot/
├── src/main/java/com/viralbot/
│   ├── ViralBotApplication.java          # Main application
│   ├── config/
│   │   └── ViralBotConfig.java           # Configuration properties
│   ├── model/
│   │   └── Video.java                    # Video data model
│   ├── service/
│   │   ├── ViralBotOrchestrator.java     # Main orchestrator
│   │   ├── ContentGeneratorService.java  # AI content generation
│   │   ├── TextToSpeechService.java      # TTS service
│   │   ├── MediaService.java             # Image fetching
│   │   └── VideoCreationService.java     # FFmpeg video creation
│   └── platform/
│       └── YouTubeService.java           # YouTube Shorts upload
└── src/main/resources/
    └── application.properties            # Configuration
```

## Channels Configuration

Edit `application.properties` to add/modify channels:

```properties
viralbot.channels=tech,motivation,facts
viralbot.channel.tech.description=Technology tips and tutorials
viralbot.channel.motivation.description=Daily motivation and inspiration
viralbot.channel.facts.description=Interesting facts and trivia
```

## Cost Monitoring

### Daily Limits (Free Tier)
- Gemini API: 60 requests/min (unlimited daily)
- Google TTS: ~33k characters/day
- Pexels: 200 requests/hour
- YouTube: 10,000 units/day (~100 uploads)

### Staying Within Free Tier
- Generate 30-60 videos/day
- Each video uses:
  - 1 Gemini API call
  - ~500-1000 TTS characters
  - 5 Pexels image requests
  - 1 YouTube upload (~50 units)

## Deployment

### Railway
```bash
# Install Railway CLI
npm install -g @railway/cli

# Login and deploy
railway login
railway init
railway up
```

### AWS Lambda
```bash
# Package application
mvn clean package

# Deploy using AWS SAM or Serverless Framework
```

## Troubleshooting

### FFmpeg not found
```bash
# macOS
brew install ffmpeg

# Linux
sudo apt-get install ffmpeg
```

### API rate limits
- Implement exponential backoff
- Monitor usage in respective dashboards
- Adjust schedule frequency

### Video upload failures
- Check API credentials
- Verify video format (MP4, H.264)
- Ensure video meets platform requirements

## License

MIT License
