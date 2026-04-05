# Standard Operating Procedure (SOP): Learning Security

This is your interactive **hacker playbook**. We will act as a regular user trying to access the platform.

**Before you start:**
1. You must have your server running (see `README.md`).
2. Open a new Terminal (or Command Prompt) window. This is where you will type commands while your server runs in the first window.

---

## 🎒 Lesson 1: The "No VIP Pass" Test (Authentication)

Let's try to view some protected data without a digital "VIP pass" (a JWT token).

**Your Goal:** Get to the `/api/protected` webpage.

**Action:**
Type this into your terminal and press Enter:
```bash
curl -v http://localhost:8080/api/protected
```

**What Happened?**
You should see something like:
`HTTP/1.1 403 Forbidden`

**Why?**
The server says, "I don't know who you are." This is **Authentication** at work. Every request MUST have an identity attached to it.

---

## 🎟️ Lesson 2: The VIP Pass (JWT Token)

To get in, we need a VIP pass. We call this a **JSON Web Token (JWT)**.
Instead of building a full login page, we built a **Debug Endpoint** that shows you your token if you provide one. For learning, let's create a fake token.

Normally, the server *gives* you a token after you type in your username and password.

1. **Action:** Go to [jwt.io](https://jwt.io/).
2. Scroll down to the **Debugger** section.
3. In the **PAYLOAD** section (the pink box), change it to look like this:
   ```json
   {
     "sub": "student_user",
     "auth": "ROLE_USER"
   }
   ```
4. In the **VERIFY SIGNATURE** section (the blue box), paste this EXACT secret (the same one we used to start the server!):
   `413F4428472B4B6250655368566D5970337336763979244226452948404D6351`
5. On the left side (the encoded section), copy the long string of letters and numbers. This is your VIP Pass!

**Let's try to get in again.**

**Action:** Replace `YOUR_COPIED_TOKEN_HERE` with the actual token you copied from jwt.io.
```bash
curl -v -H "Authorization: Bearer YOUR_COPIED_TOKEN_HERE" http://localhost:8080/api/protected
```

**What Happened?**
You should see:
`HTTP/1.1 200 OK`
`Protected Data`

**Success!** The server checked the signature of your token, verified it wasn't tampered with, and let you in.

---

## 👑 Lesson 3: "I want to be the Boss!" (Authorization / RBAC)

There is a special page for Admins at `/api/admin`. You currently have the role `ROLE_USER`.

**Action:** Let's try to access the Admin page using your current token.
```bash
curl -v -H "Authorization: Bearer YOUR_COPIED_TOKEN_HERE" http://localhost:8080/api/admin
```

**What Happened?**
You should see:
`HTTP/1.1 403 Forbidden`

**Why?**
You are *Authenticated* (we know who you are), but you are not *Authorized* (you don't have permission). The code specifically looks for the `ROLE_ADMIN` badge.

**The Fix:**
1. Go back to [jwt.io](https://jwt.io/).
2. Change `"auth": "ROLE_USER"` to `"auth": "ROLE_ADMIN"`.
3. Copy the NEW token.
4. Try the `curl` command again with the new token. It will say `200 OK`!

---

## 🚦 Lesson 4: Stop Spamming! (Rate Limiting)

Hackers often build bots that click "refresh" thousands of times a second to crash your website.
Our platform has a built-in **Rate Limiter** (using Bucket4j) set to only allow 10 requests per minute by default. Let's trigger it.

**Action:**
Run this command over and over again quickly (press the "Up" arrow on your keyboard, then "Enter"):
```bash
curl -v http://localhost:8080/debug/security-context
```

**What Happened?**
The first 10 times, you will see `HTTP/1.1 200 OK`.
On the 11th try, you will see:
`HTTP/1.1 429 Too Many Requests`
`Too many requests`

**Why?**
The system put you in "time out" to protect the server. It will automatically let you back in after 60 seconds.

---

## 🕵️ Lesson 5: Looking Under the Hood (Observability)

Want to see exactly what the server sees when you make a request?
We created a debug page to show you your "Security Context" (your digital ID card).

**Action:**
```bash
curl -v -H "Authorization: Bearer YOUR_COPIED_TOKEN_HERE" http://localhost:8080/debug/security-context
```

**What Happened?**
The server will print out a JSON response showing:
- Your username (`principal`)
- Your roles (`authorities`)
- Whether you are officially logged in (`isAuthenticated: true`)

This is incredibly useful for developers trying to debug why a user is getting a `403 Forbidden` error!

---

## 🎉 Congratulations!

You have just simulated:
1. **Unauthenticated access blocks**
2. **JWT Token generation & validation**
3. **Role-Based Access Control (RBAC)**
4. **API Rate Limiting (DDoS protection)**
5. **Security Observability**

To experiment further, go into `application.yml`, change settings, and see how the platform behaves differently!