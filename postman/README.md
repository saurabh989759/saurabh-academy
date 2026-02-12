# Postman Collection - Step-by-Step Guide

## 📋 Complete Step-by-Step Instructions

### STEP 1️⃣: Import Collection
1. Open Postman
2. Click **Import** button (top left)
3. Select `Academy.postman_collection.json` file
4. Click **Import**
5. Collection "Academy Backend API" will appear in your workspace

### STEP 2️⃣: Open Postman Console (Important!)
1. Go to **View** → **Show Postman Console** (or press `Ctrl+Alt+C` / `Cmd+Alt+C`)
2. Keep console open to see all messages and verify token is working

### STEP 3️⃣: Run Login Request (REQUIRED FIRST!)
1. Expand **Authentication** folder
2. Click on **Login** request
3. Click **Send** button
4. **Check Console** - You should see:
   ```
   ✅ STEP 1 COMPLETE: JWT Token saved successfully!
   📝 Token (first 30 chars): eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   💾 Token saved to variable: jwt_token
   ```
5. **Check Tests tab** - Should show:
   - ✅ Token saved to collection variable
   - ✅ Token is valid JWT format

**Default Credentials:**
- Username: `admin@academy.com`
- Password: `password123`

### STEP 4️⃣: Use Any API Request
1. Go to any folder (e.g., **Students**, **Batches**, **Classes**, etc.)
2. Click on any request (e.g., **Get All Students**)
3. Click **Send** button
4. **Check Console** - You should see:
   ```
   ✅ STEP 2 COMPLETE: JWT token added to Authorization header
   📝 Header: Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ✅ Verification: Authorization header is set
   ```
5. **Check Response** - Should return data (not 401/403 error)

### STEP 5️⃣: Verify Token is Working
- ✅ **Success**: Response status 200 with data
- ❌ **Error 401/403**: Token missing or expired
  - Solution: Run **Login** request again
  - Check console for detailed error messages

## 🔍 How to Verify Token is Being Sent

### Method 1: Check Postman Console
1. Open Console (View → Show Postman Console)
2. Look for: `✅ STEP 2 COMPLETE: JWT token added to Authorization header`
3. Should show: `Authorization: Bearer {token}...`

### Method 2: Check Request Headers
1. Click on any request
2. Go to **Headers** tab
3. Look for **Authorization** header
4. Should show: `Bearer {your-token}`

### Method 3: Check Collection Variables
1. Click on collection name (Academy Backend API)
2. Click **Variables** tab
3. Look for `jwt_token` variable
4. Should contain your JWT token

## 🐛 Troubleshooting

### Problem: Getting 403 Forbidden Error
**Solution:**
1. Run **Login** request again
2. Check console for "✅ JWT Token saved successfully!"
3. Verify token exists in collection variables
4. Try the API request again

### Problem: Getting 401 Unauthorized Error
**Solution:**
1. Token might be expired
2. Run **Login** request to get fresh token
3. Check console for any error messages

### Problem: No Token in Console
**Solution:**
1. Check Login response - should be status 200
2. Check response body - should contain `{"token": "...", "type": "Bearer"}`
3. Verify credentials are correct
4. Check Postman Console for error messages

### Problem: Authorization Header Not Showing
**Solution:**
1. Make sure you ran **Login** request first
2. Check collection variable `jwt_token` is set
3. Check console for pre-request script messages
4. Try running the request again

## 📝 Testing Checklist

Use this checklist to verify everything works:

- [ ] Collection imported successfully
- [ ] Postman Console is open
- [ ] Login request returns status 200
- [ ] Console shows "✅ STEP 1 COMPLETE: JWT Token saved successfully!"
- [ ] Collection variable `jwt_token` is set
- [ ] Any API request shows "✅ STEP 2 COMPLETE: JWT token added to Authorization header"
- [ ] API requests return status 200 (not 401/403)
- [ ] Authorization header is visible in request headers

## 🎯 Quick Test Sequence

Test the collection in this order:

1. **Authentication → Login**
   - Should return 200
   - Token saved automatically

2. **Students → Get All Students**
   - Should return 200 with student list
   - Token automatically included

3. **Students → Get Student by ID** (use ID: 1)
   - Should return 200 with student details

4. **Batches → Get All Batches**
   - Should return 200 with batch list

5. **Classes → Get All Classes**
   - Should return 200 with class list

If all these work, the collection is configured correctly! ✅

## 💡 Tips

- **Always run Login first** before using any other API
- **Keep Console open** to see what's happening
- **Check Tests tab** after each request for validation
- **Token expires** - if you get 401/403, run Login again
- **Use Collection Runner** to test multiple requests in sequence

## 🔐 Security Notes

- Token is stored in collection variables (local to your Postman)
- Token is automatically added to Authorization header
- Token format: `Bearer {jwt-token}`
- Token expires after 24 hours (default)
- Public endpoints (`/api/auth/**`, `/actuator/**`) don't require token

