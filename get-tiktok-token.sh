#!/bin/bash

# TikTok OAuth Token Generator with PKCE
# This script helps you get a TikTok access token

CLIENT_KEY="awq72bbyufjdwylo"
CLIENT_SECRET="V3yXiNxIx1pnGJ9o4oajfvbwTGp0jeXT"
REDIRECT_URI="http://localhost:8080/tiktok/callback"

echo "=========================================="
echo "TikTok Access Token Generator"
echo "=========================================="
echo ""

# Generate code_verifier (random 43-128 character string)
CODE_VERIFIER=$(openssl rand -base64 64 | tr -d '=+/' | cut -c1-128)

# Generate code_challenge (SHA256 hash of code_verifier, base64url encoded)
CODE_CHALLENGE=$(echo -n "$CODE_VERIFIER" | openssl dgst -sha256 -binary | openssl base64 | tr -d '=' | tr '+/' '-_')

echo "Generated PKCE parameters:"
echo "Code Verifier: $CODE_VERIFIER"
echo "Code Challenge: $CODE_CHALLENGE"
echo ""

# Step 1: Generate authorization URL with PKCE
AUTH_URL="https://www.tiktok.com/v2/auth/authorize/?client_key=${CLIENT_KEY}&scope=user.info.basic,video.upload,video.publish&response_type=code&redirect_uri=${REDIRECT_URI}&state=viralbot123&code_challenge=${CODE_CHALLENGE}&code_challenge_method=S256"

echo "STEP 1: Get Authorization Code"
echo "-------------------------------"
echo "1. Open this URL in your browser:"
echo ""
echo "$AUTH_URL"
echo ""
echo "2. Log in to TikTok and authorize the app"
echo "3. You'll be redirected to: ${REDIRECT_URI}?code=XXXXX&state=viralbot123"
echo "4. Copy the 'code' parameter from the URL"
echo ""
read -p "Enter the authorization code: " AUTH_CODE

if [ -z "$AUTH_CODE" ]; then
    echo "Error: No authorization code provided"
    exit 1
fi

echo ""
echo "STEP 2: Exchange Code for Access Token"
echo "---------------------------------------"

# Step 2: Exchange authorization code for access token with code_verifier
RESPONSE=$(curl -s -X POST "https://open.tiktokapis.com/v2/oauth/token/" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_key=${CLIENT_KEY}" \
  -d "client_secret=${CLIENT_SECRET}" \
  -d "code=${AUTH_CODE}" \
  -d "grant_type=authorization_code" \
  -d "redirect_uri=${REDIRECT_URI}" \
  -d "code_verifier=${CODE_VERIFIER}")

echo "Response from TikTok:"
echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"
echo ""

# Extract access token
ACCESS_TOKEN=$(echo "$RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('access_token', ''))" 2>/dev/null)
REFRESH_TOKEN=$(echo "$RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('refresh_token', ''))" 2>/dev/null)

if [ -n "$ACCESS_TOKEN" ]; then
    echo "=========================================="
    echo "SUCCESS! Your TikTok Access Token:"
    echo "=========================================="
    echo ""
    echo "Access Token: $ACCESS_TOKEN"
    echo "Refresh Token: $REFRESH_TOKEN"
    echo ""
    echo "Add this to your environment:"
    echo "export TIKTOK_ACCESS_TOKEN=\"$ACCESS_TOKEN\""
    echo ""
    echo "Or add to ~/.zshrc:"
    echo "echo 'export TIKTOK_ACCESS_TOKEN=\"$ACCESS_TOKEN\"' >> ~/.zshrc"
    echo "source ~/.zshrc"
    echo ""
    echo "Note: Access token expires in 24 hours. Use refresh token to get a new one."
else
    echo "=========================================="
    echo "ERROR: Failed to get access token"
    echo "=========================================="
    echo ""
    echo "Common issues:"
    echo "1. Authorization code already used (codes are single-use)"
    echo "2. Authorization code expired (valid for 10 minutes)"
    echo "3. Redirect URI mismatch (must match exactly)"
    echo "4. App not approved by TikTok yet"
    echo "5. PKCE verification failed"
    echo ""
    echo "Try again with a fresh authorization code."
fi
